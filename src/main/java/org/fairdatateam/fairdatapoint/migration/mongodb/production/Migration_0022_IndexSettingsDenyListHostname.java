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
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.fairdatateam.fairdatapoint.Profiles;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;
import java.util.List;

@ChangeUnit(
        id = "Migration_0022_IndexSettingsDenyListHostname",
        order = "0022",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0022_IndexSettingsDenyListHostname {

    private static final String OLD_DENY_LIST_ITEM = "^(http|https)://localhost(:[0-9]+){0,1}.*$";
    private static final String NEW_DENY_LIST_ITEM = "localhost";
    private static final String FIELD_PING = "ping";
    private static final String FIELD_DENY_LIST = "denyList";
    private static final String COLLECTION_INDEX_SETTINGS = "indexSettings";

    private final MongoTemplate database;

    public Migration_0022_IndexSettingsDenyListHostname(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        // https://www.mongodb.com/docs/drivers/java/sync/current/crud/update-documents/
        final MongoCollection<Document> settingsCollection = database.getCollection(COLLECTION_INDEX_SETTINGS);
        // there should be only one indexSettings document
        final Document indexSettings = settingsCollection.find(Filters.exists(FIELD_PING)).first();
        // the indexSettings collection is created lazily, so it only exist if an admin has ever actively updated the
        // index settings
        if (indexSettings != null) {
            final Document pingSettings = indexSettings.get(FIELD_PING, Document.class);
            if (pingSettings != null) {
                final List<String> denyList = pingSettings.getList(FIELD_DENY_LIST, String.class);
                if (denyList != null) {
                    final boolean replaced = Collections.replaceAll(denyList, OLD_DENY_LIST_ITEM, NEW_DENY_LIST_ITEM);
                    if (replaced) {
                        System.out.printf("replaced old regex pattern by hostname: %s%n", denyList);
                        pingSettings.put(FIELD_DENY_LIST, denyList);
                        // ...?
                    }
                }
            }
        }
    }

    @RollbackExecution
    public void rollback() {
        final MongoCollection<Document> settingsCollection = database.getCollection(COLLECTION_INDEX_SETTINGS);
        // todo: implement
    }
}
