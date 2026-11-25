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
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fairdatateam.fairdatapoint.Profiles;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Slf4j
@ChangeUnit(
        id = "Migration_0022_IndexSettingsDenyListHostname",
        order = "0022",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0022_IndexSettingsDenyListHostname {

    // we want to replace all occurrences of the old default regex pattern by the new default hostname
    private static final String DEFAULT_REGEX_PATTERN = "^(http|https)://localhost(:[0-9]+){0,1}.*$";
    private static final String DEFAULT_HOST_NAME = "localhost";
    private static final String COLLECTION_INDEX_SETTINGS = "indexSettings";

    private final MongoTemplate database;

    public Migration_0022_IndexSettingsDenyListHostname(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        replacePingDenyListItem(DEFAULT_REGEX_PATTERN, DEFAULT_HOST_NAME);
    }

    @RollbackExecution
    public void rollback() {
        replacePingDenyListItem(DEFAULT_HOST_NAME, DEFAULT_REGEX_PATTERN);
    }

    /**
     * Replaces all items matching oldValue in the indexSettings.ping.denyList array by newValue.
     */
    private void replacePingDenyListItem(String oldValue, String newValue) {
        // https://www.mongodb.com/docs/drivers/java/sync/current/crud/update-documents/embedded-arrays/
        // https://www.mongodb.com/docs/manual/core/document/#std-label-document-dot-notation
        // https://www.mongodb.com/docs/manual/reference/bson-types/

        // The indexSettings collection is created lazily, so it only exists if an admin has ever actively updated the
        // index settings. Also note that there should only ever be at most *one* indexSettings document.
        final MongoCollection<Document> settingsCollection = database.getCollection(COLLECTION_INDEX_SETTINGS);

        // prepare to select documents that have a ping denyList that is an array
        final Bson documentFilter = Filters.type("ping.denyList", "array");

        // prepare to replace all denyList items matching oldValue by newValue
        final Bson arrayFilter = Filters.eq("item", oldValue);
        final Bson update = Updates.set("ping.denyList.$[item]", newValue);

        // prepare to apply the filter to the denyList array and return the resulting document
        final FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
                .arrayFilters(List.of(arrayFilter))
                .returnDocument(ReturnDocument.AFTER);

        // apply the update to the selected items
        final Document result = settingsCollection.findOneAndUpdate(documentFilter, update, options);
        if (result != null) log.info("denyList updated: {}", result.toJson());
    }
}
