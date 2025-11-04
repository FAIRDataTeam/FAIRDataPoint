package org.fairdatapoint.database.db.repository;

import org.fairdatapoint.database.db.repository.base.BaseRepository;
import org.fairdatapoint.entity.bootstrap.FixtureHistory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixtureHistoryRepository extends BaseRepository<FixtureHistory> {
    Optional<FixtureHistory> findByFilename(String filename);
}
