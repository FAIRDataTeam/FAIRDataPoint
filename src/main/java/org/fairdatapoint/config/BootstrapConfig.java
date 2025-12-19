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

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.config.properties.BootstrapProperties;
import org.fairdatapoint.service.bootstrap.BootstrapService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.RepositoriesPopulatedEvent;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * The {@code BootstrapConfig} class configures a repository populator that loads initial data into the relational
 * database, based on JSON fixture files.
 * The fixture filenames must match the regular expression pattern specified in {@code BootstrapService},
 * which can be described as {@code <zero-padded-number>_<entity-package-name>_<description>.json}.
 * The default fixture files are located in the {@code <project-root>/fixtures} directory.
 * Additional fixture directories can also be specified, using the {@code bootstrap.locations} property.
 * Fixture files are collected from all specified directories and are applied in lexicographic order.
 * A fixture history repository keeps track of fixture files that have been applied, so they are only applied once.
 * To add custom fixtures and/or override any of the default fixtures in a docker compose setup, we can bind-mount
 * individual fixture files or entire directories.
 * For example: {@code ./my-fixtures/0100_user_user-accounts.json:/fdp/fixtures/0100_user_user-accounts.json:ro}
 * Note that bind-mounting the entire directory, instead of individual files, would hide all default files.
 */
@Configuration
@Slf4j
public class BootstrapConfig {
    private final BootstrapProperties bootstrapProperties;
    private final BootstrapService bootstrapService;

    /**
     * Constructor (autowired).
     * @param bootstrapProperties Bootstrap properties.
     * @param bootstrapService Bootstrap service.
     */
    public BootstrapConfig(BootstrapProperties bootstrapProperties, BootstrapService bootstrapService) {
        this.bootstrapProperties = bootstrapProperties;
        this.bootstrapService = bootstrapService;
    }

    /**
     * Sets up a factory bean which creates a repository populator that takes care of loading initial data
     * from JSON fixture files into the relational database.
     * @return Repository populator factory bean.
     */
    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulatorFactoryBean() {
        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        if (bootstrapProperties.isEnabled()) {
            log.info("Bootstrap repository populator enabled");
            // add resources to factory
            factory.setResources(bootstrapService.getNewResources());
        }
        else {
            log.info("Bootstrap repository populator disabled");
        }
        return factory;
    }

    /**
     * Updates the fixture history after the repository populator has finished.
     */
    @Component
    public class RepositoriesPopulatedEventListener implements ApplicationListener<RepositoriesPopulatedEvent> {
        @Override
        public void onApplicationEvent(@NotNull RepositoriesPopulatedEvent event) {
            log.info("Repository populator finished.");
            if (event.getSource() instanceof ResourceReaderRepositoryPopulator populator) {
                try {
                    // use reflection to make the private resources field accessible
                    final Field resourcesField = populator.getClass().getDeclaredField("resources");
                    resourcesField.setAccessible(true);
                    // add the populator's resources to history
                    if (resourcesField.get(populator) instanceof Collection<?> collection) {
                        log.info("Updating fixture history with {} resources", collection.size());
                        bootstrapService.updateHistory(collection.toArray(new Resource[0]));
                    }
                }
                catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException exception) {
                    log.error("Failed to access resources field of ResourceReaderRepositoryPopulator", exception);
                }
            }
        }
    }
}
