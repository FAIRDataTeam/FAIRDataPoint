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
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(
        id = "Migration_0015_FixMetadataVersion",
        order = "0015",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0015_FixMetadataVersion {

    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_VER_UUID = "versionUuid";
    private static final String FIELD_LATEST = "latest";
    private static final String FIELD_VERSION = "versionString";
    private static final String FIELD_DEF = "definition";
    private static final String COL_SCHEMAS = "metadataSchema";

    private final MongoTemplate database;

    private String previousVersionUuid;

    public Migration_0015_FixMetadataVersion(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        final MongoCollection<Document> schemasCol = database.getCollection(COL_SCHEMAS);
        final Document latestResourcesSchema = schemasCol.find(
                Filters.and(
                        Filters.eq(FIELD_UUID, KnownUUIDs.SCHEMA_RESOURCE_UUID),
                        Filters.eq(FIELD_LATEST, true)
                )
        ).first();
        if (latestResourcesSchema == null) {
            return;
        }
        previousVersionUuid = latestResourcesSchema.getString(FIELD_VER_UUID);
        latestResourcesSchema.put(
                FIELD_DEF,
                latestResourcesSchema
                        .getString(FIELD_DEF)
                        .replace(
                                """
                                    sh:path dct:hasVersion ;
                                    sh:name "version" ;
                                    sh:nodeKind sh:Literal ;
                                """,
                                """
                                    sh:path dcat:version ;
                                    sh:nodeKind sh:Literal ;
                                """
                        )
        );
        latestResourcesSchema.remove("_id");
        latestResourcesSchema.put(FIELD_VER_UUID, KnownUUIDs.SCHEMA_V2_RESOURCE_UUID);
        latestResourcesSchema.put(FIELD_LATEST, true);
        final SemVer semVer = new SemVer(latestResourcesSchema.getString(FIELD_VERSION));
        semVer.setPatch(semVer.getPatch() + 1);
        latestResourcesSchema.put(FIELD_VERSION, semVer.toString());
        schemasCol.updateMany(
                Filters.and(
                        Filters.eq(FIELD_UUID, KnownUUIDs.SCHEMA_RESOURCE_UUID),
                        Filters.eq(FIELD_VER_UUID, previousVersionUuid)
                ),
                Updates.set(FIELD_LATEST, false)
        );
        schemasCol.insertOne(latestResourcesSchema);
    }

    @RollbackExecution
    public void rollback() {
        final MongoCollection<Document> schemasCol = database.getCollection(COL_SCHEMAS);
        schemasCol.deleteOne(
                Filters.and(
                        Filters.eq(FIELD_UUID, KnownUUIDs.SCHEMA_RESOURCE_UUID),
                        Filters.eq(FIELD_VER_UUID, KnownUUIDs.SCHEMA_V2_RESOURCE_UUID)
                )
        );
        schemasCol.updateOne(
                Filters.and(
                        Filters.eq(FIELD_UUID, KnownUUIDs.SCHEMA_RESOURCE_UUID),
                        Filters.eq(FIELD_VER_UUID, previousVersionUuid)
                ),
                Updates.set(FIELD_LATEST, true)
        );
    }
}
