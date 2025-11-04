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
