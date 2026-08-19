/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.migration.mongodb.production;

import com.mongodb.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fairdatateam.fairdatapoint.Profiles;
import org.fairdatateam.fairdatapoint.util.KnownUUIDs;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

/**
 * This migration updates existing <code>_class</code> and <code>ACL.className</code> fields to the new package names.
 * Note that <code>_class</code> fields are automatically added by
 * <a href="https://docs.spring.io/spring-data/mongodb/reference/mongodb/converters-type-mapping.html#mongo-template.type-mapping">Spring Data MongoDB</a>.
 */
@Slf4j
@ChangeUnit(
        id = "Migration_0021_UpdateClassNames",
        order = "0021",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0021_UpdateClassNames {

    private static final Map<String, List<String>> COLLECTIONS = Map.of(
            "ACL", List.of(
                    // this case applies to the className field instead of the _class field
                    "org.fairdatateam.fairdatapoint.entity.metadata.Metadata",
                    "org.fairdatateam.fairdatapoint.rdf.metadata.Metadata"
            ),
            "apiKey", List.of(
                    "org.fairdatateam.fairdatapoint.entity.apikey.ApiKey",
                    "org.fairdatateam.fairdatapoint.security.apikey.ApiKey"
            ),
            "indexEntry", List.of(
                    "org.fairdatateam.fairdatapoint.entity.index.entry.IndexEntry",
                    "org.fairdatateam.fairdatapoint.index.entry.IndexEntry"
            ),
            "membership", List.of(
                    "org.fairdatateam.fairdatapoint.entity.membership.Membership",
                    "org.fairdatateam.fairdatapoint.security.membership.Membership"
            ),
            "metadata", List.of(
                    "org.fairdatateam.fairdatapoint.entity.metadata.Metadata",
                    "org.fairdatateam.fairdatapoint.rdf.metadata.Metadata"
            ),
            "metadataSchema", List.of(
                    "org.fairdatateam.fairdatapoint.entity.schema.MetadataSchema",
                    "org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchema"
            ),
            "resourceDefinition", List.of(
                    "org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinition",
                    "org.fairdatateam.fairdatapoint.resource.ResourceDefinition"
            ),
            "settings", List.of(
                    "org.fairdatateam.fairdatapoint.entity.settings.Settings",
                    "org.fairdatateam.fairdatapoint.settings.Settings"
            ),
            "user", List.of(
                    "org.fairdatateam.fairdatapoint.entity.user.User",
                    "org.fairdatateam.fairdatapoint.user.User"
            )
    );

    private final MongoTemplate database;

    /**
     * Constructor (autowired)
     */
    public Migration_0021_UpdateClassNames(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        COLLECTIONS.forEach(this::updateClassFields);
    }

    /**
     * Replaces the old class name by the new one in relevant documents in the specified collection.
     */
    @SneakyThrows
    private void updateClassFields(String collectionName, List<String> classNames) {
        // get the mongodb collection
        final MongoCollection<Document> collection = database.getCollection(collectionName);
        // the ACL class comes from an external package, so _class does not change, but className does
        final String fieldName = (collectionName.matches("ACL")) ? "className" : "_class";
        // select documents where the field value matches the old class name
        Bson filterByOldClassName = eq(fieldName, classNames.getFirst());
        // update field value to new class name
        collection.updateMany(filterByOldClassName, set(fieldName, classNames.getLast()));
    }

    @RollbackExecution
    public void rollback() {
        // todo
    }
}
