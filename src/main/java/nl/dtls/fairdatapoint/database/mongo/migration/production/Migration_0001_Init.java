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
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.springframework.context.annotation.Profile;

@ChangeLog(order = "0001")
@Profile(Profiles.PRODUCTION)
public class Migration_0001_Init {

    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_PERMISSIONS = "permissions";
    private static final String FIELD_MASK = "mask";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_CLASS = "_class";
    private static final int MASK_W = 2;
    private static final int MASK_C = 4;
    private static final int MASK_D = 8;
    private static final int MASK_A = 16;

    @ChangeSet(order = "0001", id = "0001_init", author = "migrationBot")
    public void run(MongoDatabase database) {
        final MongoCollection<Document> userCol = database.getCollection("user");
        userCol.insertOne(userAlbert());
        userCol.insertOne(userNikola());

        final MongoCollection<Document> membershipCol = database.getCollection("membership");
        membershipCol.insertOne(membershipOwner());
        membershipCol.insertOne(membershipDataProvider());
    }

    private Document userAlbert() {
        return createUser(KnownUUIDs.USER_ALBERT_UUID, "Albert", "Einstein", "albert.einstein@example.com", "ADMIN");
    }

    private Document userNikola() {
        return createUser(KnownUUIDs.USER_NIKOLA_UUID, "Nikola", "Tesla", "nikola.tesla@example.com", "USER");
    }

    private Document createUser(String uuid, String firstName, String lastName, String email, String role) {
        final Document user = new Document();
        user.append(FIELD_UUID, uuid);
        user.append("firstName", firstName);
        user.append("lastName", lastName);
        user.append("email", email);
        user.append("passwordHash", "$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW");
        user.append("role", role);
        user.append(FIELD_CLASS, "nl.dtls.fairdatapoint.entity.user.User");
        return user;
    }

    private Document membershipOwner() {
        return createMembership(KnownUUIDs.MEMBERSHIP_OWNER_UUID, "Owner", true);
    }

    private Document membershipDataProvider() {
        return createMembership(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID, "Data Provider", false);
    }

    private Document createMembership(String uuid, String name, boolean owner) {
        final Document membership = new Document();
        membership.append(FIELD_UUID, uuid);
        membership.append("name", name);
        final BasicBSONList permissions = new BasicBSONList();
        permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_C).append(FIELD_CODE, "C"));
        final BasicBSONList allowedEntities = new BasicBSONList();
        allowedEntities.add("CATALOG");
        if (owner) {
            permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_W).append(FIELD_CODE, "W"));
            permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_D).append(FIELD_CODE, "D"));
            permissions.add(new BasicBSONObject().append(FIELD_MASK, MASK_A).append(FIELD_CODE, "A"));
            allowedEntities.add("DATASET");
            allowedEntities.add("DISTRIBUTION");
        }
        membership.append(FIELD_PERMISSIONS, permissions);
        membership.append("allowedEntities", allowedEntities);
        membership.append(FIELD_CLASS, "nl.dtls.fairdatapoint.entity.membership.Membership");
        return membership;
    }

}
