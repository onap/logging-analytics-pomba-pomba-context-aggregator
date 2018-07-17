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
package org.onap.pomba.contextaggregator.service;

import com.att.nsa.mr.client.MRConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;
import org.onap.pomba.contextaggregator.publisher.EventPublisherFactory;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ConfigurationProperties("classpath:/src/test/resources/GoodProperties/sdnc.properties")
public class ContextAggregatorProcessorTest
{
    @Mock
    Logger log;
    @Mock
    ExecutorService executor;
    @Mock
    MRConsumer consumer;
    @Mock
    EventPublisherFactory publisherFactory;
    @Mock
    ContextBuilder contextBuilder;
    @Mock
    List<ContextBuilder> contextBuilders;
    @Mock
    EventHeaderConfig eventHeaderConfig;

    @InjectMocks
    ContextAggregatorProcessor contextAggregatorProcessor;

    private String payload = "{"  +
            "        \"serviceInstanceId\""  + ": " +  "\"8ea56b0d-459d-4668-b363-c9567432d8b7\"" + "," +
            "        \"modelVersionId\""     + ": " +  "\"4e3d28cf-d654-41af-a47b-04b4bd0ac58e\"" + "," +
            "        \"modelInvariantId\""   + ": " +  "\"74bc1518-282d-4148-860f-8892b6369456\"" + "," +
            "        \"customerId\""         + ": " +  "\"junit\""                                + "," +
            "        \"serviceType\""        + ": " +  "\"vFWCL\""                                + "," +
            "        \"xFromAppId\""         + ": " +  "\"POMBA\""                                + "," +
            "        \"xTransactionId\""     + ": " +  "\"8a9ddb25-2e79-449c-a40d-5011bac0da39\""                              +
            "}";

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcessNullPublisherInvalidPayload() throws Exception
    {
        when(publisherFactory.getPartition())
                .thenReturn("getPartitionResponse");
        when(publisherFactory.getRetries()).thenReturn(0);
        when(publisherFactory.createPublisher()).thenReturn(null);
        contextAggregatorProcessor.process("NoJsonPayload");
    }

    @Test
    public void testProcessNullPublisherEmptyPayload() throws Exception {
        when(publisherFactory.getPartition())
                .thenReturn("getPartitionResponse");
        when(publisherFactory.getRetries()).thenReturn(0);
        when(publisherFactory.createPublisher()).thenReturn(null);

        try {
            contextAggregatorProcessor.process("");
        }
        catch (Exception e){
            // expected
        }
    }

    @Test
    public void testProcessNullPublisherValidPayload() throws Exception
    {
        when(publisherFactory.getPartition())
                .thenReturn("getPartitionResponse");
        when(publisherFactory.getRetries()).thenReturn(0);
        when(publisherFactory.createPublisher()).thenReturn(null);
        try {
            contextAggregatorProcessor.process(payload);
        }
        catch (NullPointerException e) {
            //Expected.. No ContextBuilre found
        }
    }

//    @Test
//    public void testCall() throws Exception
//    {
//        when(publisherFactory.getPartition())
//                .thenReturn("getPartitionResponse");
//        when(publisherFactory.getRetries()).thenReturn(0);
//        when(publisherFactory.createPublisher()).thenReturn(null);
//
//        try{
//            contextAggregatorProcessor.call();
//        }
//        catch (Exception e)
//        {
//            //expected
//        }
//    }
}
