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
package nl.dtls.fairdatapoint.database.rdf.migration.development.metadata;

import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.security.AuthenticationService;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class AclMigration {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AclCache aclCache;

    public void runMigration() {
        aclCache.clearCache();

        final Authentication auth = authenticationService.getAuthentication(KnownUUIDs.USER_ALBERT_UUID.toString());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // -- Catalog
        final String catalog1Id = format("%s/catalog/catalog-1", persistentUrl);
        memberService.createOwner(catalog1Id, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);
        memberService.createOrUpdateMember(
                catalog1Id, Metadata.class, KnownUUIDs.USER_NIKOLA_UUID, KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID
        );

        final String catalog2Id = format("%s/catalog/catalog-2", persistentUrl);
        memberService.createOwner(catalog2Id, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);

        // -- Dataset
        final String dataset1Id = format("%s/dataset/dataset-1", persistentUrl);
        memberService.createOwner(dataset1Id, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);
        memberService.createOrUpdateMember(dataset1Id, Metadata.class,
                KnownUUIDs.USER_NIKOLA_UUID, KnownUUIDs.MEMBERSHIP_OWNER_UUID);

        final String dataset2Id = format("%s/dataset/dataset-2", persistentUrl);
        memberService.createOwner(dataset2Id, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);

        // -- Distribution
        final String distribution1Id = format("%s/distribution/distribution-1", persistentUrl);
        memberService.createOwner(distribution1Id, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);
        memberService.createOrUpdateMember(distribution1Id, Metadata.class,
                KnownUUIDs.USER_NIKOLA_UUID, KnownUUIDs.MEMBERSHIP_OWNER_UUID);

        final String distribution2Id = format("%s/distribution/distribution-2", persistentUrl);
        memberService.createOwner(distribution2Id, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);
    }
}
