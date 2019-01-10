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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EventHeaderConfigTest {

    private String domain;
    private String sourceName;
    private String eventType;
    private String entityType;
    private String topicEntityType;
    private String topicName;

    EventHeaderConfig eventHeaderConfig = new EventHeaderConfig(
            domain, sourceName, eventType, entityType, topicEntityType, topicName);

    @Test
    public void getDomainTest() {
        Assert.assertEquals(eventHeaderConfig.getDomain(), domain);
    }

    @Test
    public void getSourceNameTest() {
        Assert.assertEquals(eventHeaderConfig.getSourceName(), sourceName);
    }

    @Test
    public void getEventTypeTest() {
        Assert.assertEquals(eventHeaderConfig.getEventType(), eventType);
    }

    @Test
    public void getEntityTypeTest() {
        Assert.assertEquals(eventHeaderConfig.getEntityType(), entityType);
    }

    @Test
    public void getTopEntityTypeTest() {
        Assert.assertEquals(eventHeaderConfig.getTopicEntityType(), topicEntityType);
    }

    @Test
    public void getTopicNameTest() {
        Assert.assertEquals(eventHeaderConfig.getTopicName(), topicName);
    }
}
