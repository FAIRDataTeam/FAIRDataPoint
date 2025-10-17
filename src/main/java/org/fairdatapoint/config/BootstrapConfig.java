/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatapoint.config;

import org.fairdatapoint.config.properties.BootstrapProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

@Configuration
public class BootstrapConfig {
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private final BootstrapProperties bootstrapProperties;

    public BootstrapConfig(BootstrapProperties bootstrapProperties) {
        this.bootstrapProperties = bootstrapProperties;
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        // load all json resources from the fixtures dir
        try {
            // collect fixture resources
            final Path fixturesPath = Path.of(bootstrapProperties.getDbFixturesPath(), "*.json");
            final Resource[] resources = resourceResolver.getResources("file:" + fixturesPath);
            // sort resources to guarantee lexicographic order
            Arrays.sort(
                    resources,
                    Comparator.comparing(
                            Resource::getFilename,
                            Comparator.nullsLast(String::compareTo)
                    )
            );
            factory.setResources(resources);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        return factory;
    }
}
