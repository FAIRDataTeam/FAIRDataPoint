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
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(
        id = "Migration_0016_IndexSettingsAutoPermit",
        order = "0016",
        author = "migrationBot"
)
@Profile(Profiles.PRODUCTION)
public class Migration_0016_IndexSettingsAutoPermit {

    private static final String FIELD_AUTO_PERMIT = "autoPermit";
    private static final String COL_INDEX_SETTINGS = "indexSettings";

    private final MongoTemplate database;

    public Migration_0016_IndexSettingsAutoPermit(MongoTemplate template) {
        this.database = template;
    }

    @Execution
    public void run() {
        final MongoCollection<Document> settingsCol = database.getCollection(COL_INDEX_SETTINGS);
        settingsCol.updateMany(
                Filters.not(Filters.exists(FIELD_AUTO_PERMIT)),
                Updates.set(FIELD_AUTO_PERMIT, true)
        );
    }

    @RollbackExecution
    public void rollback() {
        final MongoCollection<Document> settingsCol = database.getCollection(COL_INDEX_SETTINGS);
        settingsCol.updateMany(
                Filters.exists(FIELD_AUTO_PERMIT),
                Updates.unset(FIELD_AUTO_PERMIT)
        );
    }
}
