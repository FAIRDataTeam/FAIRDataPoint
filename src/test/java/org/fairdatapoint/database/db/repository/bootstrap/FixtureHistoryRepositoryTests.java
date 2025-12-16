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

import jakarta.persistence.EntityManagerFactory;
import org.fairdatapoint.database.db.repository.FixtureHistoryRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FixtureHistoryRepositoryTests {
    final String filename = "0001-whatever.json";

    @Autowired
    FixtureHistoryRepository repository;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    public void removeAllEntities() {
        // remove all data without dropping entire tables
        // based on https://vladmihalcea.com/clean-up-test-data-spring/
        entityManagerFactory.unwrap(SessionFactoryImplementor.class).getSchemaManager().truncateMappedObjects();
    }

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
