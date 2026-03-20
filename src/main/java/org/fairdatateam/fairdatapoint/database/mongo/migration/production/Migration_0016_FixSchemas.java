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
package nl.dtls.fairdatapoint.database.mongo.migration.production;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.SneakyThrows;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ResourceReader.loadClassResource;

@ChangeUnit(
        id = "Migration_0016_FixSchemas",
        order = "0016",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0016_FixSchemas {
    private static final String FIELD_VER_UUID = "versionUuid";
    private static final String FIELD_LATEST = "latest";
    private static final String FIELD_DEF = "definition";
    private static final String COL_SCHEMAS = "metadataSchema";

    private final MongoTemplate database;

    public Migration_0016_FixSchemas(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        updateSchema(KnownUUIDs.SCHEMA_V2_RESOURCE_UUID, "resource");
        updateSchema(KnownUUIDs.SCHEMA_V1_CATALOG_UUID, "catalog");
        updateSchema(KnownUUIDs.SCHEMA_V1_DATASET_UUID, "dataset");
        updateSchema(KnownUUIDs.SCHEMA_V1_DISTRIBUTION_UUID, "distribution");
        updateSchema(KnownUUIDs.SCHEMA_V1_DATASERVICE_UUID, "data-service");
        updateSchema(KnownUUIDs.SCHEMA_V1_FDP_UUID, "fdp");
    }

    @SneakyThrows
    private void updateSchema(String versionUuid, String name) {
        final MongoCollection<Document> schemasCol = database.getCollection(COL_SCHEMAS);
        final String definition = loadClassResource(format("0016_shape-%s.ttl", name), getClass());
        schemasCol.updateOne(
                Filters.and(
                        Filters.eq(FIELD_VER_UUID, versionUuid),
                        Filters.eq(FIELD_LATEST, true)
                ),
                Updates.set(FIELD_DEF, definition)
        );
    }

    @RollbackExecution
    public void rollback() {
        // Rollback is not possible
    }
}
