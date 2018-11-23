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

import java.util.Collection;
import java.util.Properties;

import org.onap.pomba.contextaggregator.publisher.EventPublisherFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.att.nsa.mr.client.MRClientFactory;
import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.MRTopicManager;
import com.att.nsa.mr.client.impl.MRConsumerImpl;

@Configuration
public class TransportConfig {
    @Bean
    public MRConsumer consumer(@Value("${transport.consume.host}") String host,
            @Value("${transport.consume.port}") String port, @Value("${transport.consume.topic}") String topic,
            @Value("${transport.consume.motsid}") String motsid, @Value("${transport.consume.pass}") String pass,
            @Value("${transport.consume.consumergroup}") String consumerGroup,
            @Value("${transport.consume.consumerid}") String consumerId,
            @Value("${transport.consume.timeout}") int timeout, @Value("${transport.consume.batchsize}") int batchSize,
            @Value("${transport.consume.msglimit}") int msgLimit, @Value("${transport.consume.type}") String type) {


        String hostStr = host + ":" + port;

        final MRConsumer consumer = MRClientFactory.createConsumer(hostStr, topic, motsid, pass, consumerGroup,
                consumerId, timeout, msgLimit, type, null);

        final Properties extraProps = new Properties();
        extraProps.put("Protocol", "http");
        ((MRConsumerImpl) consumer).setProps(extraProps);

        return consumer;
    }

    @Bean
    public MRTopicManager messageRouterTopicMgr (@Value("${transport.consume.host}") String host,
            @Value("${transport.consume.port}") String port,
            @Value("${transport.message-router.apiKey}") String apiKey,
            @Value("${transport.message-router.apiSecret}") String apiSecret
            ) {

        String hostStr = host + ":" + port;
        // Verify if all topics ()
        Collection<String> hostSet = java.util.Arrays.asList(hostStr);
        MRTopicManager mgr = MRClientFactory.createTopicManager(hostSet, apiKey, apiSecret);
        return mgr;
    }


    @Bean
    public EventPublisherFactory publisherFactory(@Value("${transport.publish.host}") String host,
            @Value("${transport.publish.port}") String port, @Value("${transport.publish.topic}") String topic,
            @Value("${transport.publish.motsid}") String motsid, @Value("${transport.publish.pass}") String pass,
            @Value("${transport.publish.batchsize}") int batchSize, @Value("${transport.publish.maxage}") int maxAge,
            @Value("${transport.publish.delay}") int delay, @Value("${transport.publish.type}") String type,
            @Value("${transport.publish.partition}") String partition,
            @Value("${transport.publish.retries}") int retries) {

        String hostStr = host + ":" + port;
        return new EventPublisherFactory(hostStr, topic, motsid, pass, batchSize, maxAge, delay, type, partition,
                retries);
    }

}
