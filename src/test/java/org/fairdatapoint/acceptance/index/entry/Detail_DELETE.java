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
package org.fairdatapoint.acceptance.index.entry;

import org.fairdatapoint.WebIntegrationTest;
import org.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import org.fairdatapoint.database.db.repository.IndexEntryRepository;
import org.fairdatapoint.database.db.repository.IndexEventRepository;
import org.fairdatapoint.entity.index.entry.IndexEntry;
import org.fairdatapoint.entity.index.event.IndexEvent;
import org.fairdatapoint.entity.index.event.IndexEventPayload;
import org.fairdatapoint.entity.index.event.payload.AdminTrigger;
import org.fairdatapoint.utils.CustomPageImpl;
import org.fairdatapoint.utils.TestIndexEntryFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("DELETE /index/entries/{uuid}")
public class Detail_DELETE extends WebIntegrationTest {
    @Autowired
    private IndexEntryRepository indexEntryRepository;

    @Autowired
    private IndexEventRepository indexEventRepository;

    private final ParameterizedTypeReference<CustomPageImpl<IndexEntryDTO>> responseType =
            new ParameterizedTypeReference<>() {};

    private URI url(UUID uuid) {
        return URI.create("/index/entries/" + uuid);
    }

    @Test
    @DisplayName("HTTP 204: delete detail with event")
    public void res204_deleteDetailWithEvent() {
        // test for issue #644

        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        IndexEntry entry = TestIndexEntryFixtures.entryExample();
        indexEntryRepository.save(entry);
        indexEventRepository.deleteAll();
        // minimal event related to entry
        // todo: create TestIndexEventFixtures utility if used more often
        IndexEvent event = new IndexEvent(0, new AdminTrigger());
        event.setRelatedTo(entry);
        indexEventRepository.save(event);

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .delete(url(entry.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();

        // WHEN
        ResponseEntity<CustomPageImpl<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code", result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
    }
}
