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

@ChangeLog(order = "0003")
@Profile(Profiles.PRODUCTION)
public class Migration_0003_ShapeDefinition {

    @ChangeSet(order = "0003", id = "Migration_0003_ShapeDefinition", author = "migrationBot")
    public void run(MongoDatabase database) throws Exception {
        addShapeDefinitions(database);
    }

    private void addShapeDefinitions(MongoDatabase database) throws Exception {
        final MongoCollection<Document> shapeCol = database.getCollection("shape");
        shapeCol.insertOne(resourceDefinition());
        shapeCol.insertOne(repositoryDefinition());
        shapeCol.insertOne(catalogDefinition());
        shapeCol.insertOne(datasetDefinition());
        shapeCol.insertOne(distributionDefinition());
    }

    private Document resourceDefinition() throws Exception {
        return createShape("0003_shape-resource.ttl", KnownUUIDs.SCHEMA_RESOURCE_UUID, "Resource");
    }

    private Document repositoryDefinition() throws Exception {
        return createShape("0003_shape-repository.ttl", KnownUUIDs.SCHEMA_REPOSITORY_UUID, "Repository");
    }

    private Document catalogDefinition() throws Exception {
        return createShape("0003_shape-catalog.ttl", KnownUUIDs.SCHEMA_CATALOG_UUID, "Catalog");
    }

    private Document datasetDefinition() throws Exception {
        return createShape("0003_shape-dataset.ttl", KnownUUIDs.SCHEMA_DATASET_UUID, "Dataset");
    }

    private Document distributionDefinition() throws Exception {
        return createShape("0003_shape-distribution.ttl", KnownUUIDs.SCHEMA_DISTRIBUTION_UUID, "Distribution");
    }

    private Document createShape(String filename, String uuid, String name) throws Exception {
        final String shaclDefinition = loadClassResource(filename, getClass());
        final Document shape = new Document();
        shape.append("uuid", uuid);
        shape.append("name", name);
        shape.append("type", "INTERNAL");
        shape.append("definition", shaclDefinition);
        shape.append("_class", "nl.dtls.fairdatapoint.entity.shape.Shape");
        return shape;
    }
}
