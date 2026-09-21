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
package org.fairdatateam.fairdatapoint.user;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.UserMigration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMigration userMigration;

    @BeforeEach
    public void setup() {
        userMigration.runMigration();
    }

    @Test
    @DisplayName("A known identifier in string form resolves to the same user as the typed one")
    public void findByUuidStringResolvesKnownUser() {
        final Optional<User> user = userRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID);

        assertThat(user.isPresent(), is(equalTo(true)));
        assertThat(user.get().getUuid(), is(equalTo(UUID.fromString(KnownUUIDs.USER_ALBERT_UUID))));
        assertThat(user.get().getEmail(), is(equalTo("albert.einstein@example.com")));
    }

    @Test
    @DisplayName("A malformed identifier is not found instead of raising an error")
    public void findByUuidStringIgnoresMalformedIdentifier() {
        assertThat(userRepository.findByUuid("nonExisting").isPresent(), is(equalTo(false)));
        assertThat(userRepository.findByUuid("").isPresent(), is(equalTo(false)));
        assertThat(userRepository.findByUuid((String) null).isPresent(), is(equalTo(false)));
    }

    @Test
    @DisplayName("A well-formed but unknown identifier is not found")
    public void findByUuidStringIgnoresUnknownIdentifier() {
        assertThat(userRepository.findByUuid(UUID.randomUUID().toString()).isPresent(), is(equalTo(false)));
    }

    // NOTE: `save` followed by `findByUuid` in the same test method are two separate repository
    // calls, each transactional on its own (there is no @Transactional on this test), so the
    // second call is a genuine round trip through the database rather than the first-level cache
    // returning the same Java instance.
    @Test
    @DisplayName("A saved user round-trips through the JPA mapping, including audit timestamps")
    public void savedUserRoundTripsThroughJpaMapping() {
        // GIVEN: a new user built with the builder and saved
        final UUID uuid = UUID.randomUUID();
        final String email = "round-trip-" + uuid + "@example.com";
        final User newUser = User.builder()
                .uuid(uuid)
                .firstName("Round")
                .lastName("Trip")
                .email(email)
                .passwordHash("irrelevant-hash")
                .role(UserRole.USER)
                .build();
        userRepository.save(newUser);

        // WHEN: the user is reloaded for the first time
        final User firstReload = userRepository.findByUuid(uuid.toString()).orElseThrow();

        // THEN: the role is mapped and the audit timestamps have been populated
        assertThat(firstReload.getRole(), is(equalTo(UserRole.USER)));
        assertThat(firstReload.getCreatedAt(), is(notNullValue()));
        assertThat(firstReload.getUpdatedAt(), is(notNullValue()));

        // GIVEN: the reloaded user is changed and saved again
        firstReload.setFirstName("Changed");
        userRepository.save(firstReload);

        // WHEN: the user is reloaded a second time
        final User secondReload = userRepository.findByUuid(uuid.toString()).orElseThrow();

        // THEN: updatedAt has not moved backwards and createdAt has not changed
        assertThat(secondReload.getUpdatedAt().isBefore(firstReload.getUpdatedAt()), is(false));
        assertThat(secondReload.getCreatedAt(), is(equalTo(firstReload.getCreatedAt())));

        // CLEANUP: remove the user created here so the fixture set matches what other tests
        // expect; the @BeforeEach reseed in this class would also cover this on the next test.
        userRepository.delete(secondReload);
    }
}
