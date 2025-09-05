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
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryInfoDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.utils.TestIndexEntryFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("GET /index/entries/info")
public class List_Info_GET extends WebIntegrationTest {

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    private final ParameterizedTypeReference<IndexEntryInfoDTO> responseType = new ParameterizedTypeReference<>() {
    };

    private URI url() {
        return URI.create("/index/entries/info");
    }

    private URI urlWithPermit(String permitQuery) {
        return UriComponentsBuilder.fromUri(url())
                .queryParam("permit", permitQuery)
                .build().toUri();
    }

    @Test
    @DisplayName("HTTP 200: list default")
    public void res200_listMany() {
        // GIVEN: Prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // AND: Prepare expectation
        IndexEntryInfoDTO expDto = new IndexEntryInfoDTO(new HashMap<>() {{
            put("ALL", 6L);
            put("ACTIVE", 1L);
            put("INACTIVE", 2L);
            put("UNKNOWN", 1L);
            put("INVALID", 1L);
            put("UNREACHABLE", 1L);
        }});

        // WHEN:
        ResponseEntity<IndexEntryInfoDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().getEntriesCount(),
                is(equalTo(expDto.getEntriesCount())));
    }

    @Test
    @DisplayName("HTTP 200: list rejected (admin)")
    public void res200_listRejected() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // GIVEN: Prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("rejected"))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // AND: Prepare expectation
        IndexEntryInfoDTO expDto = new IndexEntryInfoDTO(new HashMap<>() {{
            put("ALL", 1L);
            put("ACTIVE", 0L);
            put("INACTIVE", 0L);
            put("UNKNOWN", 0L);
            put("INVALID", 1L);
            put("UNREACHABLE", 0L);
        }});

        // WHEN:
        ResponseEntity<IndexEntryInfoDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().getEntriesCount(),
                is(equalTo(expDto.getEntriesCount())));
    }

    @Test
    @DisplayName("HTTP 200: list combined (admin)")
    public void res200_listCombined() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // GIVEN: Prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("rejected,pending,accepted"))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // AND: Prepare expectation
        IndexEntryInfoDTO expDto = new IndexEntryInfoDTO(new HashMap<>() {{
            put("ALL", 3L);
            put("ACTIVE", 0L);
            put("INACTIVE", 0L);
            put("UNKNOWN", 0L);
            put("INVALID", 3L);
            put("UNREACHABLE", 0L);
        }});

        // WHEN:
        ResponseEntity<IndexEntryInfoDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().getEntriesCount(),
                is(equalTo(expDto.getEntriesCount())));
    }
}
