package org.fairdatapoint.acceptance.bootstrap;

import jakarta.transaction.Transactional;
import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.database.db.repository.AppliedFixtureRepository;
import org.fairdatapoint.entity.bootstrap.AppliedFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureTestEntityManager
@Transactional
public class AppliedFixtureRepositoryTests extends BaseIntegrationTest {
    @Autowired
    AppliedFixtureRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    final String filename = "0001-whatever.json";

    @Test
    public void testSave() {
        AppliedFixture appliedFixture = repository.save(new AppliedFixture(filename));
        assertEquals(filename, appliedFixture.getFilename());
        assertEquals(1, repository.count());
    }

    @Test
    public void testSaveWithExistingFilename() {
        this.entityManager.persist(new AppliedFixture(filename));
        assertEquals(1, repository.count());
//        repository.save(new AppliedFixture(filename));
//        assertEquals(1, repository.count());
        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.save(new AppliedFixture(filename)),
                "filename is not unique, but no exception was raised"
        );
    }

    @Test
    public void testSaveWithoutFilename() {
        assertThrows(
                Exception.class,
                () -> repository.save(new AppliedFixture()),
                "filename was not provided, but no exception was raised"
        );
    }

    @Test
    public void testFindByFilenameWithExistingFilename() {
        this.entityManager.persist(new AppliedFixture(filename));
        Optional<AppliedFixture> appliedFixture = repository.findByFilename(filename);
        assertTrue(appliedFixture.isPresent());
    }
}
