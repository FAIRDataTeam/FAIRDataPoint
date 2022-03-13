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
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.ArrayList;

@ChangeUnit(id="Migration_0012_MetadataSchemas", order = "0012", author = "migrationBot")
@Profile(Profiles.PRODUCTION)
public class Migration_0012_MetadataSchemas {

    private final MongoTemplate db;

    public Migration_0012_MetadataSchemas(MongoTemplate template) {
        this.db = template;
    }

    @Execution
    public void run() {
        updateInternalShapesType();
        // TODO: update Resource to be abstract and parent?
    }

    private void updateInternalShapesType() {
        MongoCollection<Document> shapeCol = db.getCollection("shape");
        MongoCollection<Document> schemaCol = db.createCollection("metadataSchema");
        db.createCollection("metadataSchemaDraft");
        SemVer version = new SemVer("1.0.0");
        Instant now = Instant.now();
        shapeCol.find().forEach(shapeDoc -> {
            Document schemaDoc = new Document();
            schemaDoc.append("uuid", shapeDoc.getString("uuid"));
            schemaDoc.append("versionString", version.toString());
            schemaDoc.append("version", version);
            schemaDoc.append("name", shapeDoc.getString("name"));
            schemaDoc.append("description", "");
            schemaDoc.append("definition", shapeDoc.getString("definition"));
            schemaDoc.append("targetClasses", shapeDoc.get("targetClasses"));
            schemaDoc.append("extendSchemas", new ArrayList<>());
            schemaDoc.append("type", shapeDoc.get("type"));
            schemaDoc.append("origin", null);
            schemaDoc.append("latest", true);
            schemaDoc.append("published", shapeDoc.getBoolean("published", false));
            schemaDoc.append("abstractSchema", false);
            schemaDoc.append("createdAt", now);
            schemaCol.insertOne(schemaDoc);
        });
        db.dropCollection("shape");
    }
    @RollbackExecution
    public void rollback() {

    }
/*
    @RollbackExecution
    public void rollback() {
        MongoCollection<Document> shapeCol = db.createCollection("shape");
        MongoCollection<Document> schemaCol = db.getCollection("metadataSchema");
        schemaCol.find(Filters.eq("latest", true)).forEach(schemaDoc -> {
            Document shapeDoc = new Document();
            shapeDoc.append("uuid", schemaDoc.getString("uuid"));
            shapeDoc.append("name", schemaDoc.getString("name"));
            shapeDoc.append("definition", schemaDoc.getString("definition"));
            shapeDoc.append("targetClasses", schemaDoc.get("targetClasses"));
            shapeDoc.append("type", schemaDoc.get("type"));
            shapeDoc.append("published", schemaDoc.getBoolean("published", false));
            shapeCol.insertOne(shapeDoc);
        });
        db.dropCollection("metadataSchema");
        db.dropCollection("metadataSchemaDraft");
    }
    */
}
