package org.fairdatapoint.database.db.repository;

import org.fairdatapoint.database.db.repository.base.BaseRepository;
import org.fairdatapoint.entity.bootstrap.AppliedFixture;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppliedFixtureRepository extends BaseRepository<AppliedFixture> {
    Optional<AppliedFixture> findByFilename(String filename);
}
