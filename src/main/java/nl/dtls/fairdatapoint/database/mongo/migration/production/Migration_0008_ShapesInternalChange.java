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
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import nl.dtls.fairdatapoint.Profiles;
import org.bson.Document;
import org.springframework.context.annotation.Profile;

@ChangeLog(order = "0008")
@Profile(Profiles.PRODUCTION)
public class Migration_0008_ShapesInternalChange {

    @ChangeSet(order = "0008", id = "Migration_0008_ShapesInternalChange", author = "migrationBot")
    public void run(MongoDatabase db) {
        updateInternalShapesType(db);
    }

    private void updateInternalShapesType(MongoDatabase db) {
        MongoCollection<Document> shapeCol = db.getCollection("shape");
        // DATASET
        shapeCol.updateOne(
                Filters.eq("uuid", "866d7fb8-5982-4215-9c7c-18d0ed1bd5f3"),
                Updates.set("type", "CUSTOM")
        );
        // DISTRIBUTION
        shapeCol.updateOne(
                Filters.eq("uuid", "ebacbf83-cd4f-4113-8738-d73c0735b0ab"),
                Updates.set("type", "CUSTOM")
        );
    }
}
