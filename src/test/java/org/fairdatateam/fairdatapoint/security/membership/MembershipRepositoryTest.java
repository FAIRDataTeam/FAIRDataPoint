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
package org.fairdatateam.fairdatapoint.security.membership;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.MembershipMigration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// NOTE: `save` and `findByUuid` are two separate repository calls, each transactional on its own
// (there is no @Transactional on this class), so every reload below is a genuine round trip
// through the database rather than the first-level cache returning the same Java instance.
public class MembershipRepositoryTest extends BaseIntegrationTest {

    private static final int MASK_READ = 1;

    private static final int MASK_WRITE = 2;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipMigration membershipMigration;

    @AfterEach
    public void cleanup() {
        // put the fixture set back the way the other tests expect it
        membershipMigration.runMigration();
    }

    @Test
    @DisplayName("A membership round-trips with its permissions and its allowed entities, both in a stable order")
    public void savedMembershipRoundTripsWithBothCollections() {
        // GIVEN: a membership with two permissions and two allowed entities
        final Membership membership = newMembership();
        membership.addPermission(new MembershipPermission(MASK_WRITE, 'W'));
        membership.addPermission(new MembershipPermission(MASK_READ, 'R'));
        membershipRepository.save(membership);

        // WHEN: it is reloaded
        final Membership reloaded = membershipRepository.findByUuid(membership.getUuid().toString()).orElseThrow();

        // THEN: the allowed entities come back in natural (sorted string) order...
        assertThat(reloaded.getAllowedEntities(), contains(
                KnownUUIDs.RD_DATASET_UUID, KnownUUIDs.RD_CATALOG_UUID
        ));

        // THEN: ...and the permissions come back in ascending mask order, regardless of the
        // order they were added in
        assertThat(
                reloaded.getPermissions().stream().map(MembershipPermission::getCode).toList(),
                contains('R', 'W')
        );
        assertThat(reloaded.getCreatedAt(), is(notNullValue()));

        // THEN: every permission points back at the membership it belongs to
        for (final MembershipPermission permission : reloaded.getPermissions()) {
            assertThat(permission.getMembership().getUuid(), is(equalTo(membership.getUuid())));
            assertThat(permission.getCreatedAt(), is(notNullValue()));
        }
    }

    @Test
    @DisplayName("Removing a permission through the helper deletes its row")
    public void removedPermissionIsOrphaned() {
        // GIVEN: a saved membership with two permissions
        final Membership membership = newMembership();
        membership.addPermission(new MembershipPermission(MASK_READ, 'R'));
        membership.addPermission(new MembershipPermission(MASK_WRITE, 'W'));
        membershipRepository.save(membership);
        final Membership saved = membershipRepository.findByUuid(membership.getUuid()).orElseThrow();

        // WHEN: one permission is detached through the helper and the membership is saved again
        final MembershipPermission read = saved.getPermissions()
                .stream()
                .filter(permission -> permission.getMask() == MASK_READ)
                .findFirst()
                .orElseThrow();
        assertThat(read.getMembership(), is(notNullValue()));
        saved.removePermission(read);
        membershipRepository.save(saved);

        // THEN: the helper has cleared the back-reference on both sides
        assertThat(read.getMembership(), is(nullValue()));

        // THEN: only the other permission survives the round trip
        final Membership reloaded = membershipRepository.findByUuid(membership.getUuid()).orElseThrow();
        assertThat(reloaded.getPermissions().size(), is(equalTo(1)));
        assertThat(
                reloaded.getPermissions().stream().map(MembershipPermission::getCode).toList(),
                contains('W')
        );
    }

    @Test
    @DisplayName("A membership can be reinserted with the same identifier after a batch delete")
    public void reinsertedAfterBatchDeleteRoundTrips() {
        // GIVEN: a saved membership
        final Membership membership = newMembership();
        membership.addPermission(new MembershipPermission(MASK_READ, 'R'));
        membershipRepository.save(membership);
        final UUID membershipUuid = membership.getUuid();

        // WHEN: every membership is removed in one batch, mirroring what ResetService does before
        // restoring the factory defaults, and a fresh instance with the same identifier (but new
        // permissions, each with its own generated identifier) is saved in its place
        membershipRepository.deleteAllInBatch();
        final Membership reinserted = Membership.builder()
                .uuid(membershipUuid)
                .name("Round Trip")
                .allowedEntities(new LinkedHashSet<>(List.of(KnownUUIDs.RD_CATALOG_UUID)))
                .build();
        reinserted.addPermission(new MembershipPermission(MASK_READ, 'R'));
        reinserted.addPermission(new MembershipPermission(MASK_WRITE, 'W'));
        membershipRepository.save(reinserted);

        // THEN: the identifier is present again, with the new permission set
        final Membership reloaded = membershipRepository.findByUuid(membershipUuid).orElseThrow();
        assertThat(reloaded.getPermissions().size(), is(equalTo(2)));
    }

    private Membership newMembership() {
        return Membership.builder()
                .uuid(UUID.randomUUID())
                .name("Round Trip")
                .allowedEntities(new LinkedHashSet<>(List.of(
                        KnownUUIDs.RD_CATALOG_UUID, KnownUUIDs.RD_DATASET_UUID
                )))
                .build();
    }
}
