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
package org.fairdatateam.fairdatapoint.migration.mongodb.development;

import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.reset.FactoryDefaults;
import org.fairdatateam.fairdatapoint.security.membership.Membership;
import org.fairdatateam.fairdatapoint.security.membership.MembershipPermission;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MembershipFixtures {

    // Every call builds fresh permissions: each one carries its own identifier and a
    // back-reference to the membership it belongs to, so instances cannot be shared.
    public Membership owner() {
        final Membership membership = Membership.builder()
                .uuid(UUID.fromString(KnownUUIDs.MEMBERSHIP_OWNER_UUID))
                .name("Owner")
                .allowedEntities(allowedEntities(
                        KnownUUIDs.RD_CATALOG_UUID,
                        KnownUUIDs.RD_DATASET_UUID,
                        KnownUUIDs.RD_DISTRIBUTION_UUID
                ))
                .build();
        membership.addPermission(new MembershipPermission(FactoryDefaults.MASK_W, 'W'));
        membership.addPermission(new MembershipPermission(FactoryDefaults.MASK_C, 'C'));
        membership.addPermission(new MembershipPermission(FactoryDefaults.MASK_D, 'D'));
        membership.addPermission(new MembershipPermission(FactoryDefaults.MASK_A, 'A'));
        return membership;
    }

    public Membership dataProvider() {
        final Membership membership = Membership.builder()
                .uuid(UUID.fromString(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID))
                .name("Data Provider")
                .allowedEntities(allowedEntities(KnownUUIDs.RD_CATALOG_UUID))
                .build();
        membership.addPermission(new MembershipPermission(FactoryDefaults.MASK_C, 'C'));
        return membership;
    }

    private Set<String> allowedEntities(final String... resourceDefinitionUuids) {
        return new LinkedHashSet<>(List.of(resourceDefinitionUuids));
    }

}
