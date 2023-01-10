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
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaShaclUtils;
import org.bson.Document;
import org.springframework.context.annotation.Profile;

import java.util.*;

@ChangeLog(order = "0009")
@Profile(Profiles.PRODUCTION)
public class Migration_0009_ShapeTargetClasses {

    private static final String FIELD_UUID = "uuid";
    private static final String FILED_CLASSES = "targetClassUris";

    @ChangeSet(order = "0009", id = "Migration_0009_ShapeTargetClasses", author = "migrationBot")
    public void run(MongoDatabase database, ResourceDefinitionCache resourceDefinitionCache,
                    ResourceDefinitionTargetClassesCache targetClassesCache) {
        updateShapesAndResources(database);
        resourceDefinitionCache.computeCache();
        targetClassesCache.computeCache();
    }

    private void updateShapesAndResources(MongoDatabase database) {
        final Map<String, Set<String>> targetClassesMap = new HashMap<>();
        // Update shapes
        final MongoCollection<Document> shapeCol = database.getCollection("shape");
        for (Document document : shapeCol.find()) {
            final String definition = (String) document.get("definition");
            final String uuid = (String) document.get(FIELD_UUID);
            final Set<String> targetClasses = MetadataSchemaShaclUtils.extractTargetClasses(definition);
            targetClassesMap.put(uuid, targetClasses);
            shapeCol.updateOne(
                    Filters.eq(FIELD_UUID, uuid),
                    Updates.set("targetClasses", targetClasses)
            );
        }
        // Update resource definitions
        final MongoCollection<Document> rdCol = database.getCollection("resourceDefinition");
        for (Document document : rdCol.find()) {
            final List<String> targetClassUris = (List<String>) document.get(FILED_CLASSES);
            final Set<String> shapeUuids = new HashSet<>();
            targetClassUris.forEach(uri -> {
                targetClassesMap.forEach((shapeUuid, targetClasses) -> {
                    if (targetClasses.contains(uri)) {
                        shapeUuids.add(shapeUuid);
                    }
                });
            });
            rdCol.updateOne(
                    Filters.eq(FIELD_UUID, document.get(FIELD_UUID)),
                    Updates.unset(FILED_CLASSES)
            );
            rdCol.updateOne(
                    Filters.eq(FIELD_UUID, document.get(FIELD_UUID)),
                    Updates.set("shapeUuids", shapeUuids)
            );
        }
    }
}
