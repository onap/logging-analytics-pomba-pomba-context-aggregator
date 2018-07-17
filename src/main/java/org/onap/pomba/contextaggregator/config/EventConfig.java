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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    @Bean
    public EventHeaderConfig eventHeaderConfig(@Value("${event.header.domain}") String domain,
            @Value("${event.header.source-name}") String sourceName,
            @Value("${event.header.event-type}") String eventType,
            @Value("${event.header.entity-type}") String entityType,
            @Value("${event.header.topic-entity-type}") String topicEntityType,
            @Value("${event.header.topic-name}") String topicName) {

        return new EventHeaderConfig(domain, sourceName, eventType, entityType, topicEntityType, topicName);
    }
}
