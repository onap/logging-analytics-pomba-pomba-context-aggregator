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

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.onap.pomba.contextaggregator.builder.ContextBuilder;
import org.onap.pomba.contextaggregator.exception.ContextAggregatorError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;


@Configuration
public class BuilderConfigLoader {
    private static final EELFLogger logger = EELFManager.getInstance().getApplicationLogger();
    private static final Resource[] EMPTY_ARRAY = new Resource[0];
    private static final String[] BUILDERS_PROPERTIES_LOCATION = { "file:./%s/*.properties",
            "classpath:/%s/*.properties" };

    @Autowired
    private ResourcePatternResolver rpr;
    @Value("${builders.properties.path}")
    private String buildersPropertiesPath;


    /**
     * Generates a list of context builders by loading property files (*.properties) from a
     * configured builders location.
     *
     * <pre>
     * The location is searched in the following order: file:./${buildersPropertiesPath} and
     * classpath:/${buildersPropertiesPath} to support override of default values.
     *
     * @return Returns a list of context builders
     */
    @Bean
    public List<ContextBuilder> contextBuilders() {
        try {
            final Resource[] blrdsConfig = resolveBldrsConfig();
            if (isEmpty(blrdsConfig)) {
                logger.error(ContextAggregatorError.BUILDER_PROPERTIES_NOT_FOUND
                        .getMessage(Arrays.toString(bldrsPropLoc2Path(buildersPropertiesPath))));
                return Collections.emptyList();
            }

            final List<ContextBuilder> contextBuilders = new ArrayList<>();
            for (Resource r : blrdsConfig) {
                contextBuilders.add(new ContextBuilder(r.getInputStream(), r.getFilename()));
            }

            return contextBuilders;
        } catch (IOException ex) {
            logger.error(ContextAggregatorError.FAILED_TO_LOAD_BUILDER_PROPERTIES.getMessage(ex.getMessage()));
        }

        return Collections.emptyList();
    }

    private Resource[] resolveBldrsConfig() throws IOException {
        for (String p : bldrsPropLoc2Path(buildersPropertiesPath)) {
            Resource[] bldrsConfig = rpr.getResources(p);
            if (!isEmpty(bldrsConfig)) {
                return bldrsConfig;
            }
        }
        return EMPTY_ARRAY;
    }

    private static String[] bldrsPropLoc2Path(String buildersPropertiesPath) {
        String[] res = new String[BUILDERS_PROPERTIES_LOCATION.length];
        int indx = 0;
        for (String tmpl : BUILDERS_PROPERTIES_LOCATION) {
            res[indx++] = format(tmpl, buildersPropertiesPath).replace("//", "/");
        }
        return res;
    }

    private static boolean isEmpty(Object[] obj) {
        return obj == null || obj.length == 0;
    }

}
