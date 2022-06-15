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
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.Document;
import org.springframework.context.annotation.Profile;

import static nl.dtls.fairdatapoint.util.ResourceReader.loadClassResource;

@ChangeLog(order = "0005")
@Profile(Profiles.PRODUCTION)
public class Migration_0005_UpdateShapeDefinition {

    @ChangeSet(order = "0005", id = "Migration_0005_UpdateShapeDefinition", author = "migrationBot")
    public void run(MongoDatabase db) throws Exception {
        addShapeDefinitions(db);
    }

    private void addShapeDefinitions(MongoDatabase db) throws Exception {
        MongoCollection<Document> shapeCol = db.getCollection("shape");

        shapeCol.deleteOne(new Document("uuid", KnownUUIDs.SCHEMA_RESOURCE_UUID));
        shapeCol.deleteOne(new Document("uuid", KnownUUIDs.SCHEMA_REPOSITORY_UUID));
        shapeCol.deleteOne(new Document("uuid", KnownUUIDs.SCHEMA_CATALOG_UUID));
        shapeCol.deleteOne(new Document("uuid", KnownUUIDs.SCHEMA_DATASET_UUID));
        shapeCol.deleteOne(new Document("uuid", KnownUUIDs.SCHEMA_DISTRIBUTION_UUID));

        shapeCol.insertOne(resourceDefinition());
        shapeCol.insertOne(repositoryDefinition());
        shapeCol.insertOne(catalogDefinition());
        shapeCol.insertOne(datasetDefinition());
        shapeCol.insertOne(distributionDefinition());
    }

    private Document resourceDefinition() throws Exception {
        String shaclDefinition = loadClassResource("0005_shape-resource.ttl", getClass());
        Document definition = new Document();
        definition.append("uuid", KnownUUIDs.SCHEMA_RESOURCE_UUID);
        definition.append("name", "Resource");
        definition.append("type", "INTERNAL");
        definition.append("definition", shaclDefinition);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }


    private Document repositoryDefinition() throws Exception {
        String shaclDefinition = loadClassResource("0005_shape-repository.ttl", getClass());
        Document definition = new Document();
        definition.append("uuid", KnownUUIDs.SCHEMA_REPOSITORY_UUID);
        definition.append("name", "Repository");
        definition.append("type", "INTERNAL");
        definition.append("definition", shaclDefinition);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

    private Document catalogDefinition() throws Exception {
        String shaclDefinition = loadClassResource("0005_shape-catalog.ttl", getClass());
        Document definition = new Document();
        definition.append("uuid", KnownUUIDs.SCHEMA_CATALOG_UUID);
        definition.append("name", "Catalog");
        definition.append("type", "INTERNAL");
        definition.append("definition", shaclDefinition);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

    private Document datasetDefinition() throws Exception {
        String shaclDefinition = loadClassResource("0005_shape-dataset.ttl", getClass());
        Document definition = new Document();
        definition.append("uuid", KnownUUIDs.SCHEMA_DATASET_UUID);
        definition.append("name", "Dataset");
        definition.append("type", "INTERNAL");
        definition.append("definition", shaclDefinition);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

    private Document distributionDefinition() throws Exception {
        String shaclDefinition = loadClassResource("0005_shape-distribution.ttl", getClass());
        Document definition = new Document();
        definition.append("uuid", KnownUUIDs.SCHEMA_DISTRIBUTION_UUID);
        definition.append("name", "Distribution");
        definition.append("type", "INTERNAL");
        definition.append("definition", shaclDefinition);
        definition.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return definition;
    }

}