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

import com.att.nsa.mr.client.MRConsumer;
import com.att.nsa.mr.client.impl.MRConsumerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.pomba.contextaggregator.publisher.EventPublisherFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@SpringBootTest
@TestPropertySource(properties = { "transport.consume.host=http://localhost", "transport.consume.port=8080" })
public class TransportConfigTest
{
    TransportConfig transportConfig = new TransportConfig();

    @Test
    public void testConsumer() throws Exception
    {
        MRConsumer result = transportConfig
                .consumer("host", "port", "topic", "motsid", "pass",
                        "consumerGroup", "consumerId", 0, 0, 0, "type");
        final Properties extraProps = new Properties();
        extraProps.put("Protocol", "http");
        ((MRConsumerImpl) result).setProps(extraProps);
        Assert.assertNotEquals(null, result);
        Assert.assertEquals("host:port", ((MRConsumerImpl) result).getHost());
    }

    @Test
    public void testPublisherFactory() throws Exception
    {
        EventPublisherFactory result = transportConfig
                .publisherFactory("host", "port", "topic", "motsid", "pass", 0,
                        0, 0, "type", "partition", 0);
        EventPublisherFactory lEventPublisherFactory = new EventPublisherFactory("host:port", "topic", "motsid", "pass", 0,
                0, 0, "type", "partition", 0);
        Assert.assertEquals(lEventPublisherFactory.getPartition(), result.getPartition());
        Assert.assertEquals(lEventPublisherFactory.getRetries(), result.getRetries());
    }
}
