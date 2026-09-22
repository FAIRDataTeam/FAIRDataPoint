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
package org.fairdatateam.fairdatapoint.acceptance.index.entry;

import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntry;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryRepository;
import org.fairdatateam.fairdatapoint.index.entry.dto.IndexEntryDetailDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@DisplayName("GET /index/entries/{uuid}")
public class Detail_GET extends WebIntegrationTest {

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    private URI url(UUID uuid) {
        return URI.create("/index/entries/" + uuid);
    }

    @Test
    @DisplayName("HTTP 200: never-retrieved entry has no metadata yet")
    public void res200_neverRetrieved() {
        // GIVEN: an entry stored the way an incoming ping stores one, before any retrieval
        final IndexEntry entry = IndexEntry.builder()
                .uuid(UUID.randomUUID())
                .clientUrl("https://never-retrieved.example.com")
                .registrationTime(Instant.now())
                .modificationTime(Instant.now())
                .build();
        indexEntryRepository.save(entry);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url(entry.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<IndexEntryDetailDTO> result = client.exchange(request, IndexEntryDetailDTO.class);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("There is no metadata yet", result.getBody().getCurrentMetadata(), is(nullValue()));
        assertThat("There is no retrieval time yet", result.getBody().getLastRetrievalTime(), is(nullValue()));
    }
}
