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

import java.util.List;
import java.util.stream.Collectors;

@ChangeLog(order = "0010")
@Profile(Profiles.PRODUCTION)
public class Migration_0010_FixShapeXsdPrefix {

    private static final String XSD_PREFIX = "@prefix xsd:      <http://www.w3.org/2001/XMLSchema#> .";
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_DEFINITION = "definition";

    @ChangeSet(order = "0010", id = "Migration_0010_FixShapeXsdPrefix", author = "migrationBot")
    public void run(MongoDatabase database) {
        fixShapePrefixes(database);
    }

    private void fixShapePrefixes(MongoDatabase database) {
        // catalog
        fixShape("2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660", database);
        // dataset
        fixShape("866d7fb8-5982-4215-9c7c-18d0ed1bd5f3", database);
        // distribution
        fixShape("ebacbf83-cd4f-4113-8738-d73c0735b0ab", database);
    }

    private void fixShape(String shapeUuid, MongoDatabase database) {
        final MongoCollection<Document> shapeCol = database.getCollection("shape");
        final Document shape = shapeCol.find(Filters.eq(FIELD_UUID, shapeUuid)).first();
        if (shape != null) {
            final String definition = shape.getString(FIELD_DEFINITION);
            final boolean isFaulty = definition.contains("xsd:") && !definition.contains("@prefix xsd:");
            if (isFaulty) {
                final List<String> lines = definition.lines().collect(Collectors.toList());
                int line = 0;
                while (lines.get(line).startsWith("@prefix")) {
                    line++;
                }
                lines.add(line, XSD_PREFIX);

                shapeCol.updateOne(
                        Filters.eq(FIELD_UUID, shapeUuid),
                        Updates.set(FIELD_DEFINITION, String.join("\n", lines))
                );
            }
        }
    }
}
