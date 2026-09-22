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
import org.fairdatateam.fairdatapoint.rdf.metadata.Metadata;
import org.fairdatateam.fairdatapoint.reset.FactoryDefaults;
import org.fairdatateam.fairdatapoint.security.auth.AuthenticationService;
import org.fairdatateam.fairdatapoint.security.membership.MemberService;
import org.fairdatateam.rdf.migration.entity.RdfMigrationAnnotation;
import org.fairdatateam.rdf.migration.runner.RdfProductionMigration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@RdfMigrationAnnotation(
        number = 1,
        name = "Init migration",
        description = "Load basic fixtures for repository, catalog, dataset and distribution")
@Service
public class Rdf_Migration_0001_Init implements RdfProductionMigration {

    @Autowired
    private Repository repository;

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Autowired
    private IRI license;

    @Autowired
    private IRI language;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MemberService memberService;

    // the runner takes this migration from the application context and calls it directly, so a
    // @Transactional annotation would not necessarily be applied: the transaction is opened here
    private final TransactionTemplate transactionTemplate;

    public Rdf_Migration_0001_Init(final PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void runMigration() {
        createRepositoryInTripleStore();
        storePermissionForRepository();
    }

    private void createRepositoryInTripleStore() {
        try (RepositoryConnection conn = repository.getConnection()) {
            final List<Statement> statements = FactoryDefaults.repositoryStatements(
                    persistentUrl,
                    license,
                    language,
                    accessRightsDescription
            );
            conn.add(statements);
        }
    }

    private void storePermissionForRepository() {
        // the ACL service derives the owner of a new access control list from the security context,
        // and this migration runs at start-up, outside any request: install the seeded administrator
        // for the duration of the call and leave no context behind on the start-up thread
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationService.getAuthentication(KnownUUIDs.USER_ALBERT_UUID));
        SecurityContextHolder.setContext(context);
        try {
            transactionTemplate.executeWithoutResult(status -> {
                // TODO(PR 14): the owner of the root ACL must become the bootstrapped first
                // administrator (D7), not the seeded fixture account
                memberService.createOwner(persistentUrl, Metadata.class, KnownUUIDs.USER_ALBERT_UUID);
            });
        }
        finally {
            SecurityContextHolder.clearContext();
        }
    }

}
