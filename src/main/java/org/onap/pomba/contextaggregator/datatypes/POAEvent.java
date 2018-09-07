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
package org.onap.pomba.contextaggregator.datatypes;

import org.onap.pomba.contextaggregator.exception.ContextAggregatorError;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorException;

public class POAEvent {

    private String serviceInstanceId;
    private String modelVersionId;
    private String modelInvariantId;
    private String customerId;
    private String serviceType;
    private String xFromAppId;
    private String xTransactionId;

    public POAEvent() {}

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(String modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public String getModelInvariantId() {
        return modelInvariantId;
    }

    public void setModelInvariantId(String modelInvariantId) {
        this.modelInvariantId = modelInvariantId;
    }

    public String getxFromAppId() {
        return xFromAppId;
    }

    public void setxFromAppId(String xFromAppId) {
        this.xFromAppId = xFromAppId;
    }

    public String getxTransactionId() {
        return xTransactionId;
    }

    public void setxTransactionId(String xTransactionId) {
        this.xTransactionId = xTransactionId;
    }

    public boolean validate() throws ContextAggregatorException {
        final String missing = " is missing";

        // serviceInstanceId
        if (getServiceInstanceId() == null || getServiceInstanceId().isEmpty()) {
            throw new ContextAggregatorException(ContextAggregatorError.INVALID_EVENT_RECEIVED,
                    "serviceInstanceId" + missing);
        }

        // modelVersionId
        if (getModelVersionId() == null || getModelVersionId().isEmpty()) {
            throw new ContextAggregatorException(ContextAggregatorError.INVALID_EVENT_RECEIVED,
                    "modelVersionId" + missing);
        }

        // modelInvariantId
        if (getModelInvariantId() == null || getModelInvariantId().isEmpty()) {
            throw new ContextAggregatorException(ContextAggregatorError.INVALID_EVENT_RECEIVED,
                    "modelInvariantId" + missing);
        }

        // X-FromAppId
        if (getxFromAppId() == null || getxFromAppId().isEmpty()) {
            throw new ContextAggregatorException(ContextAggregatorError.INVALID_EVENT_RECEIVED, "xFromAppId" + missing);
        }

        // X-TransactionId
        if (getxTransactionId() == null || getxTransactionId().isEmpty()) {
            throw new ContextAggregatorException(ContextAggregatorError.INVALID_EVENT_RECEIVED,
                    "xTransactionId" + missing);
        }

        return true;
    }

    @Override
    public String toString() {
        return "POAEvent [serviceInstanceId=" + serviceInstanceId + ", modelVersionId=" + modelVersionId
                + ", modelInvariantId=" + modelInvariantId + ", xFromAppId=" + xFromAppId + ", xTransactionId=" + xTransactionId + "]";
    }
}
