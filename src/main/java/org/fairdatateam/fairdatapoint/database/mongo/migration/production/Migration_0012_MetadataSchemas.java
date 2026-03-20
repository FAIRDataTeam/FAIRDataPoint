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
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaShaclUtils;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.*;

@ChangeUnit(id = "Migration_0012_MetadataSchemas", order = "0012", author = "migrationBot")
@Profile(Profiles.PRODUCTION)
public class Migration_0012_MetadataSchemas {

    private static final String FDP_APP_URL = "https://purl.org/fairdatapoint/app";
    private static final String COL_SHAPES = "shape";
    private static final String COL_SCHEMAS = "metadataSchema";
    private static final String COL_DRAFTS = "metadataSchemaDraft";
    private static final String COL_RD = "resourceDefinition";
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DEFINITION = "definition";
    private static final String FIELD_CLASSES = "targetClasses";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_LATEST = "latest";
    private static final String FIELD_PUBLISHED = "published";
    private static final String FIELD_SHAPES = "shapeUuids";
    private static final String FIELD_SCHEMAS = "metadataSchemaUuids";
    private static final String TYPE_INTERNAL = "INTERNAL";
    private static final String TYPE_CUSTOM = "CUSTOM";
    private static final String KEY_RESOURCE = "r";
    private static final String KEY_DATA_SERVICE = "d";
    private static final String KEY_METADATA_SERVICE = "m";

    private final MongoTemplate database;

    public Migration_0012_MetadataSchemas(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run(ResourceDefinitionCache resourceDefinitionCache,
                    ResourceDefinitionTargetClassesCache targetClassesCache) {
        updateInternalShapesType();
        updateResourceDefinitionLinks();

        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
    }

    private void updateInternalShapesType() {
        final MongoCollection<Document> shapeCol = database.getCollection(COL_SHAPES);
        final MongoCollection<Document> schemaCol = database.createCollection(COL_SCHEMAS);
        database.createCollection(COL_DRAFTS);

        // Check default schemas presence
        final Map<String, Boolean> existsMap = Map.of(
                KEY_RESOURCE, docWithUuidExists(shapeCol, KnownUUIDs.SCHEMA_RESOURCE_UUID),
                KEY_DATA_SERVICE, docWithUuidExists(shapeCol, KnownUUIDs.SCHEMA_DATASERVICE_UUID),
                KEY_METADATA_SERVICE, docWithUuidExists(shapeCol, KnownUUIDs.SCHEMA_METADATASERVICE_UUID)
        );

        // Migrate shapes to schemas
        shapeCol.find().forEach(shapeDoc -> schemaCol.insertOne(shapeToSchema(shapeDoc, existsMap)));
        database.dropCollection(COL_SHAPES);
    }

    private Document shapeToSchema(Document shape, Map<String, Boolean> existsMap) {
        final SemVer version = new SemVer("1.0.0");
        final Instant now = Instant.now();
        final String schemaUuid = shape.getString(FIELD_UUID);
        // Internal shapes
        String origin = null;
        String versionUuid = UUID.randomUUID().toString();
        String suggestedResourceName = null;
        String suggestedUrlPrefix = null;
        boolean isAbstract = false;
        if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_RESOURCE_UUID)) {
            isAbstract = true;
            versionUuid = KnownUUIDs.SCHEMA_V1_RESOURCE_UUID;
            origin = FDP_APP_URL;
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_FDP_UUID)) {
            versionUuid = KnownUUIDs.SCHEMA_V1_FDP_UUID;
            origin = FDP_APP_URL;
            suggestedResourceName = "FAIR Data Point";
            suggestedUrlPrefix = "";
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_DATASERVICE_UUID)) {
            versionUuid = KnownUUIDs.SCHEMA_V1_DATASERVICE_UUID;
            origin = FDP_APP_URL;
            suggestedResourceName = "Data Service";
            suggestedUrlPrefix = "data-service";
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_METADATASERVICE_UUID)) {
            versionUuid = KnownUUIDs.SCHEMA_V1_METADATASERVICE_UUID;
            origin = FDP_APP_URL;
            suggestedResourceName = "Metadata Service";
            suggestedUrlPrefix = "metadata-service";
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_CATALOG_UUID)) {
            versionUuid = KnownUUIDs.SCHEMA_V1_CATALOG_UUID;
            origin = FDP_APP_URL;
            suggestedResourceName = "Catalog";
            suggestedUrlPrefix = "catalog";
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_DATASET_UUID)) {
            versionUuid = KnownUUIDs.SCHEMA_V1_DATASET_UUID;
            origin = FDP_APP_URL;
            suggestedResourceName = "Dataset";
            suggestedUrlPrefix = "dataset";
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_DISTRIBUTION_UUID)) {
            versionUuid = KnownUUIDs.SCHEMA_V1_DISTRIBUTION_UUID;
            origin = FDP_APP_URL;
            suggestedResourceName = "Distribution";
            suggestedUrlPrefix = "distribution";
        }
        // Prepare
        final Document schemaDoc = new Document();
        schemaDoc.append(FIELD_UUID, schemaUuid);
        schemaDoc.append("versionUuid", versionUuid);
        schemaDoc.append("versionString", version.toString());
        schemaDoc.append(FIELD_NAME, shape.getString(FIELD_NAME));
        schemaDoc.append("description", "");
        schemaDoc.append(FIELD_DEFINITION, shape.getString(FIELD_DEFINITION));
        schemaDoc.append(FIELD_CLASSES,
                MetadataSchemaShaclUtils.extractTargetClasses(shape.getString(FIELD_DEFINITION)));
        schemaDoc.append("extendSchemas", prepareExtends(schemaUuid, existsMap));
        schemaDoc.append(FIELD_TYPE, shape.get(FIELD_TYPE));
        schemaDoc.append("origin", origin);
        schemaDoc.append("importedFrom", origin);
        schemaDoc.append(FIELD_LATEST, true);
        schemaDoc.append("previousVersionUuid", null);
        schemaDoc.append(FIELD_PUBLISHED, shape.getBoolean(FIELD_PUBLISHED, false));
        schemaDoc.append("abstractSchema", isAbstract);
        schemaDoc.append("suggestedResourceName", suggestedResourceName);
        schemaDoc.append("suggestedUrlPrefix", suggestedUrlPrefix);
        schemaDoc.append("createdAt", now);
        return schemaDoc;
    }

    private List<String> prepareExtends(String schemaUuid, Map<String, Boolean> existsMap) {
        final List<String> extendSchemas = new ArrayList<>();
        if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_FDP_UUID) && existsMap.get(KEY_METADATA_SERVICE)) {
            // FAIRDataPoint extends MetadataService
            extendSchemas.add(KnownUUIDs.SCHEMA_METADATASERVICE_UUID);
        }
        else if (Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_METADATASERVICE_UUID)
                && existsMap.get(KEY_DATA_SERVICE)) {
            // MetadataService extends DataService
            extendSchemas.add(KnownUUIDs.SCHEMA_DATASERVICE_UUID);
        }
        else if (!Objects.equals(schemaUuid, KnownUUIDs.SCHEMA_RESOURCE_UUID) && existsMap.get(KEY_RESOURCE)) {
            // Everything else (except Resource) extends Resource
            extendSchemas.add(KnownUUIDs.SCHEMA_RESOURCE_UUID);
        }
        return extendSchemas;
    }

    private void updateResourceDefinitionLinks() {
        final MongoCollection<Document> rdCol = database.getCollection(COL_RD);
        // Rename shape to metadata schema
        rdCol.updateMany(
                Filters.exists(FIELD_SHAPES),
                Updates.rename(FIELD_SHAPES, FIELD_SCHEMAS)
        );
        // Remove Resource link (it is abstract)
        rdCol.find().forEach(rdDoc -> {
            final ArrayList<String> metadataSchemaUuids = (ArrayList<String>) rdDoc.get(FIELD_SCHEMAS);
            if (metadataSchemaUuids.contains(KnownUUIDs.SCHEMA_RESOURCE_UUID)) {
                rdCol.updateOne(
                        Filters.eq(FIELD_UUID, rdDoc.get(FIELD_UUID)),
                        Updates.set(
                                FIELD_SCHEMAS,
                                metadataSchemaUuids
                                        .stream()
                                        .filter(uuid -> !Objects.equals(uuid, KnownUUIDs.SCHEMA_RESOURCE_UUID))
                                        .toList()
                        )
                );
            }
        });
    }

    private boolean docWithUuidExists(MongoCollection<Document> collection, String uuid) {
        return collection.find(Filters.eq(FIELD_UUID, uuid)).first() != null;
    }

    @RollbackExecution
    public void rollback() {
        // updateInternalShapesType
        final MongoCollection<Document> shapeCol = database.getCollection(COL_SHAPES);
        final MongoCollection<Document> schemaCol = database.getCollection(COL_SCHEMAS);
        schemaCol.find(Filters.eq(FIELD_LATEST, true)).forEach(schemaDoc -> {
            final Document shapeDoc = new Document();
            shapeDoc.append(FIELD_UUID, schemaDoc.getString(FIELD_UUID));
            shapeDoc.append(FIELD_NAME, schemaDoc.getString(FIELD_NAME));
            shapeDoc.append(FIELD_DEFINITION, schemaDoc.getString(FIELD_DEFINITION));
            shapeDoc.append(FIELD_CLASSES, schemaDoc.get(FIELD_CLASSES));
            shapeDoc.append(FIELD_TYPE, schemaDoc.get(FIELD_TYPE).equals(TYPE_INTERNAL) ? TYPE_INTERNAL : TYPE_CUSTOM);
            shapeDoc.append(FIELD_PUBLISHED, schemaDoc.getBoolean(FIELD_PUBLISHED, false));
            shapeCol.insertOne(shapeDoc);
        });
        database.dropCollection(COL_SCHEMAS);
        database.dropCollection(COL_DRAFTS);
        // updateResourceDefinitionLinks
        final MongoCollection<Document> rdCol = database.getCollection(COL_RD);
        // Add Resource link (it is abstract)
        rdCol.find().forEach(rdDoc -> {
            final ArrayList<String> metadataSchemaUuids = (ArrayList<String>) rdDoc.get(FIELD_SCHEMAS);
            if (!metadataSchemaUuids.contains(KnownUUIDs.SCHEMA_RESOURCE_UUID)) {
                metadataSchemaUuids.add(KnownUUIDs.SCHEMA_RESOURCE_UUID);
                rdCol.updateOne(
                        Filters.eq(FIELD_UUID, rdDoc.get(FIELD_UUID)),
                        Updates.set(FIELD_SCHEMAS, metadataSchemaUuids)
                );
            }
        });
        // Rename metadata schema to shape
        rdCol.updateMany(
                Filters.exists(FIELD_SCHEMAS),
                Updates.rename(FIELD_SCHEMAS, FIELD_SHAPES)
        );
    }
}
