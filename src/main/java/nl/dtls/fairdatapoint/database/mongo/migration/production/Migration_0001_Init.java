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
import nl.dtls.fairdatapoint.Profiles;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.springframework.context.annotation.Profile;

@ChangeLog(order = "0001")
@Profile(Profiles.PRODUCTION)
public class Migration_0001_Init {

    @ChangeSet(order = "0001", id = "0001_init", author = "migrationBot")
    public void run(MongoDatabase db) {
        MongoCollection<Document> userCol = db.getCollection("user");
        userCol.insertOne(userAlbert());
        userCol.insertOne(userNikola());

        MongoCollection<Document> membershipCol = db.getCollection("membership");
        membershipCol.insertOne(membershipOwner());
        membershipCol.insertOne(membershipDataProvider());
    }

    private Document userAlbert() {
        Document user = new Document();
        user.append("uuid", "7e64818d-6276-46fb-8bb1-732e6e09f7e9");
        user.append("firstName", "Albert");
        user.append("lastName", "Einstein");
        user.append("email", "albert.einstein@example.com");
        user.append("passwordHash", "$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW"); // password
        user.append("role", "ADMIN");
        user.append("_class", "nl.dtls.fairdatapoint.entity.user.User");
        return user;
    }

    private Document userNikola() {
        Document user = new Document();
        user.append("uuid", "b5b92c69-5ed9-4054-954d-0121c29b6800");
        user.append("firstName", "Nikola");
        user.append("lastName", "Tesla");
        user.append("email", "nikola.tesla@example.com");
        user.append("passwordHash", "$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW"); // password
        user.append("role", "USER");
        user.append("_class", "nl.dtls.fairdatapoint.entity.user.User");
        return user;
    }

    private Document membershipOwner() {
        Document user = new Document();
        user.append("uuid", "49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8");
        user.append("name", "Owner");
        BasicBSONList permissions = new BasicBSONList();
        permissions.add(new BasicBSONObject().append("mask", 2).append("code", "W"));
        permissions.add(new BasicBSONObject().append("mask", 4).append("code", "C"));
        permissions.add(new BasicBSONObject().append("mask", 8).append("code", "D"));
        permissions.add(new BasicBSONObject().append("mask", 16).append("code", "A"));
        user.append("permissions", permissions);
        BasicBSONList allowedEntities = new BasicBSONList();
        allowedEntities.add("CATALOG");
        allowedEntities.add("DATASET");
        allowedEntities.add("DISTRIBUTION");
        user.append("allowedEntities", allowedEntities);
        user.append("_class", "nl.dtls.fairdatapoint.entity.membership.Membership");
        return user;
    }

    private Document membershipDataProvider() {
        Document user = new Document();
        user.append("uuid", "87a2d984-7db2-43f6-805c-6b0040afead5");
        user.append("name", "Data Provider");
        BasicBSONList permissions = new BasicBSONList();
        permissions.add(new BasicBSONObject().append("mask", 4).append("code", "C"));
        user.append("permissions", permissions);
        BasicBSONList allowedEntities = new BasicBSONList();
        allowedEntities.add("CATALOG");
        user.append("allowedEntities", allowedEntities);
        user.append("_class", "nl.dtls.fairdatapoint.entity.membership.Membership");
        return user;
    }

}
