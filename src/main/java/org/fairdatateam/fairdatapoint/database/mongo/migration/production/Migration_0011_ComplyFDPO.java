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

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import org.bson.Document;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.context.annotation.Profile;

import java.util.*;

import static nl.dtls.fairdatapoint.util.ResourceReader.loadClassResource;

@ChangeLog(order = "0011")
@Profile(Profiles.PRODUCTION)
public class Migration_0011_ComplyFDPO {

    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CLASS = "_class";
    private static final String FDP_NAME = "FAIR Data Point";

    @ChangeSet(order = "0011", id = "Migration_0011_ComplyFDPO", author = "migrationBot")
    public void run(MongoDatabase database, ResourceDefinitionCache resourceDefinitionCache,
                    ResourceDefinitionTargetClassesCache targetClassesCache) throws Exception {
        updateShapes(database);
        updateResourceDefinitions(database);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
    }

    private void updateShapes(MongoDatabase database) throws Exception {
        final MongoCollection<Document> shapeCol = database.getCollection("shape");

        // Delete Repository Shape
        shapeCol.deleteOne(new Document(FIELD_UUID, KnownUUIDs.SCHEMA_REPOSITORY_UUID));

        // Insert New Shapes
        shapeCol.insertOne(dataServiceShape());
        shapeCol.insertOne(metadataServiceShape());
        shapeCol.insertOne(fdpShape());
    }

    private Document fdpShape() throws Exception {
        return createShape("0011_shape-fdp.ttl", KnownUUIDs.SCHEMA_FDP_UUID,
                FDP_NAME, List.of(FDP.FAIRDATAPOINT.stringValue()));
    }

    private Document dataServiceShape() throws Exception {
        return createShape("0011_shape-data-service.ttl", KnownUUIDs.SCHEMA_DATASERVICE_UUID,
                "Data Service", List.of(DCAT.DATA_SERVICE.stringValue()));
    }

    private Document metadataServiceShape() throws Exception {
        return createShape("0011_shape-metadata-service.ttl", KnownUUIDs.SCHEMA_METADATASERVICE_UUID,
                "Metadata Service", List.of(FDP.METADATASERVICE.stringValue()));
    }

    private Document createShape(String filename, String uuid, String name, List<String> classes) throws Exception {
        final String shaclDefinition = loadClassResource(filename, getClass());
        final Document shape = new Document();
        shape.append(FIELD_UUID, uuid);
        shape.append(FIELD_NAME, name);
        shape.append("type", "INTERNAL");
        shape.append("definition", shaclDefinition);
        shape.append("targetClasses", classes);
        shape.append(FIELD_CLASS, "nl.dtls.fairdatapoint.entity.shape.Shape");
        return shape;
    }

    private void updateResourceDefinitions(MongoDatabase database) {
        final MongoCollection<Document> rdCol = database.getCollection("resourceDefinition");

        // Delete Repository Shape
        rdCol.deleteOne(new Document(FIELD_UUID, KnownUUIDs.RD_REPOSITORY_UUID));

        // Insert New Shapes
        rdCol.insertOne(fdpResourceDefinition());
    }

    private Document fdpResourceDefinition() {
        final Document definition = new Document();
        definition.append(FIELD_UUID, KnownUUIDs.RD_FDP_UUID);
        definition.append(FIELD_NAME, FDP_NAME);
        definition.append("urlPrefix", "");
        definition.append("shapeUuids", List.of(
                KnownUUIDs.SCHEMA_RESOURCE_UUID,
                KnownUUIDs.SCHEMA_DATASERVICE_UUID,
                KnownUUIDs.SCHEMA_METADATASERVICE_UUID,
                KnownUUIDs.SCHEMA_FDP_UUID
        ));

        // Child
        final Document child = new Document();
        child.append("resourceDefinitionUuid", KnownUUIDs.RD_CATALOG_UUID);
        child.append("relationUri", FDP.METADATACATALOG.stringValue());
        final Document listView = new Document();
        listView.append("title", "Catalogs");
        listView.append("tagsUri", DCAT.THEME_TAXONOMY.stringValue());
        listView.append("metadata", List.of());
        child.append("listView", listView);
        definition.append("children", List.of(child));

        // External Links
        definition.append("externalLinks", List.of());

        definition.append(FIELD_CLASS, "nl.dtls.fairdatapoint.entity.resource.ResourceDefinition");
        return definition;
    }
}
