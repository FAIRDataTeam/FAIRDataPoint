/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.database.db.repository.bootstrap;


import jakarta.transaction.Transactional;
import org.fairdatapoint.BaseIntegrationTest;
import org.fairdatapoint.database.db.repository.ApiKeyRepository;
import org.fairdatapoint.database.db.repository.SearchSavedQueryRepository;
import org.fairdatapoint.database.db.repository.UserAccountRepository;
import org.fairdatapoint.entity.apikey.ApiKey;
import org.fairdatapoint.entity.search.SearchSavedQuery;
import org.fairdatapoint.entity.user.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestEntityManager
@Transactional
@TestPropertySource(
        properties = """
            bootstrap.enabled=true
            bootstrap.db-fixtures-dirs=src/test/resources/fixtures
        """
)
public class DatabaseBootstrapTests extends BaseIntegrationTest {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private SearchSavedQueryRepository searchSavedQueryRepository;

    @Test
    public void testSingleEntityBootstrap() {
        final Optional<UserAccount> userAccount = userAccountRepository.findByEmail("john.doe@example.org");
        assertEquals(true, userAccount.isPresent());
        assertEquals("John", userAccount.get().getFirstName());
        assertEquals("Doe", userAccount.get().getLastName());
        assertEquals(UUID.fromString("e8f98d8e-0c4f-4a4b-9cc7-dd884f0c75ee"), userAccount.get().getUuid());
    }

    @Test
    public void testRelatedEntityBootstrap() {
        final Optional<ApiKey> apiKey = apiKeyRepository.findByToken("testing-token");
        assertEquals(true, apiKey.isPresent());
        assertEquals("john.doe@example.org", apiKey.get().getUserAccount().getEmail());
        assertEquals(UUID.fromString("9d734008-91bb-47e3-97aa-2f537e67d9e6"), apiKey.get().getUuid());
    }

    @Test
    public void testDuplicateIdEntityOverwriteBootstrap() {
        final Optional<SearchSavedQuery> savedQuery = searchSavedQueryRepository.findByUuid(UUID.fromString("4c57eff3-4608-40ae-85af-b442cfea0746"));
        assertEquals(true, savedQuery.isPresent());
        assertEquals("john.doe@example.org", savedQuery.get().getUserAccount().getEmail());
        assertEquals("Some query 2", savedQuery.get().getName());
    }
}
