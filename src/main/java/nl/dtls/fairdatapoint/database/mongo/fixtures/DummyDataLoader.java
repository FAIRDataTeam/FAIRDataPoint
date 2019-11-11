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
package nl.dtls.fairdatapoint.database.mongo.fixtures;

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtls.fairdatapoint.Profiles;
import nl.dtls.fairdatapoint.database.mongo.repository.MembershipRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.UserRepository;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.acls.dao.AclRepository;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile(Profiles.NON_PRODUCTION)
public class DummyDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFixtures userFixtures;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private AclRepository aclRepository;

    @Autowired
    private MembershipFixtures membershipFixtures;

    @Autowired
    protected MemberService memberService;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Autowired
    private AclCache aclCache;

    @PostConstruct
    public void init() {
        // User
        userRepository.deleteAll();
        userRepository.save(userFixtures.albert());
        userRepository.save(userFixtures.nikola());

        // Membership
        membershipRepository.deleteAll();
        membershipRepository.save(membershipFixtures.owner());
        membershipRepository.save(membershipFixtures.dataProvider());

        // ACL
        aclRepository.deleteAll();
        aclCache.clearCache();

        String albertUuid = userFixtures.albert().getUuid();
        String nicolaUuid = userFixtures.nikola().getUuid();
        String dataProviderUuid = membershipFixtures.dataProvider().getUuid();
        Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // -- Catalog
        String catalogId = "catalog-1";
        memberService.createOwner(catalogId, CatalogMetadata.class, albertUuid);
        memberService.createOrUpdateMember(catalogId, CatalogMetadata.class, nicolaUuid, dataProviderUuid);

        // -- Dataset
        String datasetId = "dataset-1";
        memberService.createOwner(datasetId, DatasetMetadata.class, albertUuid);
    }

}
