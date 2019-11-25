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
package nl.dtls.fairdatapoint.database.mongo.migration.development;

import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.database.mongo.migration.development.acl.AclMigration;
import nl.dtls.fairdatapoint.database.mongo.migration.development.membership.MembershipMigration;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.UserMigration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile(Profiles.NON_PRODUCTION)
public class MigrationRunner {

    @Autowired
    private UserMigration userMigration;

    @Autowired
    private MembershipMigration membershipMigration;

    @Autowired
    private AclMigration aclMigration;

    @PostConstruct
    public void run() {
        userMigration.runMigration();
        membershipMigration.runMigration();
        aclMigration.runMigration();
    }

}
