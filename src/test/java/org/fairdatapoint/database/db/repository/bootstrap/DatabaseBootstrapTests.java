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
import org.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureTestEntityManager
@Transactional
public class DatabaseBootstrapTests extends BaseIntegrationTest {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private SearchSavedQueryRepository searchSavedQueryRepository;

    private final String einsteinEmail = "albert.einstein@example.org";

    @Test
    public void testSingleEntityBootstrap() {
        final Optional<UserAccount> userAccount = userAccountRepository.findByEmail(einsteinEmail);
        assertTrue(userAccount.isPresent());
        assertEquals("Albert", userAccount.get().getFirstName());
        assertEquals("Einstein", userAccount.get().getLastName());
        assertEquals(KnownUUIDs.USER_ALBERT_UUID, userAccount.get().getUuid());
    }

    @Test
    public void testRelatedEntityBootstrap() {
        final UUID einsteinApiKeyUuid = UUID.fromString("a1c00673-24c5-4e0a-bdbe-22e961ee7548");
        final String einsteinApiKeyToken = "a274793046e34a219fd0ea6362fcca61a001500b71724f4c973a017031653c20";
        final Optional<ApiKey> apiKey = apiKeyRepository.findByToken(einsteinApiKeyToken);
        assertTrue(apiKey.isPresent());
        assertEquals(einsteinEmail, apiKey.get().getUserAccount().getEmail());
        assertEquals(einsteinApiKeyUuid, apiKey.get().getUuid());
    }

    @Test
    public void testDuplicateIdEntityOverwriteBootstrap() {
        final Optional<SearchSavedQuery> savedQuery = searchSavedQueryRepository.findByUuid(
                UUID.fromString("4c57eff3-4608-40ae-85af-b442cfea0746"));
        assertTrue(savedQuery.isPresent());
        assertEquals(einsteinEmail, savedQuery.get().getUserAccount().getEmail());
        assertEquals("Some query 2", savedQuery.get().getName());
    }
}
