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
import nl.dtls.fairdatapoint.service.shape.ShapeShaclUtils;
import org.bson.Document;
import org.springframework.context.annotation.Profile;

import java.util.*;

@ChangeLog(order = "0009")
@Profile(Profiles.PRODUCTION)
public class Migration_0009_ShapeTargetClasses {

    @ChangeSet(order = "0009", id = "Migration_0009_ShapeTargetClasses", author = "migrationBot")
    public void run(MongoDatabase db) {
        updateShapesAndResources(db);
    }

    private void updateShapesAndResources(MongoDatabase db) {
        Map<String, Set<String>> targetClassesMap = new HashMap<>();
        // Update shapes
        MongoCollection<Document> shapeCol = db.getCollection("shape");
        for (Document document : shapeCol.find()) {
            String definition = (String) document.get("definition");
            String uuid = (String) document.get("uuid");
            Set<String> targetClasses = ShapeShaclUtils.extractTargetClasses(definition);
            targetClassesMap.put(uuid, targetClasses);
            shapeCol.updateOne(
                    Filters.eq("uuid", uuid),
                    Updates.set("targetClasses", targetClasses)
            );
        }
        // Update resource definitions
        MongoCollection<Document> rdCol = db.getCollection("resourceDefinition");
        for (Document document : rdCol.find()) {
            List<String> targetClassUris = (List<String>) document.get("targetClassUris");
            Set<String> shapeUuids = new HashSet<>();
            targetClassUris.forEach(uri -> {
                targetClassesMap.forEach((shapeUuid, targetClasses) -> {
                    if (targetClasses.contains(uri)) {
                        shapeUuids.add(shapeUuid);
                    }
                });
            });
            rdCol.updateOne(
                    Filters.eq("uuid", document.get("uuid")),
                    Updates.unset("targetClassUris")
            );
            rdCol.updateOne(
                    Filters.eq("uuid", document.get("uuid")),
                    Updates.set("shapeUuids", shapeUuids)
            );
        }
    }
}
