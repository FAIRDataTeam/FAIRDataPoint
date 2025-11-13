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
import org.fairdatapoint.database.db.repository.FixtureHistoryRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.RepositoriesPopulatedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The {@code BootstrapConfig} class configures a repository populator to load initial data into the relational
 * database, based on JSON fixture files.
 * Bootstrapping is disabled by default, and should only be enabled once, on the very first run of the application.
 * It can also be enabled on subsequent runs, but then it will overwrite any changes that may have been made by users.
 * To enable on the first run, set the env variable {@code BOOTSTRAP_ENABLED=true} on the command line, before running
 * the app.
 * When using e.g. docker compose, you can define {@code BOOTSTRAP_ENABLED: ${BOOTSTRAP_ENABLED:-false}} in the
 * {@code environment} section and then set up the stack by running {@code BOOTSTRAP_ENABLED=true docker compose up -d}.
 * The default fixtures are located in the {@code <project-root>/fixtures} directory.
 * To add custom fixtures and/or override any of the default fixtures in a docker compose setup, we can bind-mount
 * individual fixture files.
 * For example: {@code ./my-fixtures/0100_user-accounts.json:/fdp/fixtures/0100_user-accounts.json:ro}
 * Note that bind-mounting the entire directory, instead of individual files, would hide all default files.
 */
@Configuration
@Slf4j
public class BootstrapConfig {
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private final BootstrapProperties bootstrap;
    private final FixtureHistoryRepository fixtureHistoryRepository;
    private final List<Resource> resources = new ArrayList<>();

    public BootstrapConfig(BootstrapProperties bootstrapProperties, FixtureHistoryRepository fixtureHistoryRepository) {
        this.bootstrap = bootstrapProperties;
        this.fixtureHistoryRepository = fixtureHistoryRepository;
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        if (this.bootstrap.isEnabled()) {
            log.info("Bootstrap repository populator enabled");
            try {
                // collect fixture resources
                log.info("Looking for db fixtures in the following directories: {}",
                        String.join(", ", this.bootstrap.getDbFixturesDirs()));
                for (String fixturesDir : this.bootstrap.getDbFixturesDirs()) {
                    String locationPattern = "file:" + Path.of(fixturesDir).resolve(".json");
                    // ugly workaround for Path.resolve("*.json") failure on windows
                    locationPattern = locationPattern.replaceAll(".json$", "*.json");
                    resources.addAll(List.of(resourceResolver.getResources(locationPattern)));
                }
                // remove resources that have been applied already
                final List<String> appliedFixtures = fixtureHistoryRepository.findAll().stream()
                        .map(FixtureHistory::getFilename).toList();
                final List<Resource> resourcesToSkip = resources.stream()
                        .filter(resource -> appliedFixtures.contains(resource.getFilename())).toList();
                resources.removeAll(resourcesToSkip);
                // sort resources to guarantee lexicographic order
                resources.sort(Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareTo)));
                // add resources to factory
                log.info("Applying {} db fixtures ({} have been applied already)",
                        resources.size(), resourcesToSkip.size());
                factory.setResources(resources.toArray(new Resource[0]));
            }
            catch (IOException exception) {
                log.error("Failed to load relational database fixtures", exception);
            }
        }
        else {
            log.info("Bootstrap repository populator disabled");
        }

        return factory;
    }

    @Component
    public class RepositoriesPopulatedEventListener implements ApplicationListener<RepositoriesPopulatedEvent> {
        @Override
        public void onApplicationEvent(@NotNull RepositoriesPopulatedEvent event) {
            log.info("Repositories populated");
            // Create fixture history records for all resources that have been applied.
            // Note: This assumes that all items in the resources list have been *successfully* applied. However, I'm
            // not sure if this can be guaranteed. If it does turn out to be a problem, we could try e.g. extending the
            // ResourceReaderRepositoryPopulator.persist() method, so the history record is added there.
            for (final Resource resource : resources) {
                final String filename = resource.getFilename();
                final FixtureHistory fixtureHistory = fixtureHistoryRepository.save(new FixtureHistory(filename));
                log.debug("Fixture history updated: {} ({})", fixtureHistory.getFilename(), fixtureHistory.getUuid());
            }
        }
    }
}
