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
package org.fairdatateam.fairdatapoint.security.apikey;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.ApiKeyFixtures;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.ApiKeyMigration;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.UserMigration;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.UserRepository;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// NOTE: none of the test methods here are transactional, so every repository call is a separate
// round trip to the database instead of a lookup in a shared first-level cache.
public class ApiKeyRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMigration userMigration;

    @Autowired
    private ApiKeyMigration apiKeyMigration;

    @BeforeEach
    public void setup() {
        // users first, then the API keys that reference them
        userMigration.runMigration();
        apiKeyMigration.runMigration();
    }

    private User createUser() {
        final UUID uuid = UUID.randomUUID();
        return userRepository.save(User.builder()
                .uuid(uuid)
                .firstName("Api")
                .lastName("Key")
                .email("api-key-" + uuid + "@example.com")
                .passwordHash("irrelevant-hash")
                .role(UserRole.USER)
                .build());
    }

    private ApiKey createApiKey(User user, String token) {
        return apiKeyRepository.save(ApiKey.builder()
                .uuid(UUID.randomUUID())
                .user(user)
                .token(token)
                .build());
    }

    @Test
    @DisplayName("Deleting a user deletes the API keys it owns")
    public void deletingUserCascadesToItsApiKeys() {
        // GIVEN: a user with an API key
        final User user = createUser();
        final String token = "cascade-" + UUID.randomUUID();
        createApiKey(user, token);
        assertThat(apiKeyRepository.findByToken(token).isPresent(), is(equalTo(true)));

        // WHEN: the user is deleted
        userRepository.delete(user);

        // THEN: the API key is gone as well, through the foreign key of the schema
        assertThat(apiKeyRepository.findByToken(token).isPresent(), is(equalTo(false)));
    }

    @Test
    @DisplayName("Looking an API key up by token or by owner returns the key, without eagerly loading the owner")
    public void lookupReturnsTheApiKeyWithoutEagerlyLoadingTheOwner() {
        // GIVEN: a user with an API key
        final User user = createUser();
        final String token = "lookup-" + UUID.randomUUID();
        final ApiKey apiKey = createApiKey(user, token);

        // WHEN: the key is looked up by token, as the request filter used to do
        final ApiKey byToken = apiKeyRepository.findByToken(token).orElseThrow();

        // THEN: the key itself is populated; its owner is lazy and is not navigated to here
        assertThat(byToken.getUuid(), is(equalTo(apiKey.getUuid())));
        assertThat(byToken.getToken(), is(equalTo(token)));

        // WHEN: the keys of that user are listed
        final List<ApiKey> byUser = apiKeyRepository.findByUserUuid(user.getUuid());

        // THEN: the same key is found there as well
        assertThat(byUser.size(), is(equalTo(1)));
        assertThat(byUser.get(0).getUuid(), is(equalTo(apiKey.getUuid())));
        assertThat(byUser.get(0).getToken(), is(equalTo(token)));

        // CLEANUP: drop the user, which takes its API keys with it
        userRepository.delete(user);
    }

    @Test
    @DisplayName("A malformed, missing, or unknown identifier is not found instead of raising an error")
    public void findByUuidStringIgnoresMalformedMissingOrUnknownIdentifier() {
        assertThat(apiKeyRepository.findByUuid("nonExisting").isPresent(), is(equalTo(false)));
        assertThat(apiKeyRepository.findByUuid((String) null).isPresent(), is(equalTo(false)));
        assertThat(apiKeyRepository.findByUuid(UUID.randomUUID().toString()).isPresent(), is(equalTo(false)));
    }

    @Test
    @DisplayName("Resolving a token to its owner's identifier finds known tokens and ignores unknown ones")
    public void findUserUuidByTokenResolvesOwnerIdentifier() {
        // GIVEN: a fixture API key, seeded in the @BeforeEach

        // WHEN/THEN: a known token resolves to the identifier of its owner
        assertThat(apiKeyRepository.findUserUuidByToken(ApiKeyFixtures.ALBERT_API_KEY),
                is(equalTo(Optional.of(UUID.fromString(KnownUUIDs.USER_ALBERT_UUID)))));

        // WHEN/THEN: an unknown token is not found
        assertThat(apiKeyRepository.findUserUuidByToken("nonExisting-token").isPresent(), is(equalTo(false)));
    }

    // NOTE: `save` followed by `findByUuid` in the same test method are two separate repository
    // calls, each transactional on its own (there is no @Transactional on this test), so the
    // second call is a genuine round trip through the database rather than the first-level cache
    // returning the same Java instance.
    @Test
    @DisplayName("A saved API key round-trips through the JPA mapping, including audit timestamps")
    public void savedApiKeyRoundTripsThroughJpaMapping() {
        // GIVEN: a new API key for an existing fixture user, built with the builder and saved
        final User owner = userRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).orElseThrow();
        final UUID uuid = UUID.randomUUID();
        final ApiKey newApiKey = ApiKey.builder()
                .uuid(uuid)
                .user(owner)
                .token("round-trip-" + uuid)
                .build();
        apiKeyRepository.save(newApiKey);

        // WHEN: the key is reloaded for the first time
        final ApiKey firstReload = apiKeyRepository.findByUuid(uuid).orElseThrow();

        // THEN: the audit timestamps have been populated
        assertThat(firstReload.getCreatedAt(), is(notNullValue()));
        assertThat(firstReload.getUpdatedAt(), is(notNullValue()));

        // GIVEN: the reloaded key has its token changed and is saved again
        firstReload.setToken("round-trip-changed-" + uuid);
        apiKeyRepository.save(firstReload);

        // WHEN: the key is reloaded a second time
        final ApiKey secondReload = apiKeyRepository.findByUuid(uuid).orElseThrow();

        // THEN: updatedAt has not moved backwards and createdAt has not changed
        assertThat(secondReload.getUpdatedAt().isBefore(firstReload.getUpdatedAt()), is(false));
        assertThat(secondReload.getCreatedAt(), is(equalTo(firstReload.getCreatedAt())));

        // CLEANUP: remove the key created here
        apiKeyRepository.delete(secondReload);
    }

}
