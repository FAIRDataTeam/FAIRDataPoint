package org.fairdatapoint.service.bootstrap;

import jakarta.validation.ValidationException;
import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.database.db.repository.FixtureHistoryRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BootstrapServiceTest extends BaseIntegrationTest {
    final String validFilename = "0100_user_test-user-accounts.json";

    @Autowired
    private BootstrapService bootstrapService;

    @Autowired
    private FixtureHistoryRepository fixtureHistoryRepository;

    @BeforeEach
    public void clearFixtureHistory() {
        fixtureHistoryRepository.deleteAll();
    }

    @Test
    public void testValidateFilenameValid() {
        // test
        assertDoesNotThrow(() -> bootstrapService.validateFilename(validFilename));
    }

    @Test
    public void testValidateFilenameInvalid() {
        // given
        final String invalidFilename = "0100_search_invalid_description.json";
        // test
        assertThrows(ValidationException.class, () -> bootstrapService.validateFilename(invalidFilename));
    }

    @Test
    public void testValidatePackageNameValid() {
        // given
        final String validPackageName = "user";
        // test
        assertDoesNotThrow(() -> bootstrapService.validatePackageName(validPackageName));
    }

    @Test
    public void testValidatePackageNameInvalid() {
        // given
        final String invalidPackageName = "users";
        // test
        assertThrows(ValidationException.class, () -> bootstrapService.validatePackageName(invalidPackageName));
    }

    @Test
    public void testGetAppliedFixtures() {
        // given
        fixtureHistoryRepository.saveAndFlush(new FixtureHistory(validFilename));
        // test
        assertEquals(List.of(validFilename), bootstrapService.getAppliedFixtures());
    }

    @Test
    public void testAddToHistory() {
        // test
        bootstrapService.addToHistory(validFilename);
        assertTrue(fixtureHistoryRepository.findByFilename(validFilename).isPresent());
    }

    @Test
    public void testRemoveFromHistory() {
        // given
        final String remainingFilename = "0003_resource_dummy-resource-definitions.json";
        final List<String> filenames = List.of(
                "0001_user_dummy-user-account-1.json",
                "0002_user_dummy-user-account-2.json",
                remainingFilename
        );
        for (String filename : filenames) {
            fixtureHistoryRepository.saveAndFlush(new FixtureHistory(filename));
        }
        assertEquals(filenames.size(), fixtureHistoryRepository.count());
        // test
        final String[] packageNames = {"user"};
        bootstrapService.removeFromHistory(packageNames);
        assertEquals(1, fixtureHistoryRepository.count());
        assertTrue(fixtureHistoryRepository.findByFilename(remainingFilename).isPresent());
    }
}
