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

import com.att.aft.dme2.internal.gson.Gson;
import com.att.aft.dme2.internal.gson.GsonBuilder;
import com.att.aft.dme2.internal.gson.JsonSyntaxException;
import com.att.nsa.apiClient.http.HttpException;
import com.att.nsa.mr.client.MRBatchingPublisher;
import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.MRPublisher;
import com.att.nsa.mr.client.MRTopicManager;
import com.att.nsa.mr.client.impl.MRSimplerBatchPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.onap.pomba.common.datatypes.DataQuality;
import org.onap.pomba.common.datatypes.DataQuality.Status;
import org.onap.pomba.common.datatypes.ModelContext;
import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;
import org.onap.pomba.contextaggregator.datatypes.AggregatedModels;
import org.onap.pomba.contextaggregator.datatypes.POAEvent;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorError;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorException;
import org.onap.pomba.contextaggregator.publisher.EventPublisherFactory;
import org.onap.pomba.contextaggregator.rest.RestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ContextAggregatorProcessor implements Callable<Void> {

    private Logger log = LoggerFactory.getLogger(ContextAggregatorProcessor.class);
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private ExecutorService executor = Executors.newFixedThreadPool(10);
    private int retriesRemaining;

    @Autowired
    private MRConsumer consumer;

    @Autowired
    private MRTopicManager messageRouterTopicMgr;

    @Value("${transport.message-router.requiredPombaTopics}")
    private String messageRouterRequiredPombaTopicList;

    @Autowired
    private EventPublisherFactory publisherFactory;

    @Autowired
    List<ContextBuilder> contextBuilders;

    @Autowired
    EventHeaderConfig eventHeaderConfig;


    /**
     * Parses the consumed event, retrieves model data from all context builders and publishes the
     * aggregated models to DMaaP.
     *
     * @param payload The event pay load
     */
    public void process(String payload) {

        log.info("Consumed event: {}", payload);
        POAEvent event;
        try {
            event = parseEvent(payload);
            log.info("Received POA event: {}", event);
        } catch (ContextAggregatorException e) {
            log.error(ContextAggregatorError.INVALID_EVENT_RECEIVED.getMessage(e.getMessage()));
            // TODO: publish to error topic?
            return;
        }

        Map<String, String> retrievedModels = new HashMap<>();
        for (ContextBuilder builder : contextBuilders) {
            try {
                log.info("Retrieving model data for: {}", builder.getContextName());
                String modelData = RestRequest.getModelData(builder, event);
                retrievedModels.put(builder.getContextName(), modelData);
            } catch (ContextAggregatorException e) {
                DataQuality errorDataQuality = new DataQuality();
                errorDataQuality.setStatus(Status.error);
                errorDataQuality.setErrorText(e.getMessage());
                ModelContext modelContext = new ModelContext();
                modelContext.setDataQuality(errorDataQuality);
                Gson gsonBuilder = new GsonBuilder().create();
                String errorData = gsonBuilder.toJson(modelContext);
                log.error("Setting dataQuality status for '{}' context builder to ERROR: {}.", builder.getContextName(), e.getMessage());
                retrievedModels.put(builder.getContextName(), errorData);
            }
        }

        try {
            publishModels(new AggregatedModels(eventHeaderConfig, retrievedModels, event));
        } catch (ContextAggregatorException e) {
            log.error(ContextAggregatorError.FAILED_TO_PUBLISH_RESULT.getMessage(e.getMessage()));
        }
    }

    /**
     * The configurable POMBA Topics (POA-AUDIT-INIT,POA-AUDIT-RESULT,POA-RULE-VALIDATION, etc.)
     * will be created if any of topics doesn't exist prior to be invoked.
     *
     * @param eventPayload
     * @return
     * @throws ContextAggregatorException
     */
    @Override
    public Void call() throws Exception {
        createPombaTopics();

        while (true) {
            for (String event : consumer.fetch()) {
                executor.execute(() -> {
                    try {
                        process(event);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                });
            }
        }
    }

    /**
     * Parses, validates and returns a PAOEvent
     *
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
     * Publishes the aggregated models.
     *
     * @param models
     * @throws ContextAggregatorException
     */
    private void publishModels(AggregatedModels models) throws ContextAggregatorException {
        String payload = models.generateJsonPayload();
        log.info("Publishing models: {}", payload);
        retriesRemaining = publisherFactory.getRetries();
        publish(Arrays.asList(payload));
    }

    /**
     * Publishes the given messages with a new EventPublisher instance. Will retry if problems are
     * encountered.
     *
     * @param messages
     * @throws ContextAggregatorException
     */
    private void publish(Collection<String> messages) throws ContextAggregatorException {
        MRBatchingPublisher publisher = publisherFactory.createPublisher();
        String partition = publisherFactory.getPartition();
        try {
            ((MRSimplerBatchPublisher) publisher).getProps().put("partition", partition);
            final Collection<MRPublisher.message> dmaapMessages = new ArrayList<>();
            for (final String message : messages) {
                dmaapMessages.add(new MRPublisher.message(partition, message));
            }

            int sent = publisher.send(dmaapMessages);
            if (sent != messages.size()) {
                closePublisher(publisher);
                retryOrThrow(messages, new ContextAggregatorException(ContextAggregatorError.PUBLISHER_SEND_ERROR,
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
     * Completes message publishing by closing the publisher. Will retry if an error is encountered.
     *
     * @param publisher
     * @throws ContextAggregatorException
     */
    private void completeMessagePublishing(MRBatchingPublisher publisher) throws ContextAggregatorException {
        List<String> unsentMessages = closePublisher(publisher);
        if ((unsentMessages != null) && !unsentMessages.isEmpty()) {
            String errorString = String.valueOf(unsentMessages.size()) + " unsent message(s)";
            retryOrThrow(unsentMessages,
                    new ContextAggregatorException(ContextAggregatorError.PUBLISHER_SEND_ERROR, errorString));
        }
    }

    /**
     * Retries to publish messages or throws the given exception if no retries are left.
     *
     * @param messages
     * @param exceptionToThrow
     * @throws ContextAggregatorException
     */
    private void retryOrThrow(Collection<String> messages, ContextAggregatorException exceptionToThrow)
            throws ContextAggregatorException {
        if (retriesRemaining <= 0) {
            throw exceptionToThrow;
        }
        log.info("Retrying to publish messages ({} {} remaining)...", retriesRemaining,
                ((retriesRemaining == 1) ? "retry" : "retries"));
        retriesRemaining--;
        publish(messages);
    }

    /**
     * Closes the event publisher and returns any unsent messages
     *
     * @param publisher
     * @return
     * @throws ContextAggregatorException
     */
    private List<String> closePublisher(MRBatchingPublisher publisher) throws ContextAggregatorException {
        try {
            return publisher.close(20L, TimeUnit.SECONDS).stream().map(m -> m.fMsg).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ContextAggregatorException(ContextAggregatorError.PUBLISHER_CLOSE_ERROR, e);
        }
    }

    private List<String> getRequiredTopicList(String messageRouterRequiredPombaTopicList) {
        List<String> pombaTopicList = new ArrayList<>();
        String noSpacePombaTopicList = messageRouterRequiredPombaTopicList.replaceAll("\\s", "");
        String[] pombaTopicStrSet = noSpacePombaTopicList.split(",");
        for (int i = 0; i < pombaTopicStrSet.length; i++) {
            pombaTopicList.add(pombaTopicStrSet[i]);
        }
        return pombaTopicList;
    }

    private void createPombaTopics() {

        List<String> requiredTopicList = getRequiredTopicList(messageRouterRequiredPombaTopicList);

        String topicDescription = "create default topic";
        int partitionCount = 1;
        int replicationCount = 1;

        for (String topicRequired : requiredTopicList) {
            try {
                messageRouterTopicMgr.createTopic(topicRequired, topicDescription, partitionCount, replicationCount);
                log.info("Created Pomba Topic {}", topicRequired);
            } catch (HttpException e1) {
                log.error(ContextAggregatorError.FAILED_TO_CREATE_POMBA_TOPICS.getMessage(e1.getMessage()));
            } catch (IOException e) {
                log.error(ContextAggregatorError.FAILED_TO_CREATE_POMBA_TOPICS.getMessage(e.getMessage()));
            }
        }
    }
}

