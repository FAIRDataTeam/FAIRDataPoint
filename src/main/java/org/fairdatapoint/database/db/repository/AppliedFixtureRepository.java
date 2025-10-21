package org.fairdatapoint.database.db.repository;

import org.fairdatapoint.database.db.repository.base.BaseRepository;
import org.fairdatapoint.entity.bootstrap.AppliedFixture;

import java.util.Optional;

public interface AppliedFixtureRepository extends BaseRepository<AppliedFixture> {
    Optional<AppliedFixture> findByFilename(String filename);
}
