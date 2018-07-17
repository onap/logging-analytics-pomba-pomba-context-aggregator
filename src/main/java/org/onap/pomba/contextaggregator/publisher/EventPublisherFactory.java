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
package org.onap.pomba.contextaggregator.publisher;

import org.onap.aai.event.client.DMaaPEventPublisher;

public class EventPublisherFactory {

    private String host;
    private String topic;
    private String motsid;
    private String pass;
    private int batchSize;
    private int maxAge;
    private int delay;
    private String type;
    private String partition;
    private int retries;


    public EventPublisherFactory(String host, String topic, String motsid, String pass,
            int batchSize, int maxAge, int delay, String type, String partition, int retries) {
        this.host = host;
        this.topic = topic;
        this.motsid = motsid;
        this.pass = pass;
        this.batchSize = batchSize;
        this.maxAge = maxAge;
        this.delay = delay;
        this.type = type;
        this.partition = partition;
        this.retries = retries;
    }

    public String getPartition() {
        return partition;
    }

    public int getRetries() {
        return retries;
    }

    public DMaaPEventPublisher createPublisher() {
        return new DMaaPEventPublisher(host, topic, motsid, pass, batchSize, maxAge, delay, type);
    }
}
