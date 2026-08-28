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
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.not;
import static com.mongodb.client.model.Updates.set;

/**
 * This migration updates existing <code>_class</code> and <code>ACL.className</code> fields to the new package names
 * that resulted from refactoring the project file structure in PR #941.
 * Note that <code>_class</code> fields are also updated automatically when
 * <a href="https://docs.spring.io/spring-data/mongodb/reference/mongodb/converters-type-mapping.html#mongo-template.type-mapping">
 * Spring Data MongoDB</a> saves or updates documents.
 */
@Slf4j
@ChangeUnit(
        id = "Migration_0021_UpdateMongoTypeMapperClassNames",
        order = "0021",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0021_UpdateMongoTypeMapperClassNames {

    private static final String ACL = "ACL";

    private static final String CLASS = "_class";

    private static final String METADATA = "org.fairdatateam.fairdatapoint.rdf.metadata.Metadata";

    private static final List<Change> CHANGES = List.of(
            // Note that the "ACL" and "rdfMigration" collections refer to external package names which were changed
            // in v1.17.3. However, existing `_class` values were never migrated, so we fix that here as well.
            new Change(ACL, CLASS, "org.fairdatateam.security.acls.domain.MongoAcl"),
            new Change("rdfMigration", CLASS, "org.fairdatateam.rdf.migration.entity.RdfMigration"),
            // The following changes are related to the current refactoring.
            new Change(ACL, "className", METADATA),
            new Change("apiKey", CLASS, "org.fairdatateam.fairdatapoint.security.apikey.ApiKey"),
            new Change("event", CLASS, "org.fairdatateam.fairdatapoint.index.event.Event"),
            new Change("indexEntry", CLASS, "org.fairdatateam.fairdatapoint.index.entry.IndexEntry"),
            new Change("indexSettings", CLASS, "org.fairdatateam.fairdatapoint.index.settings.IndexSettings"),
            new Change("membership", CLASS, "org.fairdatateam.fairdatapoint.security.membership.Membership"),
            new Change("metadata", CLASS, METADATA),
            new Change("metadataSchema", CLASS, "org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchema"),
            new Change("metadataSchemaDraft", CLASS, "org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchemaDraft"),
            new Change("resourceDefinition", CLASS, "org.fairdatateam.fairdatapoint.resource.ResourceDefinition"),
            new Change("searchSavedQuery", CLASS, "org.fairdatateam.fairdatapoint.search.SearchSavedQuery"),
            new Change("settings", CLASS, "org.fairdatateam.fairdatapoint.settings.Settings"),
            new Change("user", CLASS, "org.fairdatateam.fairdatapoint.user.User"),
            new Change("webhook", CLASS, "org.fairdatateam.fairdatapoint.index.webhook.Webhook")
    );

    private final MongoTemplate mongoTemplate;

    /**
     * Constructor (autowired)
     */
    public Migration_0021_UpdateMongoTypeMapperClassNames(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Applies all the changes
     */
    @Execution
    public void run() {
        CHANGES.forEach(this::applyChange);
    }

    /**
     * Applies a <code>Change</code> to all relevant documents in the mongodb database
     */
    @SneakyThrows
    private void applyChange(Change change) {
        // get the mongodb collection
        final MongoCollection<Document> collection = mongoTemplate.getCollection(change.collection());
        // apply the change to documents in the collection that do not have the new value yet
        final Bson filterNotNewValue = not(eq(change.field(), change.newValue()));
        collection.updateMany(filterNotNewValue, set(change.field(), change.newValue()));
    }

    /**
     * Cannot roll back because we cannot be sure whether the old package names were either org.fairdatateam or nl.dtls.
     * This is because migration immutability was not strictly maintained in the past.
     */
    @RollbackExecution
    public void rollback() {
        // no-op
    }

    /**
     * Defines a new value to be applied to the specified field, for all documents in the specified collection.
     */
    private record Change(String collection, String field, String newValue) {
    }

}
