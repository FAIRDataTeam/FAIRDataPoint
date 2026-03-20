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
package nl.dtls.fairdatapoint.acceptance.index.admin;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.EventRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexSettingsRepository;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.entity.index.event.EventType;
import nl.dtls.fairdatapoint.utils.TestIndexEntryFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("POST /index/admin/trigger")
public class List_Trigger_POST extends WebIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    @Autowired
    private IndexSettingsRepository indexSettingsRepository;

    private final ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
    };

    private URI url() {
        return URI.create("/index/admin/trigger");
    }

    private PingDTO reqDTO(String clientUrl) {
        PingDTO dto = new PingDTO();
        dto.setClientUrl(clientUrl);
        return dto;
    }

    @Test
    @DisplayName("HTTP 403: no token")
    public void res403_noToken() {
        // GIVEN: prepare data
        String clientUrl = "http://example.com";
        PingDTO reqDTO = reqDTO(clientUrl);

        // AND: prepare request
        RequestEntity<PingDTO> request = RequestEntity
                .post(url())
                .body(reqDTO);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN: 
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 403: incorrect token")
    public void res403_incorrectToken() {
        // GIVEN: prepare data
        String clientUrl = "http://example.com";
        PingDTO reqDTO = reqDTO(clientUrl);

        // AND: prepare request
        RequestEntity<PingDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, "mySecretToken")
                .body(reqDTO);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN: 
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 403: non-admin token")
    public void res403_nonAdminToken() {
        // GIVEN: prepare data
        String clientUrl = "http://example.com";
        PingDTO reqDTO = reqDTO(clientUrl);

        // AND: prepare request
        RequestEntity<PingDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .body(reqDTO);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN: 
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 400: malformed URL")
    public void res400_malformedUrl() {
        // GIVEN: prepare data
        String clientUrl = "http://example.com";
        PingDTO reqDTO = reqDTO(clientUrl);
        reqDTO.setClientUrl("thisIsNot/Url");

        // AND: prepare request
        RequestEntity<PingDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTO);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
    }

    @Test
    @DisplayName("HTTP 204: trigger one")
    public void res204_triggerOne() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        indexSettingsRepository.deleteAll();
        IndexEntry entry = TestIndexEntryFixtures.entryExample();
        indexEntryRepository.save(entry);
        PingDTO reqDTO = reqDTO(entry.getClientUrl());

        // AND: prepare request
        RequestEntity<PingDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTO);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, responseType);
        List<Event> events = eventRepository.getAllByType(EventType.AdminTrigger);

        // THEN: 
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
        assertThat("One AdminTrigger event is created", events.size(), is(equalTo(1)));
        assertThat("Records correct client URL", events.get(0).getAdminTrigger().getClientUrl(), is(equalTo(entry.getClientUrl())));
    }

    @Test
    @DisplayName("HTTP 404: trigger non-existing")
    public void res404_triggerOne() {
        // GIVEN: prepare data
        indexEntryRepository.deleteAll();
        indexSettingsRepository.deleteAll();
        IndexEntry entry = TestIndexEntryFixtures.entryExample();
        PingDTO reqDTO = reqDTO(entry.getClientUrl());

        // AND: prepare request
        RequestEntity<PingDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTO);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, responseType);
        List<Event> events = eventRepository.getAllByType(EventType.AdminTrigger);

        // THEN: 
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
        assertThat("One AdminTrigger event is created", events.size(), is(equalTo(1)));
        assertThat("Records correct client URL", events.get(0).getAdminTrigger().getClientUrl(), is(equalTo(entry.getClientUrl())));
    }
}
