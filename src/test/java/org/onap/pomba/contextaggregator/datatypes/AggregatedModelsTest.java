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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AggregatedModelsTest
{
    AggregatedModels aggregatedModels;

    private String domain;
    private String sourceName;
    private String eventType;
    private String entityType;
    private String topEntityType;
    private String topicName;

    EventHeaderConfig eventHeaderConfig = new EventHeaderConfig(
            domain, sourceName, eventType, entityType, topEntityType, topicName);

    Map<String, String> jsonContextMap = new HashMap<>();

    POAEvent pOAEvent = new POAEvent();

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        pOAEvent.setServiceInstanceId("a");
        pOAEvent.setModelVersionId("b");
        pOAEvent.setModelInvariantId("c");
        pOAEvent.setCustomerId("d");
        pOAEvent.setServiceType("e");
        pOAEvent.setxFromAppId("e");
        pOAEvent.setxTransactionId("f");
    }

    @Test
    public void testGenerateJsonPayload() throws Exception
    {
        aggregatedModels = new AggregatedModels(eventHeaderConfig,jsonContextMap,pOAEvent);
        Assert.assertNotNull(aggregatedModels.generateJsonPayload());
        Assert.assertNotNull(aggregatedModels.getEntityHeader());
    }
}
