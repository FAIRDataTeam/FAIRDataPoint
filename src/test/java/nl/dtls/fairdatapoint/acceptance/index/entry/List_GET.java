/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.acceptance.index.entry;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.utils.CustomPageImpl;
import nl.dtls.fairdatapoint.utils.TestIndexEntryFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("GET /index/entries")
public class List_GET extends WebIntegrationTest {

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    private final ParameterizedTypeReference<CustomPageImpl<IndexEntryDTO>> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/index/entries");
    }

    private URI urlWithPage(int page) {
        return UriComponentsBuilder.fromUri(url())
                .queryParam("page", page)
                .build().toUri();
    }

    private URI urlWithPageSize(int page, int size) {
        return UriComponentsBuilder.fromUri(url())
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toUri();
    }

    @Test
    @DisplayName("HTTP 200: page empty")
    public void res200_pageEmpty() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<CustomPageImpl<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Current page is the first page", result.getBody().isFirst(), is(Boolean.TRUE));
        assertThat("Current page is the last page", result.getBody().isLast(), is(Boolean.TRUE));
        assertThat("Current page is empty", result.getBody().isEmpty(), is(Boolean.TRUE));
        assertThat("Number of pages is 0", result.getBody().getTotalPages(), is(equalTo(0)));
        assertThat("Number of elements is 0", result.getBody().getTotalElements(), is(equalTo(0L)));
        assertThat("There are no entries in the response", result.getBody().getContent().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 200: out-of-bounds page")
    public void res200_outOfBoundsPage() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPage(7))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN:
        ResponseEntity<CustomPageImpl<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Current page is not the first page", result.getBody().isFirst(), is(Boolean.FALSE));
        assertThat("Current page is the last page", result.getBody().isLast(), is(Boolean.TRUE));
        assertThat("Current page is empty", result.getBody().isEmpty(), is(Boolean.TRUE));
        assertThat("Number of pages is 0", result.getBody().getTotalPages(), is(equalTo(0)));
        assertThat("Number of elements is 0", result.getBody().getTotalElements(), is(equalTo(0L)));
        assertThat("There are no entries in the response", result.getBody().getContent().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 200: page few")
    public void res200_pageFew() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesFew();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<CustomPageImpl<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Current page is the first page", result.getBody().isFirst(), is(Boolean.TRUE));
        assertThat("Current page is the last page", result.getBody().isLast(), is(Boolean.TRUE));
        assertThat("Current page is not empty", result.getBody().isEmpty(), is(Boolean.FALSE));
        assertThat("Number of pages is 1", result.getBody().getTotalPages(), is(equalTo(1)));
        assertThat("Number of elements is correct", result.getBody().getTotalElements(),
                is(equalTo(Integer.toUnsignedLong(entries.size()))));
        assertThat("There is correct number of entries in the response", result.getBody().getContent().size(),
                is(equalTo(entries.size())));
        for (int i = 0; i < entries.size(); i++) {
            assertThat("Entry matches: " + entries.get(i).getClientUrl(),
                    result.getBody().getContent().get(i).getClientUrl(), is(equalTo(entries.get(i).getClientUrl())));
        }
    }

    @Test
    @DisplayName("HTTP 200: page many (middle)")
    public void res200_pageManyMiddle() {
        // GIVEN: prepare data
        long items = 300L;
        int size = 30;
        int page = 3;
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesN(items);
        indexEntryRepository.saveAll(entries);

        // AND (prepare request)
        RequestEntity<?> request = RequestEntity
                .get(urlWithPageSize(page, size))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<CustomPageImpl<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Current page is the first page", result.getBody().isFirst(), is(Boolean.FALSE));
        assertThat("Current page is the last page", result.getBody().isLast(), is(Boolean.FALSE));
        assertThat("Current page is not empty", result.getBody().isEmpty(), is(Boolean.FALSE));
        assertThat("Number of pages is correct", result.getBody().getTotalPages(), is(equalTo(10)));
        assertThat("Number of elements is correct", result.getBody().getTotalElements(), is(equalTo(items)));
        assertThat("There is correct number of entries in the response", result.getBody().getContent().size(),
                is(equalTo(size)));
    }

    @Test
    @DisplayName("HTTP 200: page many (last)")
    public void res200_pageManyLast() {
        // GIVEN (prepare data)
        long items = 666;
        int size = 300;
        int lastSize = 66;
        int page = 2;
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesN(items);
        indexEntryRepository.saveAll(entries);

        // AND (prepare request)
        RequestEntity<?> request = RequestEntity
                .get(urlWithPageSize(page, size))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<CustomPageImpl<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Current page is the first page", result.getBody().isFirst(), is(Boolean.FALSE));
        assertThat("Current page is the last page", result.getBody().isLast(), is(Boolean.TRUE));
        assertThat("Current page is not empty", result.getBody().isEmpty(), is(Boolean.FALSE));
        assertThat("Number of pages is correct", result.getBody().getTotalPages(), is(equalTo(3)));
        assertThat("Number of elements is correct", result.getBody().getTotalElements(), is(equalTo(items)));
        assertThat("There is correct number of entries in the response", result.getBody().getContent().size(),
                is(equalTo(lastSize)));
    }
}
