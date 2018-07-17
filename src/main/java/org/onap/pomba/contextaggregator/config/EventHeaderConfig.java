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
package org.onap.pomba.contextaggregator.config;

public class EventHeaderConfig {

    private String domain;
    private String sourceName;
    private String eventType;
    private String entityType;
    private String topicEntityType;
    private String topicName;

    public EventHeaderConfig(String domain, String sourceName, String eventType, String entityType,
            String topicEntityType, String topicName) {
        this.domain = domain;
        this.sourceName = sourceName;
        this.eventType = eventType;
        this.entityType = entityType;
        this.topicEntityType = topicEntityType;
        this.topicName = topicName;
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

    public String getTopicEntityType() {
        return topicEntityType;
    }

    public String getTopicName() {
        return topicName;
    }
}
