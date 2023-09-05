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
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryPermit;
import nl.dtls.fairdatapoint.utils.TestIndexEntryFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("GET /index/entries/all")
public class List_All_GET extends WebIntegrationTest {

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    private final ParameterizedTypeReference<List<IndexEntryDTO>> responseType = new ParameterizedTypeReference<>() {
    };

    private URI url() {
        return URI.create("/index/entries/all");
    }

    private URI urlWithPermit(String permitQuery) {
        return UriComponentsBuilder.fromUri(url())
                .queryParam("permit", permitQuery)
                .build().toUri();
    }

    @Test
    @DisplayName("HTTP 200: list empty")
    public void res200_listEmpty() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN:
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("There are no entries in the response", result.getBody().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 200: list few")
    public void res200_listFew() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesFew();
        indexEntryRepository.saveAll(entries);
        int size = 9;

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(entries.size())));
        for (int i = 0; i < entries.size(); i++) {
            assertThat("Entry matches: " + entries.get(i).getClientUrl(), result.getBody().get(i).getClientUrl(),
                    is(equalTo(entries.get(i).getClientUrl())));
        }
    }

    @Test
    @DisplayName("HTTP 200: list many")
    public void res200_listMany() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesN(300);
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(entries.size())));
        for (int i = 0; i < entries.size(); i++) {
            assertThat("Entry matches: " + entries.get(i).getClientUrl(), result.getBody().get(i).getClientUrl(),
                    is(equalTo(entries.get(i).getClientUrl())));
        }
    }

    @Test
    @DisplayName("HTTP 200: list accepted")
    public void res200_listAccepted() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("accepted"))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(),
                is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(),
                is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(1)));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(notNullValue()));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(equalTo(IndexEntryPermit.ACCEPTED)));
    }

    @Test
    @DisplayName("HTTP 200: list rejected (non-admin)")
    public void res200_listRejected_nonAdmin() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("rejected"))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(),
                is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(),
                is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(1)));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(notNullValue()));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(equalTo(IndexEntryPermit.ACCEPTED)));
    }

    @Test
    @DisplayName("HTTP 200: list rejected (admin)")
    public void res200_listRejected_admin() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("rejected"))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(),
                is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(),
                is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(1)));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(notNullValue()));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(equalTo(IndexEntryPermit.REJECTED)));
    }

    @Test
    @DisplayName("HTTP 200: list pending (admin)")
    public void res200_listPending_admin() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("pending"))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(),
                is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(),
                is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(1)));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(notNullValue()));
        assertThat("Response body is not null", result.getBody().get(0).getPermit(),
                is(equalTo(IndexEntryPermit.PENDING)));
    }

    @Test
    @DisplayName("HTTP 200: list combined (admin)")
    public void res200_listCombined_admin() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("rejected,pending"))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(),
                is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(),
                is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(2)));
    }

    @Test
    @DisplayName("HTTP 200: list all (admin)")
    public void res200_listAll_admin() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        List<IndexEntry> entries = TestIndexEntryFixtures.entriesPermits();
        indexEntryRepository.saveAll(entries);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(urlWithPermit("all"))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<List<IndexEntryDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(),
                is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(),
                is(notNullValue()));
        assertThat("Correct number of entries is in the response", result.getBody().size(),
                is(equalTo(3)));
    }
}
