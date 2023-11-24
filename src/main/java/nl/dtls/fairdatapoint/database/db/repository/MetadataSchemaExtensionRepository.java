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

import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetadataSchemaExtensionRepository extends JpaRepository<MetadataSchemaExtension, UUID> {

    List<MetadataSchemaExtension> findByExtendedMetadataSchema(MetadataSchema schema);

    @Query(
            """
                SELECT e.extendedMetadataSchema.uuid
                FROM MetadataSchemaExtension e
                WHERE e.metadataSchemaVersion.uuid = :uuid
            """
    )
    List<UUID> getExtendedSchemaUuids(UUID uuid);

    @Query(
            """
                SELECT e.uuid as uuid, v.schema.uuid as schemaUuid,
                       e.metadataSchemaVersion.uuid as versionUuid,
                       e.extendedMetadataSchema.uuid as extendedMetadataSchemaUuid
                FROM MetadataSchemaExtension e JOIN  MetadataSchemaVersion v ON e.metadataSchemaVersion.uuid = v.uuid
                WHERE v.state = 'LATEST'
            """
    )
    Iterable<MetadataSchemaExtensionBasic> getBasicExtensionsForLatest();

    interface MetadataSchemaExtensionBasic {
        UUID getUuid();

        UUID getSchemaUuid();

        UUID getVersionUuid();

        UUID getExtendedMetadataSchemaUuid();
    }
}
