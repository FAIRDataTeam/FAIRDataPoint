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
package org.fairdatateam.fairdatapoint.index.settings;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// NOTE: none of the test methods here are transactional, so `save` and the reload that follows it
// are two separate round trips to the database rather than a lookup in a shared first-level cache.
public class IndexSettingsRepositoryTest extends BaseIntegrationTest {

    private static final String DENY_LOCALHOST = "^(http|https)://localhost(:[0-9]+){0,1}.*$";

    private static final String DENY_EXAMPLE = "^https://deny\\.example\\.com/.*$";

    private static final int RATE_LIMIT_HITS = 42;

    @Autowired
    private IndexSettingsRepository indexSettingsRepository;

    @BeforeEach
    public void setup() {
        // the Index runs on a single row of settings, and the acceptance tests expect to find
        // none of it left behind
        indexSettingsRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        indexSettingsRepository.deleteAll();
    }

    @Test
    @DisplayName("Settings round-trip with their durations as ISO-8601 text and with their deny list")
    public void settingsRoundTripThroughJpaMapping() {
        // GIVEN: settings with two deny patterns and durations of every size
        final IndexSettings settings = IndexSettings
                .builder()
                .retrieval(IndexSettingsRetrieval
                        .builder()
                        .rateLimitWait(Duration.ofHours(16))
                        .timeout(Duration.ofSeconds(55))
                        .build())
                .ping(IndexSettingsPing
                        .builder()
                        .validDuration(Duration.ofDays(5))
                        .rateLimitDuration(Duration.ofMinutes(17))
                        .rateLimitHits(RATE_LIMIT_HITS)
                        .denyList(Set.of(DENY_LOCALHOST, DENY_EXAMPLE))
                        .build())
                .autoPermit(false)
                .build();

        // WHEN: they are saved and read back the way the service reads them
        final IndexSettings saved = indexSettingsRepository.save(settings);
        final IndexSettings reloaded =
                indexSettingsRepository.findFirstByOrderByCreatedAtAsc().orElseThrow();

        // THEN: the identifier was assigned on that first save
        assertThat(saved.getUuid(), is(notNullValue()));
        assertThat(reloaded.getUuid(), is(equalTo(saved.getUuid())));

        // THEN: every duration comes back as the very duration that went in, through the text
        // column the converter writes it to
        assertThat(reloaded.getRetrieval().getRateLimitWait(), is(equalTo(Duration.ofHours(16))));
        assertThat(reloaded.getRetrieval().getTimeout(), is(equalTo(Duration.ofSeconds(55))));
        assertThat(reloaded.getPing().getValidDuration(), is(equalTo(Duration.ofDays(5))));
        assertThat(reloaded.getPing().getRateLimitDuration(), is(equalTo(Duration.ofMinutes(17))));

        // THEN: so do the remaining settings, including the deny list from its own table
        assertThat(reloaded.getPing().getRateLimitHits(), is(equalTo(RATE_LIMIT_HITS)));
        assertThat(reloaded.getPing().getDenyList(),
                containsInAnyOrder(DENY_LOCALHOST, DENY_EXAMPLE));
        assertThat(reloaded.getAutoPermit(), is(equalTo(false)));

        // THEN: the audit timestamps have been populated by Hibernate
        assertThat(reloaded.getCreatedAt(), is(notNullValue()));
        assertThat(reloaded.getUpdatedAt(), is(notNullValue()));
    }

    @Test
    @DisplayName("Stored default settings still compare equal to the defaults, deny list included")
    public void storedDefaultsCompareEqualToInMemoryDefaults() {
        // GIVEN: the defaults, stored
        final IndexSettings defaults = IndexSettings
                .builder()
                .retrieval(IndexSettingsRetrieval.getDefault())
                .ping(IndexSettingsPing.getDefault())
                .autoPermit(true)
                .build();
        indexSettingsRepository.save(defaults);

        // WHEN: they are read back
        final IndexSettings reloaded =
                indexSettingsRepository.findFirstByOrderByCreatedAtAsc().orElseThrow();

        // THEN: they are still recognised as the defaults, which is what the API reports as
        // `isDefault` - the deny list has to be compared by content, not by identity, for that
        final IndexSettings inMemoryDefaults = IndexSettings
                .builder()
                .retrieval(IndexSettingsRetrieval.getDefault())
                .ping(IndexSettingsPing.getDefault())
                .autoPermit(true)
                .build();
        assertThat(reloaded, is(equalTo(inMemoryDefaults)));
    }

    @Test
    @DisplayName("A duplicate pattern in the deny list is collapsed rather than tripping the "
            + "collection table's primary key")
    public void denyListDeduplicatesRepeatedPatterns() {
        // GIVEN: settings whose deny set was built from a list holding a repeated pattern
        final IndexSettings settings = IndexSettings
                .builder()
                .retrieval(IndexSettingsRetrieval.getDefault())
                .ping(IndexSettingsPing
                        .builder()
                        .validDuration(Duration.ofDays(5))
                        .rateLimitDuration(Duration.ofMinutes(17))
                        .rateLimitHits(RATE_LIMIT_HITS)
                        .denyList(new TreeSet<>(List.of("^b$", "^a$", "^b$")))
                        .build())
                .autoPermit(false)
                .build();

        // WHEN: they are saved and read back
        indexSettingsRepository.save(settings);
        final IndexSettings reloaded =
                indexSettingsRepository.findFirstByOrderByCreatedAtAsc().orElseThrow();

        // THEN: the duplicate is gone, and the two distinct patterns come back in sorted order
        assertThat(reloaded.getPing().getDenyList(), contains("^a$", "^b$"));
    }
}
