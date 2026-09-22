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
import org.fairdatateam.fairdatapoint.rdf.metadata.Metadata;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// Deleting a user must remove that user's access control entries everywhere and nothing else; the
// SID row itself may only disappear when it is no longer referenced as the owner of an object.
public class MemberServiceDeleteMembersTest extends BaseIntegrationTest {

    private static final String COUNT_ENTRIES_OF_SID = """
            SELECT count(*) FROM acl_entry e JOIN acl_sid s ON e.sid = s.id WHERE s.sid = ?
            """;

    private static final String COUNT_SID_ROWS = "SELECT count(*) FROM acl_sid WHERE sid = ?";

    private static final String DELETE_ENTRIES_OF_OWNED_OBJECTS = """
            DELETE FROM acl_entry
            WHERE acl_object_identity IN (SELECT id FROM acl_object_identity
                                          WHERE owner_sid IN (SELECT id FROM acl_sid WHERE sid = ?))
            """;

    private static final String DELETE_OBJECTS_OWNED_BY_SID =
            "DELETE FROM acl_object_identity WHERE owner_sid IN (SELECT id FROM acl_sid WHERE sid = ?)";

    private static final String DELETE_SID_ROWS = "DELETE FROM acl_sid WHERE sid IN (?, ?, ?)";

    @Autowired
    private MemberService memberService;

    @Autowired
    private MutableAclService aclService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private String ownerUuid;

    private ObjectIdentity identity;

    private User leaving;

    private User staying;

    @BeforeEach
    public void setup() {
        ownerUuid = UUID.randomUUID().toString();
        leaving = user();
        staying = user();
        identity = new ObjectIdentityImpl(Metadata.class, "http://localhost:8088/catalog/acl-" + UUID.randomUUID());

        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(ownerUuid, "", List.of()));
        SecurityContextHolder.setContext(context);

        // the ACL service writes the class and the SID rows, which it only does in a transaction
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            final MutableAcl acl = aclService.createAcl(identity);
            acl.insertAce(0, BasePermission.WRITE, new PrincipalSid(leaving.getUuid().toString()), true);
            acl.insertAce(1, BasePermission.CREATE, new PrincipalSid(leaving.getUuid().toString()), true);
            acl.insertAce(2, BasePermission.WRITE, new PrincipalSid(staying.getUuid().toString()), true);
            aclService.updateAcl(acl);
        });
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
        // children before parents, so that a failing test leaves no rows behind either
        jdbcTemplate.update(DELETE_ENTRIES_OF_OWNED_OBJECTS, ownerUuid);
        jdbcTemplate.update(DELETE_OBJECTS_OWNED_BY_SID, ownerUuid);
        jdbcTemplate.update(DELETE_SID_ROWS,
                ownerUuid, leaving.getUuid().toString(), staying.getUuid().toString());
    }

    private User user() {
        final UUID uuid = UUID.randomUUID();
        return User.builder()
                .uuid(uuid)
                .firstName("Acl")
                .lastName("Member")
                .email("acl-member-" + uuid + "@example.com")
                .passwordHash("irrelevant-hash")
                .role(UserRole.USER)
                .build();
    }

    private int countEntriesOf(User user) {
        return jdbcTemplate.queryForObject(COUNT_ENTRIES_OF_SID, Integer.class, user.getUuid().toString());
    }

    private int countSidRowsOf(String sid) {
        return jdbcTemplate.queryForObject(COUNT_SID_ROWS, Integer.class, sid);
    }

    @Test
    @DisplayName("Deleting the members of one user leaves the entries of the other users alone")
    public void deleteMembersRemovesOnlyTheGivenUser() {
        // GIVEN: two users hold entries on the same object
        assertThat(countEntriesOf(leaving), is(equalTo(2)));
        assertThat(countEntriesOf(staying), is(equalTo(1)));

        // WHEN: the memberships of one of them are deleted
        memberService.deleteMembers(leaving);

        // THEN: only that user's entries are gone, together with the now unreferenced SID row
        assertThat(countEntriesOf(leaving), is(equalTo(0)));
        assertThat(countSidRowsOf(leaving.getUuid().toString()), is(equalTo(0)));
        assertThat(countEntriesOf(staying), is(equalTo(1)));
        assertThat(countSidRowsOf(staying.getUuid().toString()), is(equalTo(1)));

        // AND: the access control list still holds the remaining entry; the setup left the list in
        // the ACL cache, so reading fewer entries back also shows that the cache was invalidated
        final Acl reloaded = aclService.readAclById(identity);
        assertThat(reloaded.getEntries().size(), is(equalTo(1)));
        assertThat(reloaded.getEntries().getFirst().getSid(),
                is(equalTo(new PrincipalSid(staying.getUuid().toString()))));
    }

    @Test
    @DisplayName("The SID row of a user that still owns an object is kept so the foreign key holds")
    public void deleteMembersKeepsTheSidRowOfAnOwner() {
        // GIVEN: the owner of the object identity, who holds no entry of their own
        final User owner = User.builder().uuid(UUID.fromString(ownerUuid)).build();
        assertThat(countEntriesOf(owner), is(equalTo(0)));
        assertThat(countSidRowsOf(ownerUuid), is(equalTo(1)));

        // WHEN: their memberships are deleted
        memberService.deleteMembers(owner);

        // THEN: the SID row survives, because acl_object_identity.owner_sid still points at it
        assertThat(countSidRowsOf(ownerUuid), is(equalTo(1)));
    }

}
