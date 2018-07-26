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
package org.onap.pomba.contextaggregator.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ConfigurationProperties("classpath:/src/test/resources/GoodProperties/sdnc.properties")
 public class RestRequestTest
{
    @Mock
    Logger log;
    @InjectMocks
    RestRequest restRequest;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetModelData() throws Exception
    {
        File configFile =
                new File("./src/test/resources/GoodProperties/sdnc.properties");
        ContextBuilder contextBuilder = new ContextBuilder(configFile);
//        String result = RestRequest
//                .getModelData(new ContextBuilder(is, "resName"),
//                        new POAEvent());
//        Assert.assertEquals("replaceMeWithExpectedResult", result);
    }
}
