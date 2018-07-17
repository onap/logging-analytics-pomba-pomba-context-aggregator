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
public class EventConfigTest
{
    EventConfig eventConfig = new EventConfig();

    @Test
    public void testEventHeaderConfig() throws Exception
    {
        EventHeaderConfig result = eventConfig
                .eventHeaderConfig("domain", "sourceName", "eventType",
                        "entityType", "topicEntityType", "topicName");
        Assert.assertEquals(result.getDomain(), "domain");
        Assert.assertEquals(result.getSourceName(), "sourceName");
        Assert.assertEquals(result.getEventType(), "eventType");
        Assert.assertEquals(result.getEntityType(), "entityType");
        Assert.assertEquals(result.getTopicEntityType(), "topicEntityType");
        Assert.assertEquals(result.getTopicName(), "topicName");
    }
}
