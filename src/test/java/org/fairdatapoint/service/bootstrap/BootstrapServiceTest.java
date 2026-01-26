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
