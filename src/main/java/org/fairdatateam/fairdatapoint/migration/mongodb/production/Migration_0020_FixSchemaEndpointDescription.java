/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.migration.mongodb.production;

import com.mongodb.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fairdatateam.fairdatapoint.Profiles;
import org.fairdatateam.fairdatapoint.util.KnownUUIDs;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static org.fairdatateam.fairdatapoint.util.SpringResourceReader.loadClassResource;

/**
 * This migration creates a new version of the default metadata schema for the `DataServiceShape` and marks this as the
 * latest version. The previous version is updated to set latest=false.
 * The new schema SHACL content is obtained from the `resources/.../migration/production/0020_shape-data-service.ttl`
 * file and copied into the `definition` field of the corresponding mongodb document.
 * Changes with respect to the previous schema:
 * - Changed `sh:nodeKind` from `sh:Literal` to `sh:IRI` (to comply with the fdp spec v1.2)
 * - Removed `sh:maxCount 1` (to comply with the fdp spec v1.2, which allows 0 or more)
 * - Changed `dash:viewer` from `dash:LiteralViewer` to `dash:LabelViewer` (to render as a clickable link in fdp-client)
 */
@Slf4j
@ChangeUnit(
        id = "Migration_0020_FixSchemaEndpointDescription",
        order = "0020",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0020_FixSchemaEndpointDescription {

    private static final String COLLECTION_SCHEMAS = "metadataSchema";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_DEFINITION = "definition";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_ID = "_id";
    private static final String FIELD_LATEST = "latest";
    private static final String FIELD_PREVIOUS_VERSION_UUID = "previousVersionUuid";
    private static final String FIELD_VERSION_STRING = "versionString";
    private static final String FIELD_VERSION_UUID = "versionUuid";

    private final MongoTemplate database;

    /**
     * Constructor (autowired)
     */
    public Migration_0020_FixSchemaEndpointDescription(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        createDataServiceV2Schema();
    }

    /**
     * Creates the mongodb metadata schema documents
     */
    @SneakyThrows
    private void createDataServiceV2Schema() {
        // get the mongodb collection containing the metadata schemas
        final MongoCollection<Document> schemas = database.getCollection(COLLECTION_SCHEMAS);
        // get the old schema (V1), which should match the V1 UUID and should be the latest
        final Bson filterSchemaV1 = and(
                eq(FIELD_VERSION_UUID, KnownUUIDs.SCHEMA_V1_DATASERVICE_UUID), eq(FIELD_LATEST, true));
        final Document schemaV1 = schemas.find(filterSchemaV1).first();
        if (schemaV1 == null) {
            log.warn("Skipping migration because the V1 data-service schema was not found or is not the latest.");
            return;
        }
        // unset latest for the old (V1) schema
        schemas.updateOne(filterSchemaV1, set(FIELD_LATEST, false));
        // create new schema (V2) based on the old one (V1)
        final Document schemaV2 = new Document(schemaV1);
        // remove old _id so a new one will be generated upon document insertion
        schemaV2.remove(FIELD_ID);
        schemaV2.replace(FIELD_CREATED_AT, Instant.now());
        schemaV2.replace(FIELD_DEFINITION, loadClassResource("0020_shape-data-service.ttl", getClass()));
        schemaV2.replace(FIELD_DESCRIPTION, "Fixed dcat:endpointDescription conformance to FDP v1.2 spec");
        schemaV2.replace(FIELD_PREVIOUS_VERSION_UUID, KnownUUIDs.SCHEMA_V1_DATASERVICE_UUID);
        // major update because node type changes from Literal to IRI, which is more restrictive for validation
        schemaV2.replace(FIELD_VERSION_STRING, "2.0.0");
        schemaV2.replace(FIELD_VERSION_UUID, KnownUUIDs.SCHEMA_V2_DATASERVICE_UUID);
        if (!schemas.insertOne(schemaV2).wasAcknowledged()) {
            log.warn("Failed to create data-service schema V2.");
        }
    }

    @RollbackExecution
    public void rollback() {
        // Rollback is not possible
    }
}
