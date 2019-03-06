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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.onap.pomba.contextaggregator.config.EventHeaderConfig;

public class AggregatedModelsTest {

    AggregatedModels aggregatedModels;

    private String domain;
    private String sourceName;
    private String eventType;
    private String entityType;
    private String topEntityType;
    private String topicName;

    EventHeaderConfig eventHeaderConfig = new EventHeaderConfig(domain, sourceName, eventType, entityType,
            topEntityType, topicName);

    Map<String, String> jsonContextMap = new HashMap<>();

    POAEvent poaEvent = new POAEvent();

    /**
     * JUnit setup.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        poaEvent.setServiceInstanceId("a");
        poaEvent.setModelVersionId("b");
        poaEvent.setModelInvariantId("c");
        poaEvent.setxFromAppId("e");
        poaEvent.setxTransactionId("f");
    }

    @Test
    public void testGenerateJsonPayload() throws Exception {
        aggregatedModels = new AggregatedModels(eventHeaderConfig, jsonContextMap, poaEvent);
        Assert.assertNotNull(aggregatedModels.generateJsonPayload());
        Assert.assertNotNull(aggregatedModels.getEntityHeader());
    }

    @Test
    public void testDataQualitySummaryError() throws Exception {
        String filename = "src/test/resources/modelContextAAI-input.json";
        String fileContent = new String(Files.readAllBytes(Paths.get(filename)));

        jsonContextMap.put("aai", fileContent);
        jsonContextMap.put("sdnc", fileContent);
        aggregatedModels = new AggregatedModels(eventHeaderConfig, jsonContextMap, poaEvent);
        Assert.assertNotNull(aggregatedModels.generateJsonPayload());
        Assert.assertNotNull(aggregatedModels.getEntityHeader());
        Assert.assertNotNull(poaEvent.getDataQualitySummary());
        System.err.println(poaEvent.getDataQualitySummary());
        Assert.assertEquals(DataQualitySummary.Status.error, poaEvent.getDataQualitySummary().getStatus());
    }
    
    @Test
    public void testDataQualitySummaryOk() throws Exception {
        jsonContextMap.put("aai", "{}");
        aggregatedModels = new AggregatedModels(eventHeaderConfig, jsonContextMap, poaEvent);
        Assert.assertNotNull(aggregatedModels.generateJsonPayload());
        Assert.assertNotNull(aggregatedModels.getEntityHeader());
        Assert.assertNotNull(poaEvent.getDataQualitySummary());
        Assert.assertEquals(DataQualitySummary.Status.ok, poaEvent.getDataQualitySummary().getStatus());
    }
}
