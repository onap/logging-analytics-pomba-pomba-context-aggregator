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
import org.onap.pomba.contextaggregator.exception.ContextAggregatorException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class POAEventTest
{
    POAEvent pOAEvent = new POAEvent();

    @Before
    public void setup()
    {
        pOAEvent.setServiceInstanceId("a");
        pOAEvent.setModelVersionId("b");
        pOAEvent.setModelInvariantId("c");
        pOAEvent.setCustomerId("d");
        pOAEvent.setServiceType("e");
        pOAEvent.setxFromAppId("e");
        pOAEvent.setxTransactionId("f");
    }

    @Test
    public void testValidate() throws ContextAggregatorException
    {
        pOAEvent.validate();
    }

    @Test
    public void testValidateEmptyServiceInstanceId() throws ContextAggregatorException {
        pOAEvent.setServiceInstanceId("");
        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyModelVersionId() throws ContextAggregatorException {
        pOAEvent.setModelVersionId("");

        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyModelInvariantId() throws ContextAggregatorException {
        pOAEvent.setModelInvariantId("");

        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyCustomerId() throws ContextAggregatorException {
        pOAEvent.setCustomerId("");

        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyServiceType() throws ContextAggregatorException {
        pOAEvent.setServiceType("");

        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyxFromAppId() throws ContextAggregatorException {
        pOAEvent.setxFromAppId("");

        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testValidateEmptyxTransactionId() throws ContextAggregatorException {
        pOAEvent.setxTransactionId("");

        try {
            pOAEvent.validate();
        }
        catch (ContextAggregatorException e){
            assertTrue(e.getMessage().contains("is missing"));
        }
    }

    @Test
    public void testToString()
    {
        String result = pOAEvent.toString();
        Assert.assertNotEquals("", result);
    }
}

