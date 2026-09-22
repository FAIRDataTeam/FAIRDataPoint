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
package org.fairdatateam.fairdatapoint.search;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.SearchSavedQueryFixtures;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.UserMigration;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.UserRepository;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// NOTE: none of the test methods here are transactional, so `save` and the reload that follows it
// are two separate round trips to the database rather than a lookup in a shared first-level cache.
public class SearchSavedQueryRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private SearchSavedQueryRepository searchSavedQueryRepository;

    @Autowired
    private SearchSavedQueryFixtures searchSavedQueryFixtures;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMigration userMigration;

    @BeforeEach
    public void setup() {
        // the saved queries reference their owner, so the users have to be there first
        userMigration.runMigration();
        searchSavedQueryRepository.deleteAll();
    }

    private User createUser() {
        final UUID uuid = UUID.randomUUID();
        return userRepository.save(User.builder()
                .uuid(uuid)
                .firstName("Saved")
                .lastName("Query")
                .email("saved-query-" + uuid + "@example.com")
                .passwordHash("irrelevant-hash")
                .role(UserRole.USER)
                .build());
    }

    @Test
    @DisplayName("A saved query round-trips with its owner, its variables, and its audit timestamps")
    public void savedQueryRoundTripsThroughJpaMapping() {
        // GIVEN: the private fixture query, owned by Nikola, is saved
        final SearchSavedQuery saved = searchSavedQueryRepository.save(
                searchSavedQueryFixtures.savedQueryPrivate01()
        );

        // WHEN: it is read back by the string form of its identifier, as the REST layer does
        final SearchSavedQuery reloaded = searchSavedQueryRepository
                .findByUuid(saved.getUuid().toString())
                .orElseThrow();

        // THEN: the scalar columns and the enum come back unchanged
        assertThat(reloaded.getName(), is(equalTo("Things with data")));
        assertThat(reloaded.getType(), is(equalTo(SearchSavedQueryType.PRIVATE)));

        // THEN: the embedded variables come back from their three columns, empty string included
        assertThat(reloaded.getVariables().getPrefixes(), is(equalTo("")));
        assertThat(reloaded.getVariables().getGraphPattern(),
                is(equalTo(searchSavedQueryFixtures.savedQueryPrivate01().getVariables().getGraphPattern())));
        assertThat(reloaded.getVariables().getOrdering(), is(equalTo("ASC(?title)")));

        // THEN: the owner is the one the fixture named, fetched along with the query
        assertThat(reloaded.getUser().getUuid(), is(equalTo(UUID.fromString(KnownUUIDs.USER_NIKOLA_UUID))));

        // THEN: the audit timestamps have been populated by Hibernate rather than by the service
        assertThat(reloaded.getCreatedAt(), is(notNullValue()));
        assertThat(reloaded.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    @DisplayName("Deleting a user deletes the saved queries it owns")
    public void deletingUserCascadesToItsSavedQueries() {
        // GIVEN: a user of its own with a saved query
        final User owner = createUser();
        final SearchSavedQuery query = searchSavedQueryRepository.save(
                searchSavedQueryFixtures.savedQueryPublic01().toBuilder().user(owner).build()
        );
        assertThat(searchSavedQueryRepository.findByUuid(query.getUuid()).isPresent(), is(equalTo(true)));

        // WHEN: the owner is deleted
        userRepository.delete(owner);

        // THEN: the saved query is gone as well, through the foreign key of the schema
        assertThat(searchSavedQueryRepository.findByUuid(query.getUuid()).isPresent(), is(equalTo(false)));
    }

    @Test
    @DisplayName("Listing saved queries reports them in creation order")
    public void findAllReportsSavedQueriesInCreationOrder() {
        // GIVEN: three saved queries, stored one after another
        final SearchSavedQuery first = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());
        final SearchSavedQuery second =
                searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryInternal01());
        final SearchSavedQuery third = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPrivate01());

        // WHEN: all of them are listed
        final List<SearchSavedQuery> listed = searchSavedQueryRepository.findAllByOrderByCreatedAtAscUuidAsc();

        // THEN: every saved query is reported back, regardless of its position
        assertThat(
                listed.stream().map(SearchSavedQuery::getUuid).toList(),
                containsInAnyOrder(first.getUuid(), second.getUuid(), third.getUuid())
        );

        // THEN: the creation timestamps come back in non-decreasing order
        for (int index = 1; index < listed.size(); index++) {
            assertThat(
                    listed.get(index).getCreatedAt().isBefore(listed.get(index - 1).getCreatedAt()),
                    is(false)
            );
        }
    }

    @Test
    @DisplayName("A malformed, missing, or unknown identifier is not found instead of raising an error")
    public void findByUuidStringIgnoresMalformedMissingOrUnknownIdentifier() {
        assertThat(searchSavedQueryRepository.findByUuid("nonExisting").isPresent(), is(equalTo(false)));
        assertThat(searchSavedQueryRepository.findByUuid((String) null).isPresent(), is(equalTo(false)));
        assertThat(searchSavedQueryRepository.findByUuid(UUID.randomUUID().toString()).isPresent(),
                is(equalTo(false)));
    }
}
