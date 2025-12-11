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

import jakarta.validation.ValidationException;
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
import java.util.*;

/**
 * The {@code BootstrapConfig} class configures a repository populator that loads initial data into the relational
 * database, based on JSON fixture files.
 * The default fixture files are located in the {@code <project-root>/fixtures} directory.
 * Additional fixture directories can also be specified, using the {@code dbFixturesDirs} property.
 * Fixture files are collected from all specified directories and are applied in lexicographic order.
 * A FixtureHistory repository keeps track of fixture files that have been applied, so they are only applied once.
 * To add custom fixtures and/or override any of the default fixtures in a docker compose setup, we can bind-mount
 * individual fixture files or entire directories.
 * For example: {@code ./my-fixtures/0100_user-accounts.json:/fdp/fixtures/0100_user-accounts.json:ro}
 * Note that bind-mounting the entire directory, instead of individual files, would hide all default files.
 */
@Configuration
@Slf4j
public class BootstrapConfig {
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private final BootstrapProperties bootstrap;
    private final FixtureHistoryRepository fixtureHistoryRepository;

    public BootstrapConfig(BootstrapProperties bootstrapProperties, FixtureHistoryRepository fixtureHistoryRepository) {
        this.bootstrap = bootstrapProperties;
        this.fixtureHistoryRepository = fixtureHistoryRepository;
    }

    /**
     * Raises a ValidationException if the filename does not match the specified regular expression.
     * This is required to ensure that the reset service works as expected.
     * @param filename name of a fixture file
     */
    public void validateFixtureFilename(String filename) {
        final String pattern = "^(?<order>[0-9]{4})_(?<package>[a-z]+)_(?<description>[a-zA-Z0-9\\-]+)\\.json$";
        if (!filename.matches(pattern)) {
            throw new ValidationException("Filename %s does not match pattern %s".formatted(filename, pattern));
        }
    }

    /**
     * Creates a sorted array of unique fixture resources, representing files in the specified fixtures directories.
     * Checks the fixture history repository for files that have already been applied, and removes them from the array.
     * @return sorted array of unique Resource objects
     */
    public Resource[] getNewResources() {
        // use TreeSet with comparator for lexicographic order and uniqueness
        final SortedSet<Resource> resources = new TreeSet<>(
                Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareTo)));
        // collect fixture resources from specified directories
        log.info("Looking for db fixtures in the following directories: {}",
                String.join(", ", this.bootstrap.getDbFixturesDirs()));
        for (String fixturesDir : this.bootstrap.getDbFixturesDirs()) {
            // Path.of() removes trailing slashes, so it is safe to concatenate "/*.json".
            // Note that Path.of(fixturesDir).resolve("*.json") could work on unix but fails on windows.
            final String locationPattern = "file:" + Path.of(fixturesDir) + "/*.json";
            try {
                resources.addAll(List.of(resourceResolver.getResources(locationPattern)));
            }
            catch (IOException exception) {
                log.error("Failed to resolve fixture resources", exception);
            }
        }
        // remove resources that have been applied already
        final List<String> appliedFixtures = fixtureHistoryRepository.findAll().stream()
                .map(FixtureHistory::getFilename).toList();
        final List<Resource> resourcesToSkip = resources.stream()
                .filter(resource -> appliedFixtures.contains(resource.getFilename())).toList();
        resourcesToSkip.forEach(resources::remove);
        // validate filenames
        resources.forEach(resource -> validateFixtureFilename(Objects.requireNonNull(resource.getFilename())));
        // return the result
        log.info("Found {} new db fixture files ({} have been applied already)",
                resources.size(), resourcesToSkip.size());
        return resources.toArray(new Resource[0]);
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulatorFactoryBean() {
        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        if (this.bootstrap.isEnabled()) {
            log.info("Bootstrap repository populator enabled");
            // add resources to factory
            factory.setResources(getNewResources());
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
            for (final Resource resource : getNewResources()) {
                final String filename = resource.getFilename();
                final FixtureHistory fixtureHistory = fixtureHistoryRepository.save(new FixtureHistory(filename));
                log.debug("Fixture history updated: {} ({})", fixtureHistory.getFilename(), fixtureHistory.getUuid());
            }
        }
    }
}
