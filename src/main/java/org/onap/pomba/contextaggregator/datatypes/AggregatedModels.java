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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.onap.pomba.common.datatypes.ModelContext;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AggregatedModels {

    @Expose
    @SerializedName("event-header")
    Header entityHeader;
    @Expose
    @SerializedName("entity")
    POAEntity poaEntity;


    /**
     * Creates an event with an entity header and entity containing the models and poa-event from Dmaap
     *
     * @param headerConfig
     * @param jsonContextMap
     */
    public AggregatedModels(EventHeaderConfig headerConfig, Map<String, String> jsonContextMap, POAEvent event) {
        entityHeader = new Header(headerConfig);

        Gson gson = new GsonBuilder().create();
        Map<String, ModelContext> contextMap = new HashMap<>();
        for (Entry<String, String> entry : jsonContextMap.entrySet()) {
            ModelContext context = null;
            if (entry.getValue().isEmpty()) {
                context = new ModelContext();
                context.setVf(null);
            } else {
                context = gson.fromJson(entry.getValue(), ModelContext.class);
            }
            contextMap.put(entry.getKey(), context);
        }

        poaEntity = new POAEntity(contextMap, event);
    }


    /**
     * Returns this instance as a JSON payload
     *
     * @return
     */
    public String generateJsonPayload() {
        Gson gson = new GsonBuilder().create();
        String payload = gson.toJson(this);
        return payload;
    }

    public Header getEntityHeader() {
        return entityHeader;
    }



    /**
     * Entity header class for JSON serialization
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


        public String getId() {
            return id;
        }

        public String getDomain() {
            return domain;
        }

        public String getSourceName() {
            return sourceName;
        }

        public String getEventType() {
            return eventType;
        }

        public String getEntityType() {
            return entityType;
        }

        public String getTopEntityType() {
            return topEntityType;
        }

        public String getTopicName() {
            return topicName;
        }

        public String getEventId() {
            return eventId;
        }
    }


    private class POAEntity {
        @Expose
        @SerializedName("poa-event")
        POAEvent event;
        @Expose
        @SerializedName("context-list")
        private Map<String, ModelContext> contextMap;

        public POAEntity(Map<String, ModelContext> contextMap, POAEvent event) {
            this.contextMap = contextMap;
            this.event = event;
        }
    }
}
