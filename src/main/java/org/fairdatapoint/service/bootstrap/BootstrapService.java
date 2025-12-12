package org.fairdatapoint.service.bootstrap;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.database.db.repository.FixtureHistoryRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BootstrapService {
    private final FixtureHistoryRepository fixtureHistoryRepository;
    private final String packageNameFormat = "_%s_";
    private final String packageNamePattern = "(?<package>apikey|membership|resource|schema|search|settings|user)";

    public BootstrapService(FixtureHistoryRepository fixtureHistoryRepository) {
        this.fixtureHistoryRepository = fixtureHistoryRepository;
    }

    /**
     * Raises a ValidationException if the filename does not match the specified regular expression pattern.
     * The package name part is required to ensure that {@code removeByPackagename} works as expected.
     * @param filename name of a fixture file
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
     * @param packageName name of an entity package
     */
    public void validatePackageName(String packageName) {
        if (!packageName.matches(packageNamePattern)) {
            throw new ValidationException(
                    "Package name %s does not match pattern %s".formatted(packageName, packageNamePattern));
        }
    }

    /**
     * Returns a list of fixture filenames that have already been applied.
     * @return list of filename strings
     */
    public List<String> getAppliedFixtures() {
        return fixtureHistoryRepository.findAll().stream().map(FixtureHistory::getFilename).toList();
    }

    /**
     * Creates a fixture history record for the specified filename.
     * Raises {@code ValidationException} if filename does not match the required pattern.
     * @param filename name of a fixture file
     */
    public void addToHistory(String filename) {
        validateFilename(filename);
        final FixtureHistory fixtureHistory = fixtureHistoryRepository.save(new FixtureHistory(filename));
        log.debug("Fixture history updated: {} ({})", fixtureHistory.getFilename(), fixtureHistory.getUuid());
    }

    /**
     * Removes records from the fixture history repository based on specified package names.
     * @param packageNames array of package name strings
     */
    public void removeFromHistory(String[] packageNames) {
        log.debug("Removing fixture history for the following packages: {}", String.join(", ", packageNames));
        for (String packageName : packageNames) {
            validatePackageName(packageName);
            fixtureHistoryRepository.deleteByFilenameContains(packageNameFormat.formatted(packageName));
        }
    }
}
