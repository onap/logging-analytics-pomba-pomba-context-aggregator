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

import static org.junit.Assert.assertEquals;

import com.att.nsa.mr.client.MRBatchingPublisher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class EventPublisherFactoryTest {

    @Test
    public void testgetPartition() {
        EventPublisherFactory pojo = new EventPublisherFactory(
                "host", "topic", "motsid", "pass", 0, 0, 0, "type", "partition1", 0);
        assertEquals("other values", pojo.getPartition(),"partition1");
    }

    @Test
    public void testgetRetries() {
        EventPublisherFactory pojo = new EventPublisherFactory(
                "host", "topic", "motsid", "pass", 0, 0, 0, "type", "partition", 5);
        assertEquals(5.0, pojo.getRetries(),10.0);
    }

    @Test
    public void testCreatePublisherNullPublisher() throws IllegalArgumentException {
        EventPublisherFactory eventPublisherFactory =
                new EventPublisherFactory("host", "topic", "motsid", "pass", 0, 0,
                        0, "type", "partition", 0);
        try {
            eventPublisherFactory.createPublisher();
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testCreatePublisher() {
        EventPublisherFactory eventPublisherFactory =
                new EventPublisherFactory("host", "topic", "motsid", "pass", 0, 0,
                        1, "type", "partition", 0);

        MRBatchingPublisher result = eventPublisherFactory.createPublisher();
        Assert.assertNotEquals(null, result);
    }
}
