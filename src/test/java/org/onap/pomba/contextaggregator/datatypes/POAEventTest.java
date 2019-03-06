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

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorException;

public class POAEventTest {

    POAEvent poaEvent = new POAEvent();

    @Before
    public void setup() {
        poaEvent.setServiceInstanceId("a");
        poaEvent.setModelVersionId("b");
        poaEvent.setModelInvariantId("c");
        poaEvent.setxFromAppId("e");
        poaEvent.setxTransactionId("f");
    }

    @Test
    public void testValidate() throws ContextAggregatorException {
        poaEvent.validate();
    }

    @Test
    public void testValidateEmptyServiceInstanceId() throws ContextAggregatorException {
        poaEvent.setServiceInstanceId("");
        try {
            poaEvent.validate();
        }
        catch (ContextAggregatorException e) {
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyModelVersionId() throws ContextAggregatorException {
        poaEvent.setModelVersionId("");

        try {
            poaEvent.validate();
        }
        catch (ContextAggregatorException e) {
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyModelInvariantId() throws ContextAggregatorException {
        poaEvent.setModelInvariantId("");

        try {
            poaEvent.validate();
        }
        catch (ContextAggregatorException e) {
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyxFromAppId() throws ContextAggregatorException {
        poaEvent.setxFromAppId("");

        try {
            poaEvent.validate();
        }
        catch (ContextAggregatorException e) {
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyxTransactionId() throws ContextAggregatorException {
        poaEvent.setxTransactionId("");

        try {
            poaEvent.validate();
        }
        catch (ContextAggregatorException e) {
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testToString() {
        String result = poaEvent.toString();
        Assert.assertNotEquals("", result);
    }
}

