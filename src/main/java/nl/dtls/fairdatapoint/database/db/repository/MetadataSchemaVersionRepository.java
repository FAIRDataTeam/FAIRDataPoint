/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.database.db.repository;

import nl.dtls.fairdatapoint.database.db.repository.base.BaseRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaState;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetadataSchemaVersionRepository extends BaseRepository<MetadataSchemaVersion> {

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv JOIN MetadataSchema ms ON msv.schema = ms
        WHERE ms.uuid = :uuid AND msv.state = 'DRAFT'
    """)
    Optional<MetadataSchemaVersion> getDraftBySchemaUuid(UUID uuid);

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv
        WHERE msv.uuid = :uuid AND msv.state = 'DRAFT'
    """)
    Optional<MetadataSchemaVersion> getDraftBySchemaVersionUuid(UUID uuid);

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv JOIN MetadataSchema ms ON msv.schema = ms
        WHERE msv.state = 'LATEST'
    """)
    List<MetadataSchemaVersion> getAllLatest();

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv JOIN MetadataSchema ms ON msv.schema = ms
        WHERE ms.uuid = :uuid AND msv.state = 'LATEST'
    """)
    Optional<MetadataSchemaVersion> getLatestBySchemaUuid(UUID uuid);

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv
        WHERE msv.uuid = :uuid AND msv.state = 'LATEST'
    """)
    Optional<MetadataSchemaVersion> getLatestBySchemaVersionUuid(UUID uuid);

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv JOIN MetadataSchema ms ON msv.schema = ms
        WHERE ms.uuid = :uuid AND msv.version = :version
    """)
    Optional<MetadataSchemaVersion> getBySchemaUuidAndVersion(UUID uuid, String version);

    @Query("""
        SELECT msv
        FROM MetadataSchemaVersion msv JOIN MetadataSchema ms ON msv.schema = ms
        WHERE ms.uuid = :uuid
    """)
    List<MetadataSchemaVersion> getBySchemaUuid(UUID uuid);

    List<MetadataSchemaVersion> findAllByImportedFromIsNotNull();

    List<MetadataSchemaVersion> findAllByState(MetadataSchemaState state);

    List<MetadataSchemaVersion> findAllByPublishedIsTrue();
}
