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
package org.fairdatapoint.service.bootstrap;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.config.properties.BootstrapProperties;
import org.fairdatapoint.database.db.repository.FixtureHistoryRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Slf4j
@Service
public class BootstrapService {

    private final ApplicationContext applicationContext;

    private final BootstrapProperties bootstrapProperties;

    private final FixtureHistoryRepository fixtureHistoryRepository;

    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final String packageNameFormat = "_%s_";

    private final String packageNamePattern = "(?<package>apikey|membership|resource|schema|search|settings|user)";

    /**
     * Constructor (autowired).
     * @param applicationContext Spring application context
     * @param bootstrapProperties Bootstrap properties
     * @param fixtureHistoryRepository Fixture history repository
     */
    public BootstrapService(
            ApplicationContext applicationContext,
            BootstrapProperties bootstrapProperties,
            FixtureHistoryRepository fixtureHistoryRepository
    ) {
        this.applicationContext = applicationContext;
        this.bootstrapProperties = bootstrapProperties;
        this.fixtureHistoryRepository = fixtureHistoryRepository;
    }

    /**
     * Raises a ValidationException if the filename does not match the specified regular expression pattern.
     * The package name part is required to ensure that {@code removeByPackagename} works as expected.
     * @param filename Name of a fixture file
     */
    public void validateFilename(String filename) {
        final String pattern = "^(?<order>[0-9]{4})"
                + packageNameFormat.formatted(packageNamePattern)
                + "(?<description>[a-zA-Z0-9\\-]+)\\.json$";
        if (!filename.matches(pattern)) {
            throw new ValidationException("Filename %s does not match pattern %s".formatted(filename, pattern));
        }
    }

    /**
     * Raises a ValidationException if the package name does not match the specified regular expression pattern.
     * @param packageName Name of an entity package
     */
    public void validatePackageName(String packageName) {
        if (!packageName.matches(packageNamePattern)) {
            throw new ValidationException(
                    "Package name %s does not match pattern %s".formatted(packageName, packageNamePattern));
        }
    }

    /**
     * Creates a sorted array of unique fixture resources, representing files in the specified fixtures directories.
     * Checks the fixture history repository for files that have already been applied and removes them from the array.
     * @return Sorted array of unique {@code Resource} objects
     */
    public Resource[] getNewResources() {
        // use TreeSet with comparator for lexicographic order and uniqueness
        final SortedSet<Resource> resources = new TreeSet<>(
                Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareTo)));
        // collect fixture resources from specified directories
        for (String location : bootstrapProperties.getLocations()) {
            // Only look for JSON files
            String locationPattern = location;
            if (!locationPattern.endsWith(".json")) {
                // naive append may lead to redundant slashes, but the OS ignores those
                locationPattern += "/*.json";
            }
            try {
                log.info("Fixture resources location: {}", locationPattern);
                resources.addAll(List.of(resourceResolver.getResources(locationPattern)));
            }
            catch (IOException exception) {
                log.error("Failed to resolve fixture resources", exception);
            }
        }
        // remove resources that have been applied already
        final List<Resource> resourcesToSkip = resources.stream()
                .filter(resource -> getAppliedFixtures().contains(resource.getFilename()))
                .toList();
        resourcesToSkip.forEach(resources::remove);
        // return the result
        log.info("Found {} new db fixture files ({} have been applied already)",
                resources.size(), resourcesToSkip.size());
        return resources.toArray(new Resource[0]);
    }

    /**
     * Returns a list of fixture filenames that have already been applied.
     * @return List of filename strings
     */
    public List<String> getAppliedFixtures() {
        return fixtureHistoryRepository.findAll().stream().map(FixtureHistory::getFilename).toList();
    }

    /**
     * Creates a fixture history record for the specified filename.
     * Raises {@code ValidationException} if filename does not match the required pattern.
     * @param filename Name of a fixture file
     */
    public void addToHistory(String filename) {
        validateFilename(filename);
        final FixtureHistory fixtureHistory = fixtureHistoryRepository.save(new FixtureHistory(filename));
        log.debug("Fixture history updated: {} ({})", fixtureHistory.getFilename(), fixtureHistory.getUuid());
    }

    /**
     * Removes records from the fixture history repository based on specified package names.
     * @param packageNames Array of package name strings
     */
    public void removeFromHistory(String[] packageNames) {
        log.debug("Removing fixture history for the following packages: {}", String.join(", ", packageNames));
        for (String packageName : packageNames) {
            validatePackageName(packageName);
            fixtureHistoryRepository.deleteByFilenameContains(packageNameFormat.formatted(packageName));
        }
    }

    /**
     * Creates fixture history records for all new resources.
     * This assumes that all new resources have been applied successfully.
     */
    public void updateHistory() {
        // Note that it may not be guaranteed that all new resources have been applied successfully at this point.
        // If this turns out to be a problem, we could try e.g. adding the history record by extending the
        // ResourceReaderRepositoryPopulator.persist() method.
        for (final Resource resource : getNewResources()) {
            addToHistory(resource.getFilename());
            log.debug("Fixture history updated: {}", resource.getFilename());
        }
    }

    /**
     * Reloads data from JSON fixture files into the relational database.
     * This works by clearing history records for the specified packages and then re-running the repository populator.
     * Note that it may be necessary to delete existing entities from the relevant repositories first.
     * @param packageNames Array of names of entity packages to be repopulated
     */
    public void repopulate(String[] packageNames, ResourceReaderRepositoryPopulator populator) {
        removeFromHistory(packageNames);
        populator.setResources(getNewResources());
        populator.populate(new Repositories(applicationContext));
    }
}
