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
package nl.dtls.fairdatapoint.database.mongo.migration.development.acl;

import nl.dtls.fairdatapoint.database.common.migration.Migration;
import nl.dtls.fairdatapoint.database.mongo.migration.development.membership.data.MembershipFixtures;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.acls.dao.AclRepository;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AclMigration implements Migration {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private UserFixtures userFixtures;

    @Autowired
    private MembershipFixtures membershipFixtures;

    @Autowired
    private AclRepository aclRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Autowired
    private AclCache aclCache;

    public void runMigration() {
        aclRepository.deleteAll();
        aclCache.clearCache();

        String albertUuid = userFixtures.albert().getUuid();
        String nicolaUuid = userFixtures.nikola().getUuid();
        String ownerUuid = membershipFixtures.owner().getUuid();
        String dataProviderUuid = membershipFixtures.dataProvider().getUuid();
        Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // -- Catalog
        String catalog1Id = format("%s/catalog/catalog-1", persistentUrl);
        memberService.createOwner(catalog1Id, Metadata.class, albertUuid);
        memberService.createOrUpdateMember(catalog1Id, Metadata.class, nicolaUuid, dataProviderUuid);

        String catalog2Id = format("%s/catalog/catalog-2", persistentUrl);
        memberService.createOwner(catalog2Id, Metadata.class, albertUuid);

        // -- Dataset
        String dataset1Id = format("%s/dataset/dataset-1", persistentUrl);
        memberService.createOwner(dataset1Id, Metadata.class, albertUuid);
        memberService.createOrUpdateMember(dataset1Id, Metadata.class, nicolaUuid, ownerUuid);

        String dataset2Id = format("%s/dataset/dataset-2", persistentUrl);
        memberService.createOwner(dataset2Id, Metadata.class, albertUuid);

        // -- Distribution
        String distribution1Id = format("%s/distribution/distribution-1", persistentUrl);
        memberService.createOwner(distribution1Id, Metadata.class, albertUuid);
        memberService.createOrUpdateMember(distribution1Id, Metadata.class, nicolaUuid, ownerUuid);

        String distribution2Id = format("%s/distribution/distribution-2", persistentUrl);
        memberService.createOwner(distribution2Id, Metadata.class, albertUuid);
    }

}
