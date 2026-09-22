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
package org.fairdatateam.fairdatapoint.security.acl;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.rdf.metadata.Metadata;
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
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

// The relational store of the testing profile is H2 while production is PostgreSQL, so these tests
// mainly guard that the statements of PortableJdbcMutableAclService contain no vendor-specific
// function: the same code path is what the acceptance tests under acceptance/metadata/*/member use.
public class PortableJdbcMutableAclServiceTest extends BaseIntegrationTest {

    private static final String COUNT_CLASS_ROWS = "SELECT count(*) FROM acl_class WHERE class = ?";

    private static final String COUNT_SID_ROWS = "SELECT count(*) FROM acl_sid WHERE sid = ?";

    private static final String DELETE_ENTRIES_OF_SID =
            "DELETE FROM acl_entry WHERE sid IN (SELECT id FROM acl_sid WHERE sid = ?)";

    private static final String DELETE_OBJECTS_OWNED_BY_SID =
            "DELETE FROM acl_object_identity WHERE owner_sid IN (SELECT id FROM acl_sid WHERE sid = ?)";

    private static final String DELETE_SID_ROW = "DELETE FROM acl_sid WHERE sid = ?";

    @Autowired
    private MutableAclService aclService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private String principal;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setup() {
        // the service writes the class and the SID row, which it only does inside a transaction
        transactionTemplate = new TransactionTemplate(transactionManager);
        // the ACL service takes the owner of a new list from the security context
        principal = UUID.randomUUID().toString();
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal, "", List.of()));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
        // children before parents, so that a failing test leaves no rows behind either
        jdbcTemplate.update(DELETE_ENTRIES_OF_SID, principal);
        jdbcTemplate.update(DELETE_OBJECTS_OWNED_BY_SID, principal);
        jdbcTemplate.update(DELETE_SID_ROW, principal);
    }

    private ObjectIdentity freshIdentity() {
        return new ObjectIdentityImpl(Metadata.class, "http://localhost:8088/catalog/acl-" + UUID.randomUUID());
    }

    private int countClassRows() {
        return jdbcTemplate.queryForObject(COUNT_CLASS_ROWS, Integer.class, Metadata.class.getName());
    }

    private int countSidRows() {
        return jdbcTemplate.queryForObject(COUNT_SID_ROWS, Integer.class, principal);
    }

    @Test
    @DisplayName("An access control entry written through the JDBC service is read back unchanged")
    public void insertedEntryIsReadBack() {
        // GIVEN: an object identity that has no access control list yet
        final ObjectIdentity identity = freshIdentity();

        // WHEN: a list is created for it and one entry is inserted
        transactionTemplate.executeWithoutResult(status -> {
            final MutableAcl acl = aclService.createAcl(identity);
            acl.insertAce(0, BasePermission.WRITE, new PrincipalSid(principal), true);
            aclService.updateAcl(acl);
        });

        // THEN: reading the list back yields exactly that entry, for that principal
        final Acl reloaded = aclService.readAclById(identity);
        final List<AccessControlEntry> entries = reloaded.getEntries();
        assertThat(entries.size(), is(equalTo(1)));
        assertThat(entries.getFirst().getPermission().getMask(), is(equalTo(BasePermission.WRITE.getMask())));
        assertThat(entries.getFirst().getSid(), is(equalTo(new PrincipalSid(principal))));
        assertThat(reloaded.getOwner(), is(equalTo(new PrincipalSid(principal))));

        // CLEANUP:
        aclService.deleteAcl(identity, false);
    }

    @Test
    @DisplayName("The class and the SID row are created once and reused by the next access control list")
    public void classAndSidRowsAreCreatedOnceAndReused() {
        // GIVEN: no SID row exists for this principal yet (the class row may exist from the fixtures)
        assertThat(countSidRows(), is(equalTo(0)));

        // WHEN: a first access control list is created for that principal
        final ObjectIdentity first = freshIdentity();
        transactionTemplate.executeWithoutResult(status -> {
            final MutableAcl firstAcl = aclService.createAcl(first);
            firstAcl.insertAce(0, BasePermission.WRITE, new PrincipalSid(principal), true);
            aclService.updateAcl(firstAcl);
        });

        // THEN: exactly one class row and one SID row back the list
        assertThat(countClassRows(), is(equalTo(1)));
        assertThat(countSidRows(), is(equalTo(1)));

        // WHEN: a second list is created for the same class and the same principal
        final ObjectIdentity second = freshIdentity();
        transactionTemplate.executeWithoutResult(status -> {
            final MutableAcl secondAcl = aclService.createAcl(second);
            secondAcl.insertAce(0, BasePermission.READ, new PrincipalSid(principal), true);
            aclService.updateAcl(secondAcl);
        });

        // THEN: both rows were retrieved instead of inserted a second time
        assertThat(countClassRows(), is(equalTo(1)));
        assertThat(countSidRows(), is(equalTo(1)));

        // CLEANUP:
        aclService.deleteAcl(first, false);
        aclService.deleteAcl(second, false);
    }

    @Test
    @DisplayName("Deleting an access control list makes it unreadable again")
    public void deletedAclIsGone() {
        // GIVEN: an object identity with an access control list
        final ObjectIdentity identity = freshIdentity();
        transactionTemplate.executeWithoutResult(status -> {
            final MutableAcl acl = aclService.createAcl(identity);
            acl.insertAce(0, BasePermission.ADMINISTRATION, new PrincipalSid(principal), true);
            aclService.updateAcl(acl);
        });

        // WHEN: the list is deleted
        aclService.deleteAcl(identity, false);

        // THEN: it is not found any more
        assertThrows(NotFoundException.class, () -> aclService.readAclById(identity));
    }

}
