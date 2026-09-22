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
package org.fairdatateam.fairdatapoint.migration.triplestore.production;

import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.security.auth.AuthenticationService;
import org.fairdatateam.fairdatapoint.security.membership.MemberService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.util.ArrayList;
import java.util.List;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// The production migration runs on the start-up thread, which carries no security context, and the
// runner calls it without a Spring proxy around it: it has to install and remove the context itself
// and to open the transaction that the ACL writes need.
public class Rdf_Migration_0001_InitTest {

    private static final String PERSISTENT_URL = "http://localhost:8088";

    private final MemberService memberService = mock(MemberService.class);

    private final AuthenticationService authenticationService = mock(AuthenticationService.class);

    private Rdf_Migration_0001_Init migration;

    // a transaction manager that hands out a status object and does nothing on commit or rollback
    private static PlatformTransactionManager stubTransactionManager() {
        return new PlatformTransactionManager() {

            @Override
            public TransactionStatus getTransaction(final TransactionDefinition definition) {
                return new SimpleTransactionStatus();
            }

            @Override
            public void commit(final TransactionStatus status) {
                // nothing to commit: the collaborators of this test are mocks
            }

            @Override
            public void rollback(final TransactionStatus status) {
                // nothing to roll back: the collaborators of this test are mocks
            }
        };
    }

    @BeforeEach
    public void setup() {
        final Repository repository = mock(Repository.class);
        when(repository.getConnection()).thenReturn(mock(RepositoryConnection.class));
        when(authenticationService.getAuthentication(KnownUUIDs.USER_ALBERT_UUID)).thenReturn(
                UsernamePasswordAuthenticationToken.authenticated(KnownUUIDs.USER_ALBERT_UUID, "", List.of())
        );

        migration = new Rdf_Migration_0001_Init(stubTransactionManager());
        ReflectionTestUtils.setField(migration, "repository", repository);
        ReflectionTestUtils.setField(migration, "persistentUrl", PERSISTENT_URL);
        ReflectionTestUtils.setField(migration, "accessRightsDescription", "No access restriction");
        ReflectionTestUtils.setField(migration, "license", license());
        ReflectionTestUtils.setField(migration, "language", language());
        ReflectionTestUtils.setField(migration, "authenticationService", authenticationService);
        ReflectionTestUtils.setField(migration, "memberService", memberService);
    }

    @AfterEach
    public void teardown() {
        SecurityContextHolder.clearContext();
    }

    private IRI license() {
        return i("http://purl.org/NET/rdflicense/cc-by-nc-nd4.0");
    }

    private IRI language() {
        return i("http://id.loc.gov/vocabulary/iso639-1/en");
    }

    @Test
    @DisplayName("The access control list is seeded as the administrator, and no context is left behind")
    public void seedsTheAclAsTheAdministratorAndClearsTheContext() {
        // GIVEN: a member service that records the authentication in force while it is called
        final List<String> namesWhileSeeding = new ArrayList<>();
        doAnswer(invocation -> {
            namesWhileSeeding.add(SecurityContextHolder.getContext().getAuthentication().getName());
            return null;
        }).when(memberService).createOwner(anyString(), any(), anyString());

        // WHEN: the migration runs
        migration.runMigration();

        // THEN: the seeded administrator was authenticated for the duration of the seeding
        assertThat(namesWhileSeeding, is(equalTo(List.of(KnownUUIDs.USER_ALBERT_UUID))));

        // AND: the start-up thread is left without a security context
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
    }

    @Test
    @DisplayName("The security context is cleared even when the seeding fails")
    public void clearsTheContextWhenTheSeedingFails() {
        // GIVEN: a member service that rejects the seeding
        doThrow(new IllegalStateException("seeding refused"))
                .when(memberService).createOwner(anyString(), any(), anyString());

        // WHEN: the migration runs, THEN: the failure propagates
        assertThrows(IllegalStateException.class, () -> migration.runMigration());

        // AND: the start-up thread is still left without a security context
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
    }

}
