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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ContextBuilder {

    private static final String HOST = "server.host";
    private static final String PORT = "server.port";
    private static final String PROTOCOL = "server.protocol";
    private static final String TRUST_STORE_PATH = "trust.store.path";
    private static final String KEY_STORE_PATH = "key.store.path";
    private static final String KEY_STORE_PASSWORD = "key.store.password";
    private static final String KEY_STORE_TYPE = "key.store.type";
    private static final String KEY_MANAGER_FACTORY_ALGORITHM = "key.manager.factory.algorithm";
    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String CONNECTION_TIMEOUT = "connection.timeout.ms";
    private static final String READ_TIMEOUT = "read.timeout.ms";
    private static final String BASE_URI = "base.uri";
    private static final String USERNAME = "basicauth.username";
    private static final String PASSWORD = "basicauth.password";

    private Properties properties;
    private String contextName;


    /**
     * Instantiates a context builder by loading the given properties file.
     * The context name is extracted from the file name.
     * File name format is expected to be [context-name].properties (ex: aai.properties)
     * @param configFile
     * @throws IOException
     */
    public ContextBuilder(File configFile) throws IOException {
        properties = new Properties();
        InputStream input = new FileInputStream(configFile.getAbsolutePath());
        properties.load(input);
        String fileName = configFile.getName();
        contextName = fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public ContextBuilder(InputStream is, String resName) throws IOException {
        properties = new Properties();
        properties.load(is);
        contextName = resName.substring(0, resName.lastIndexOf('.'));
    }

    public String getContextName() {
        return contextName;
    }

    public String getHost() {
        return properties.getProperty(HOST);
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty(PORT));
    }

    public String getProtocol() {
        return properties.getProperty(PROTOCOL);
    }

    public String getTrustStorePath() {
        return properties.getProperty(TRUST_STORE_PATH);
    }

    public String getKeyStorePath() {
        return properties.getProperty(KEY_STORE_PATH);
    }

    public String getKeyStorePassword() {
        return properties.getProperty(KEY_STORE_PASSWORD);
    }

    public String getKeyStoreType() {
        return properties.getProperty(KEY_STORE_TYPE);
    }

    public String getKeyManagerFactoryAlgorithm() {
        return properties.getProperty(KEY_MANAGER_FACTORY_ALGORITHM);
    }

    public String getSecurityProtocol() {
        return properties.getProperty(SECURITY_PROTOCOL);
    }

    public int getConnectionTimeout() {
        return Integer.parseInt(properties.getProperty(CONNECTION_TIMEOUT));
    }

    public int getReadTimeout() {
        return Integer.parseInt(properties.getProperty(READ_TIMEOUT));
    }

    public String getBaseUri() {
        return properties.getProperty(BASE_URI);
    }
    
    public String getUsername() {
    	return properties.getProperty(USERNAME);
    }
    
    public String getPassword() {
    	return properties.getProperty(PASSWORD);
    }
}
