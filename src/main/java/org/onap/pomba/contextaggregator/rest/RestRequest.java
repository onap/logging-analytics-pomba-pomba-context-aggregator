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

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.eclipse.jetty.util.security.Password;
import org.onap.aai.restclient.client.Headers;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.onap.pomba.contextaggregator.datatypes.POAEvent;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorError;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class RestRequest {

    private static final String SERVICE_INSTANCE_ID = "serviceInstanceId";
    private static final String MODEL_VERSION_ID = "modelVersionId";
    private static final String MODEL_INVARIANT_ID = "modelInvariantId";
    private static final String APP_NAME = "context-aggregator";
    private static final String BASIC_AUTH = "Basic ";

    private static final String MDC_REQUEST_ID = "RequestId";
    private static final String MDC_SERVER_FQDN = "ServerFQDN";
    private static final String MDC_SERVICE_NAME = "ServiceName";
    private static final String MDC_PARTNER_NAME = "PartnerName";
    private static final String MDC_START_TIME = "StartTime";
    private static final String MDC_SERVICE_INSTANCE_ID = "ServiceInstanceId";
    private static final String MDC_INVOCATION_ID = "InvocationID";
    private static final String MDC_CLIENT_ADDRESS = "ClientAddress";

    private static final String MDC_STATUS_CODE = "StatusCode";
    private static final String MDC_RESPONSE_CODE = "ResponseCode";
    private static final String MDC_INSTANCE_UUID = "InstanceId";

    private static Logger log = LoggerFactory.getLogger(RestRequest.class);


    private RestRequest() {
        // intentionally empty
    }

    /**
     * Retrieves the model data from the given context builder.
     *
     * @param builder The context builder.
     * @param event The audit event.
     * @return Returns the JSON response from the context builder
     */
    public static String getModelData(ContextBuilder builder, POAEvent event, UUID instanceId) throws ContextAggregatorException {

        initMdc(event, instanceId);
        RestClient restClient = createRestClient(builder);

        OperationResult result;

        try {
            result = restClient.get(generateUri(builder, event), generateHeaders(event, builder),
                    MediaType.APPLICATION_JSON_TYPE);
        } catch (Exception e) {
            log.error("Exception in Rest call", e);
            throw new ContextAggregatorException(ContextAggregatorError.FAILED_TO_GET_MODEL_DATA,
                    builder.getContextName(), e.getMessage());
        }

        if (result == null) {
            MDC.put(MDC_STATUS_CODE, "ERROR");
            throw new ContextAggregatorException(ContextAggregatorError.FAILED_TO_GET_MODEL_DATA,
                    builder.getContextName(), "Null result");
        }
        if (result.wasSuccessful()) {
            MDC.put(MDC_RESPONSE_CODE, String.valueOf(result.getResultCode()));
            MDC.put(MDC_STATUS_CODE, "COMPLETE");
            log.info("Retrieved model data for '{}' context builder. Result: {}", builder.getContextName(), result.getResult());
            return result.getResult();
        }
        // failed! throw Exception:
        MDC.put(MDC_STATUS_CODE, "ERROR");
        throw new ContextAggregatorException(ContextAggregatorError.FAILED_TO_GET_MODEL_DATA, builder.getContextName(),
                result.getFailureCause());

    }

    private static void initMdc(POAEvent event, UUID instanceId) {
        MDC.clear();
        MDC.put(MDC_REQUEST_ID, event.getxTransactionId());
        MDC.put(MDC_SERVICE_NAME, APP_NAME);
        MDC.put(MDC_SERVICE_INSTANCE_ID, event.getServiceInstanceId());
        MDC.put(MDC_PARTNER_NAME, event.getxFromAppId());
        MDC.put(MDC_START_TIME, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
        MDC.put(MDC_INVOCATION_ID, UUID.randomUUID().toString());
        MDC.put(MDC_INSTANCE_UUID, instanceId.toString());

        try {
            MDC.put(MDC_CLIENT_ADDRESS, InetAddress.getLocalHost().getCanonicalHostName());
        } catch (Exception e) {
            // If, for some reason we are unable to get the canonical host name,
            // we
            // just want to leave the field null.
            log.info("Could not get canonical host name for " + MDC_SERVER_FQDN + ", leaving field null");
        }
    }


    private static RestClient createRestClient(ContextBuilder builder) {
        return new RestClient()
                .connectTimeoutMs(builder.getConnectionTimeout()).readTimeoutMs(builder.getReadTimeout());
    }

    private static String generateUri(ContextBuilder builder, POAEvent event) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(builder.getProtocol())
                .host(builder.getHost()).port(builder.getPort()).path(builder.getBaseUri())
                .queryParam(SERVICE_INSTANCE_ID, event.getServiceInstanceId())
                .queryParam(MODEL_VERSION_ID, event.getModelVersionId())
                .queryParam(MODEL_INVARIANT_ID, event.getModelInvariantId()).build().encode();
        return uriComponents.toUriString();
    }

    private static Map<String, List<String>> generateHeaders(POAEvent event, ContextBuilder builder) {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add(Headers.FROM_APP_ID, event.getxFromAppId());
        headers.add(Headers.TRANSACTION_ID, event.getxTransactionId());
        headers.add(Headers.AUTHORIZATION, getBasicAuthString(builder));
        return headers;
    }

    private static String getBasicAuthString(ContextBuilder builder) {
        String encodedString = Base64.getEncoder()
                .encodeToString((builder.getUsername() + ":" + Password.deobfuscate(builder.getPassword())).getBytes());
        return BASIC_AUTH + encodedString;

    }
}
