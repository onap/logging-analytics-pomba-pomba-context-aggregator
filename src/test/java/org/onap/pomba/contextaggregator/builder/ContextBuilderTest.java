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
package org.onap.pomba.contextaggregator.builder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ContextBuilderTest
{
    private ContextBuilder contextBuilder;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEntity() throws Exception
    {
        File configFile =
                new File("./src/test/resources/GoodProperties/sdnc.properties");
        contextBuilder = new ContextBuilder(configFile);
        Assert.assertEquals(contextBuilder.getContextName(), "sdnc");
        Assert.assertEquals(contextBuilder.getHost(), "sdnchost");
        Assert.assertEquals(contextBuilder.getPort(), 1000);
        Assert.assertEquals(contextBuilder.getProtocol(), "http");
        Assert.assertEquals(contextBuilder.getTrustStorePath(),
                "/auth/tomcat_keystore");
        Assert.assertEquals(contextBuilder.getKeyStorePath(),
                "auth/aai-client-cert.p12");
        Assert.assertEquals(contextBuilder.getKeyStorePassword(),
                "70c87528c88dcd9f9c2558d30e817868");
        Assert.assertEquals(contextBuilder.getKeyStoreType(), "PKCS12");
        Assert.assertEquals(contextBuilder.getKeyManagerFactoryAlgorithm(),
                "SunX509");
        Assert.assertEquals(contextBuilder.getSecurityProtocol(), "TLS");
        Assert.assertEquals(contextBuilder.getConnectionTimeout(), 5000);
        Assert.assertEquals(contextBuilder.getReadTimeout(), 1000);
        Assert.assertEquals(contextBuilder.getBaseUri(),
                "/sdnccontextbuilder/service/context");
    }
}