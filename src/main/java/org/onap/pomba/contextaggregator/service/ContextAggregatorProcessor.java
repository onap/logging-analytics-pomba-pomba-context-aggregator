/*
 * ============LICENSE_START===================================================
 * Copyright (c) 2018 Amdocs
 * ============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=====================================================
 */
package org.onap.pomba.contextaggregator.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.onap.aai.event.api.EventConsumer;
import org.onap.aai.event.client.DMaaPEventPublisher;
import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;
import org.onap.pomba.contextaggregator.datatypes.AggregatedModels;
import org.onap.pomba.contextaggregator.datatypes.POAEvent;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorError;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorException;
import org.onap.pomba.contextaggregator.publisher.EventPublisherFactory;
import org.onap.pomba.contextaggregator.rest.RestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.aft.dme2.internal.gson.Gson;
import com.att.aft.dme2.internal.gson.GsonBuilder;
import com.att.aft.dme2.internal.gson.JsonSyntaxException;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import jline.internal.Log;

@Component
public class ContextAggregatorProcessor implements Callable<Void>{

    private EELFLogger logger = EELFManager.getInstance().getApplicationLogger();
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private ExecutorService executor = Executors.newFixedThreadPool(10);
	private int retriesRemaining;

	@Autowired
	private EventConsumer consumer;

	@Autowired
	private EventPublisherFactory publisherFactory;

	@Autowired
	List<ContextBuilder> contextBuilders;

	@Autowired
	EventHeaderConfig eventHeaderConfig;


	/**
	 * Parses the consumed event, retrieves model data from all context builders
	 * and publishes the aggregated models to DMaaP.
	 * @param payload
	 * @throws Exception
	 */
	public void process(String payload) throws Exception {

	    logger.debug("Consumed event: " + payload);
	    POAEvent event;
	    try {
	        event = parseEvent(payload);
	        logger.debug("Received POA event: " + event.toString());
        } catch (ContextAggregatorException e) {
            logger.error(ContextAggregatorError.INVALID_EVENT_RECEIVED.getMessage(e.getMessage()));
            // TODO: publish to error topic?
            return;
        }

        Map<String, String> retrievedModels = new HashMap<>();
        for (ContextBuilder builder : contextBuilders) {
            logger.debug("Retrieving model data for: " + builder.getContextName());
            String modelData= RestRequest.getModelData(builder, event);
            if (modelData == null) {
            	// If one of the Context builder return error, Aggregator will not publish the event
            	logger.info("Error returned from one of the Context builder, no event will be published.");
            	return;
            } else {
            	retrievedModels.put(builder.getContextName(), modelData);
            }
        }

        try {
            publishModels(new AggregatedModels(eventHeaderConfig, retrievedModels, event));
        } catch(ContextAggregatorException e) {
            logger.error(ContextAggregatorError.FAILED_TO_PUBLISH_RESULT.getMessage(e.getMessage()));
        }
	}

	@Override
    public Void call() throws Exception {
		while(true) {
			try {
				Iterable<String> consumed = consumer.consume();
				consumed.forEach((x)->executor.submit(new Callable<Void>() {
					@Override
                    public Void call() throws Exception {
						process(x);
						return null;
					}
				}));
			}catch(Exception e) {
                e.printStackTrace();
                throw e;
			}
		}
	}

	/**
	 * Parses, validates and returns a PAOEvent
	 * @param eventPayload
	 * @return
	 * @throws ContextAggregatorException
	 */
	private POAEvent parseEvent(String eventPayload) throws ContextAggregatorException {
	    POAEvent event = null;
	    try {
	        event = gson.fromJson(eventPayload, POAEvent.class);
        } catch (JsonSyntaxException e) {
            throw new ContextAggregatorException(ContextAggregatorError.JSON_PARSER_ERROR, e.getMessage());
        }
        event.validate();
        return event;
	}

	/**
	 * Publishes the aggregated models
	 * @param models
	 * @throws ContextAggregatorException
	 */
	private void publishModels(AggregatedModels models) throws ContextAggregatorException {
	    String payload = models.generateJsonPayload();
	    Log.debug("Publishing models: " + payload);
	    retriesRemaining = publisherFactory.getRetries();
	    publish(Arrays.asList(payload));
	}

	/**
	 * Publishes the given messages with a new EventPublisher instance.
	 * Will retry if problems are encountered.
	 * @param messages
	 * @throws ContextAggregatorException
	 */
    private void publish(Collection<String> messages) throws ContextAggregatorException {

        DMaaPEventPublisher publisher = publisherFactory.createPublisher();
	    try {
	        int sent = publisher.sendSync(publisherFactory.getPartition(), messages);
	        if(sent != messages.size()) {
	            closePublisher(publisher);
	            retryOrThrow(messages,
	                    new ContextAggregatorException(ContextAggregatorError.PUBLISHER_SEND_ERROR,
	                            "failed to send synchronously to partition " + publisherFactory.getPartition()));
	        }
	    } catch (Exception e) {
	        closePublisher(publisher);
		retryOrThrow(messages,
			new ContextAggregatorException(ContextAggregatorError.PUBLISHER_SEND_ERROR, e.getMessage()));
	    }
	    completeMessagePublishing(publisher);
	}

    /**
     * Completes message publishing by closing the publisher.
     * Will retry if an error is encountered.
     * @param publisher
     * @throws ContextAggregatorException
     */
    private void completeMessagePublishing(DMaaPEventPublisher publisher) throws ContextAggregatorException {
        List<String> unsentMessages = closePublisher(publisher);
        if ((unsentMessages != null) && !unsentMessages.isEmpty()) {
            String errorString = String.valueOf(unsentMessages.size()) + " unsent message(s)";
            retryOrThrow(unsentMessages, new ContextAggregatorException(
                    ContextAggregatorError.PUBLISHER_SEND_ERROR, errorString));
        }
    }

    /**
     * Retries to publish messages or throws the given exception if no retries are left
     * @param messages
     * @param exceptionToThrow
     * @throws ContextAggregatorException
     */
	private void retryOrThrow(Collection<String> messages, ContextAggregatorException exceptionToThrow)
	        throws ContextAggregatorException {
	    if (retriesRemaining <= 0) {
	        throw exceptionToThrow;
	    }
	    Log.debug(String.format("Retrying to publish messages (%d %s remaining)...", retriesRemaining, ((retriesRemaining == 1) ? "retry" : "retries")));
        retriesRemaining--;
	    publish(messages);
	}

	/**
	 * Closes the event publisher and returns any unsent messages
	 * @param publisher
	 * @return
	 * @throws ContextAggregatorException
	 */
    private List<String> closePublisher(DMaaPEventPublisher publisher) throws ContextAggregatorException {
        try {
            return publisher.closeWithUnsent();
        } catch (Exception e) {
            throw new ContextAggregatorException(ContextAggregatorError.PUBLISHER_CLOSE_ERROR, e.getMessage());
        }
    }
}

