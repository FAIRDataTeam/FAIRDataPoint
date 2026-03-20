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
import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.service.settings.SettingsCache;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

@ChangeUnit(
        id = "Migration_0013_ComplexSearch",
        order = "0013",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0013_ComplexSearch {

    private static final String COL_SETTINGS = "settings";
    private static final String FIELD_SEARCH_FILTERS = "searchFilters";

    private final MongoTemplate database;

    public Migration_0013_ComplexSearch(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run(SettingsCache settingsCache) {
        addSearchRelatedSettings();
        settingsCache.updateCachedSettings();
    }

    private void addSearchRelatedSettings() {
        final MongoCollection<Document> settingsCol = database.getCollection(COL_SETTINGS);
        settingsCol.updateMany(
                Filters.not(Filters.exists(FIELD_SEARCH_FILTERS)),
                Updates.set(FIELD_SEARCH_FILTERS, Collections.emptyList())
        );
    }

    @RollbackExecution
    public void rollback() {
        final MongoCollection<Document> settingsCol = database.getCollection(COL_SETTINGS);
        settingsCol.updateMany(
                Filters.exists(FIELD_SEARCH_FILTERS),
                Updates.unset(FIELD_SEARCH_FILTERS)
        );
    }
}
