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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.onap.pomba.common.datatypes.ModelContext;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;

public class AggregatedModels {

    @Expose
    @SerializedName("event-header")
    Header entityHeader;
    @Expose
    @SerializedName("entity")
    PoaEntity poaEntity;

    /**
     * Creates an event with an entity header and entity containing the models and
     * poa-event from Dmaap.
     *
     * @param headerConfig The event header config
     * @param jsonContextMap The context map
     * @param event The POA Event
     */
    public AggregatedModels(EventHeaderConfig headerConfig, Map<String, String> jsonContextMap, POAEvent event) {
        entityHeader = new Header(headerConfig);

        Gson gson = new GsonBuilder().create();
        Map<String, ModelContext> contextMap = new HashMap<>();
        List<String> errorTexts = new ArrayList<>();

        for (Entry<String, String> entry : jsonContextMap.entrySet()) {
            ModelContext context = null;
            if (entry.getValue().isEmpty()) {
                context = new ModelContext();
            } else {
                context = gson.fromJson(entry.getValue(), ModelContext.class);
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(entry.getValue());
                errorTexts.addAll(extractErrors(entry.getKey(), jsonElement));
            }
            contextMap.put(entry.getKey(), context);
        }

        if (errorTexts.isEmpty()) {
            event.setDataQualitySummary(DataQualitySummary.ok());
        } else {
            // Fill the errors:
            event.setDataQualitySummary(DataQualitySummary.error(errorTexts));
        }

        poaEntity = new PoaEntity(contextMap, event);
    }

    /**
     * Recursive method to find all the dataQuality errors in the JsonElement.
     * @param errorPath Path to the current element
     * @param jsonElement The json element
     * @return list of error strings extracted from the json element and it's children.
     */
    private static List<String> extractErrors(String errorPath, JsonElement jsonElement) {
        List<String> errorTexts = new ArrayList<>();
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement indexElement = jsonArray.get(i);
                errorTexts.addAll(extractErrors(errorPath + "[" + i + "]", indexElement));
            }
        } else if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            extractErrorsFromJsonObject(errorPath, errorTexts, jsonObject);
        }

        return errorTexts;
    }

    private static void extractErrorsFromJsonObject(String errorPath, List<String> errorTexts, JsonObject jsonObject) {
        for (Entry<String, JsonElement> entrySet : jsonObject.entrySet()) {
            if ("dataQuality".equals(entrySet.getKey())) {
                JsonElement dqElement = entrySet.getValue();

                JsonObject dqObject = dqElement.getAsJsonObject();
                JsonElement dqStatusElement = dqObject.get("status");
                if (dqStatusElement == null) {
                    continue;
                }
                String statusValue = dqStatusElement.getAsString();

                if ("error".equals(statusValue)) {
                    JsonElement dqErrorTextElement = dqObject.get("errorText");
                    if (dqErrorTextElement != null) {
                        String errorTextValue = dqErrorTextElement.getAsString();
                        errorTexts.add(errorPath + ": " + errorTextValue);                        
                    }
                }
            } else {
                // recursive call to extract errors from other JsonElements:
                errorTexts.addAll(extractErrors(errorPath + "/" + entrySet.getKey(), entrySet.getValue()));
            }
        }
    }

    /**
     * Returns this instance as a JSON payload.
     *
     * @return
     */
    public String generateJsonPayload() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public Header getEntityHeader() {
        return entityHeader;
    }

    /**
     * Entity header class for JSON serialization.
     */
    private class Header {
        @Expose
        private String id;
        @Expose
        private String domain;
        @Expose
        @SerializedName("source-name")
        private String sourceName;
        @Expose
        @SerializedName("event-type")
        private String eventType;
        @Expose
        @SerializedName("entity-type")
        private String entityType;
        @Expose
        @SerializedName("top-entity-type")
        private String topEntityType;
        @Expose
        @SerializedName("topic-name")
        private String topicName;
        @Expose
        @SerializedName("event-id")
        private String eventId;

        public Header(EventHeaderConfig config) {
            id = UUID.randomUUID().toString();
            domain = config.getDomain();
            sourceName = config.getSourceName();
            eventType = config.getEventType();
            entityType = config.getEntityType();
            topEntityType = config.getTopicEntityType();
            topicName = config.getTopicName();
            eventId = UUID.randomUUID().toString();
        }
    }

    private class PoaEntity {
        @Expose
        @SerializedName("poa-event")
        POAEvent event;
        @Expose
        @SerializedName("context-list")
        private Map<String, ModelContext> contextMap;

        public PoaEntity(Map<String, ModelContext> contextMap, POAEvent event) {
            this.contextMap = contextMap;
            this.event = event;
        }
    }
}
