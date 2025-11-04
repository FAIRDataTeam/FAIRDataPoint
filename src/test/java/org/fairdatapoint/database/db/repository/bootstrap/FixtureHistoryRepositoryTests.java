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
package org.fairdatapoint.database.db.repository.bootstrap;

import jakarta.transaction.Transactional;
import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.database.db.repository.FixtureHistoryRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureTestEntityManager
@Transactional
public class FixtureHistoryRepositoryTests extends BaseIntegrationTest {
    @Autowired
    FixtureHistoryRepository repository;

    final String filename = "0001-whatever.json";

    @Test
    public void testSave() {
        FixtureHistory fixtureHistory = repository.saveAndFlush(new FixtureHistory(filename));
        assertEquals(filename, fixtureHistory.getFilename());
        assertEquals(1, repository.count());
    }

    @Test
    public void testSaveWithExistingFilename() {
        repository.saveAndFlush(new FixtureHistory(filename));
        assertEquals(1, repository.count());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(new FixtureHistory(filename)),
                "filename is not unique, but no exception was raised"
        );
    }

    @Test
    public void testSaveWithoutFilename() {
        assertThrows(
                Exception.class,
                () -> repository.saveAndFlush(new FixtureHistory()),
                "filename was not provided, but no exception was raised"
        );
    }

    @Test
    public void testFindByFilenameWithExistingFilename() {
        repository.saveAndFlush(new FixtureHistory(filename));
        Optional<FixtureHistory> appliedFixture = repository.findByFilename(filename);
        assertTrue(appliedFixture.isPresent());
    }
}
