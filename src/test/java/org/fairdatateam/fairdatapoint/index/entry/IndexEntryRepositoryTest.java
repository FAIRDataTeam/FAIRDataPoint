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
package org.fairdatateam.fairdatapoint.index.entry;

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.index.RepositoryMetadata;
import org.fairdatateam.fairdatapoint.index.event.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class IndexEntryRepositoryTest extends BaseIntegrationTest {

    private static final String URL_OLDEST = "https://oldest.example.com";

    private static final String URL_MIDDLE = "https://middle.example.com";

    private static final String URL_NEWEST = "https://newest.example.com";

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    public void setup() {
        // Not deleteAll(): the self-referencing triggered_by foreign key on index_event has no
        // ON DELETE action, so deleting row by row can fail when a parent is removed before its
        // child.
        eventRepository.deleteAllInBatch();
        indexEntryRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAllInBatch();
        indexEntryRepository.deleteAll();
    }

    private IndexEntry newEntry(String clientUrl, Instant registered, RepositoryMetadata metadata) {
        return IndexEntry.builder()
                .uuid(UUID.randomUUID())
                .clientUrl(clientUrl)
                .state(IndexEntryState.Valid)
                .permit(IndexEntryPermit.ACCEPTED)
                .registrationTime(registered)
                .modificationTime(registered)
                .lastRetrievalTime(registered)
                .currentMetadata(metadata)
                .build();
    }

    @Test
    @DisplayName("The harvested metadata of an entry survives a round trip through its JSON column")
    public void currentMetadataRoundTrips() {
        // GIVEN: an entry whose harvested metadata holds properties that are not columns
        final RepositoryMetadata metadata = new RepositoryMetadata(
                RepositoryMetadata.CURRENT_VERSION,
                "https://repository.example.com",
                Map.of("title", "Example", "publisherName", "Example Publisher")
        );
        final IndexEntry saved = indexEntryRepository.save(
                newEntry(URL_NEWEST, Instant.now(), metadata));

        // WHEN: the entry is read back
        final IndexEntry reloaded = indexEntryRepository.findByUuid(saved.getUuid()).orElseThrow();

        // THEN: every part of the metadata came back, the map included
        assertThat(reloaded.getCurrentMetadata().getMetadataVersion(),
                is(equalTo(RepositoryMetadata.CURRENT_VERSION)));
        assertThat(reloaded.getCurrentMetadata().getRepositoryUri(),
                is(equalTo("https://repository.example.com")));
        assertThat(reloaded.getCurrentMetadata().getMetadata(), is(equalTo(metadata.getMetadata())));

        // THEN: the identifier is also accepted in the string form the REST API uses, and a
        // malformed one is simply not found
        assertThat(indexEntryRepository.findByUuid(saved.getUuid().toString()).isPresent(),
                is(equalTo(true)));
        assertThat(indexEntryRepository.findByUuid("not-a-uuid").isPresent(), is(equalTo(false)));
    }

    @Test
    @DisplayName("An entry that has never been retrieved still carries an empty metadata record")
    public void entryWithoutMetadataIsStoredAsEmpty() {
        // GIVEN: an entry built the way the ping handler builds one, without metadata
        final IndexEntry entry = new IndexEntry();
        entry.setUuid(UUID.randomUUID());
        entry.setClientUrl(URL_MIDDLE);
        entry.setRegistrationTime(Instant.now());
        entry.setModificationTime(entry.getRegistrationTime());

        // WHEN: it is stored and read back
        final IndexEntry saved = indexEntryRepository.save(entry);
        final IndexEntry reloaded = indexEntryRepository.findByUuid(saved.getUuid()).orElseThrow();

        // THEN: the NOT NULL metadata column holds an empty record rather than nothing
        assertThat(reloaded.getCurrentMetadata(), is(notNullValue()));
        assertThat(reloaded.getCurrentMetadata().getRepositoryUri(), is(equalTo(null)));
        assertThat(reloaded.getCurrentMetadata().getMetadata().isEmpty(), is(equalTo(true)));

        // THEN: the defaults of a fresh entry are stored as they are declared
        assertThat(reloaded.getState(), is(equalTo(IndexEntryState.Unknown)));
        assertThat(reloaded.getPermit(), is(equalTo(IndexEntryPermit.PENDING)));
    }

    @Test
    @DisplayName("Entries are paged in the order the request asks for, by registration time")
    public void entriesArePagedByRegistrationTime() {
        // GIVEN: three accepted entries registered at three different moments
        final Instant now = Instant.now();
        indexEntryRepository.save(newEntry(URL_NEWEST, now, new RepositoryMetadata()));
        indexEntryRepository.save(
                newEntry(URL_MIDDLE, now.minusSeconds(60), new RepositoryMetadata()));
        indexEntryRepository.save(
                newEntry(URL_OLDEST, now.minusSeconds(120), new RepositoryMetadata()));

        // WHEN: the first page of two is asked for, sorted the way the REST API names the
        // property
        final Page<IndexEntry> page = indexEntryRepository.findAllByPermitIn(
                PageRequest.of(0, 2, Sort.by("registrationTime")),
                List.of(IndexEntryPermit.ACCEPTED)
        );

        // THEN: the page holds the two oldest entries, oldest first
        assertThat(page.getTotalElements(), is(equalTo(3L)));
        assertThat(page.getContent(), hasSize(2));
        assertThat(page.getContent().stream().map(IndexEntry::getClientUrl).toList(),
                contains(URL_OLDEST, URL_MIDDLE));
    }

    @Test
    @DisplayName("Valid entries retrieved since a moment are counted by state and permit")
    public void validEntriesAreCountedByLastRetrieval() {
        // GIVEN: one entry retrieved a minute ago and one retrieved two hours ago
        final Instant now = Instant.now();
        indexEntryRepository.save(newEntry(URL_NEWEST, now, new RepositoryMetadata()));
        indexEntryRepository.save(
                newEntry(URL_OLDEST, now.minusSeconds(7200), new RepositoryMetadata()));

        // WHEN / THEN: only the recent one counts as active
        assertThat(
                indexEntryRepository.countAllByStateEqualsAndLastRetrievalTimeAfterAndPermitIn(
                        IndexEntryState.Valid, now.minusSeconds(600),
                        List.of(IndexEntryPermit.ACCEPTED)),
                is(equalTo(1L))
        );

        // WHEN / THEN: and both of them are counted by permit alone
        assertThat(
                indexEntryRepository.countAllByPermitIn(List.of(IndexEntryPermit.ACCEPTED)),
                is(equalTo(2L))
        );
    }
}
