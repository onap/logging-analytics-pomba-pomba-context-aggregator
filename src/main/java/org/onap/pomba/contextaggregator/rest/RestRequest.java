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
package org.onap.pomba.contextaggregator.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.onap.pomba.contextaggregator.datatypes.POAEvent;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorError;
import org.springframework.boot.autoconfigure.security.SecurityProperties.Headers;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

public class RestRequest {

    private static final String SERVICE_INSTANCE_ID = "serviceInstanceId";
    private static final String MODEL_VERSION_ID = "modelVersionId";
    private static final String MODE_INVARIANT_ID = "modelInvariantId";
    private static final String CUSTOMER_ID = "customerId";
	private static final String SERVICE_TYPE = "serviceType";

    private static final String APP_NAME = "context-aggregator";
    
    private static final String FROM_APP_ID = "X-FromAppId";
    private static final String TRANSACTION_ID = "X-TransactionId";

    private static EELFLogger logger = EELFManager.getInstance().getApplicationLogger();


    private RestRequest() {
        // intentionally empty
    }

    /**
     * Retrieves the model data from the given context builder
     * @param builder
     * @param event
     * @return Returns the JSON response from the context builder
     */
    public static String getModelData(ContextBuilder builder, POAEvent event) {

        RestClient restClient = createRestClient(builder);

        OperationResult result = restClient.get(
                generateUri(builder, event),
                generateHeaders(event.getxTransactionId()),
                MediaType.APPLICATION_JSON_TYPE);

        if(result.wasSuccessful()) {
            logger.debug("Retrieved model data for '" + builder.getContextName() + "': " + result.getResult());
            return result.getResult();
        } else {
     	    // failed!  return null
        	logger.error(ContextAggregatorError.FAILED_TO_GET_MODEL_DATA.getMessage(
        			builder.getContextName(), result.getFailureCause()));
        	logger.debug("Failed to retrieve model data for '" + builder.getContextName());
        	return null;
        }
    }

    private static RestClient createRestClient(ContextBuilder builder) {
        return new RestClient()
//                .validateServerHostname(false)
//                .validateServerCertChain(true)
//                .clientCertFile(builder.getKeyStorePath())
//                .clientCertPassword(builder.getKeyStorePassword())
//                .trustStore(builder.getTrustStorePath())
                .connectTimeoutMs(builder.getConnectionTimeout())
                .readTimeoutMs(builder.getReadTimeout());
    }

    private static String generateUri(ContextBuilder builder, POAEvent event) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(builder.getProtocol())
                .host(builder.getHost())
                .port(builder.getPort())
                .path(builder.getBaseUri())
                .queryParam(SERVICE_INSTANCE_ID, event.getServiceInstanceId())
                .queryParam(MODEL_VERSION_ID, event.getModelVersionId())
                .queryParam(MODE_INVARIANT_ID, event.getModelInvariantId())
                .queryParam(SERVICE_TYPE, event.getServiceType())
                .queryParam(CUSTOMER_ID, event.getCustomerId())
                .build()
                .encode();
        return uriComponents.toUriString();
    }

    private static Map<String, List<String>> generateHeaders(String transactionId) {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(FROM_APP_ID, APP_NAME);
        headers.add(TRANSACTION_ID, transactionId);
        return headers;
    }
}
