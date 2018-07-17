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
package org.onap.pomba.contextaggregator.exception;

import java.text.MessageFormat;

public enum ContextAggregatorError {

    GENERAL_ERROR("CA-100", "Internal error encountered: {0}"), FAILED_TO_LOAD_BUILDER_PROPERTIES("CA-101",
            "Failed to load builder properties: {0}"), JSON_PARSER_ERROR("CA-102",
                    "Failed to parse JSON request: {0}"), INVALID_EVENT_RECEIVED("CA-103",
                            "Invalid event received: {0}"), EVENT_ATTRIBUTE_MISSING("CA-104",
                                    "Mandatory attribute missing from event: {0}"), FAILED_TO_GET_MODEL_DATA("CA-105",
                                            "Failed to retrieve model data for {0}, reason: {1}"), PUBLISHER_SEND_ERROR(
                                                    "CA-106",
                                                    "Error encountered when publishing messages: {0}"), PUBLISHER_CLOSE_ERROR(
                                                            "CA-107",
                                                            "Error encountered when closing publisher: {0}"), FAILED_TO_PUBLISH_RESULT(
                                                                    "CA-108",
                                                                    "Failed to publish model data: {0}"), BUILDER_PROPERTIES_NOT_FOUND(
                                                                            "CA-109",
                                                                            "No builder properties were found under location(s): {0}");

    private String errorId;
    private String message;

    private ContextAggregatorError(String errorId, String message) {
        this.errorId = errorId;
        this.message = message;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getMessage(Object... args) {
        MessageFormat formatter = new MessageFormat(this.message);
        return formatter.format(args);
    }
}
