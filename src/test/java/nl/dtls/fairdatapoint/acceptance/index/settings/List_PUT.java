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
package nl.dtls.fairdatapoint.acceptance.index.settings;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsDTO;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsPingDTO;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsRetrievalDTO;
import nl.dtls.fairdatapoint.api.dto.index.settings.IndexSettingsUpdateDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexSettingsRepository;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettings;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettingsPing;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettingsRetrieval;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("PUT /index/settings")
public class List_PUT extends WebIntegrationTest {

    @Autowired
    private IndexSettingsRepository indexSettingsRepository;

    private final ParameterizedTypeReference<IndexSettingsDTO> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/index/settings");
    }

    private IndexSettings customSettings1() {
        return new IndexSettings()
                .toBuilder()
                .ping(
                        new IndexSettingsPing()
                                .toBuilder()
                                .denyList(Collections.singletonList("http://localhost.*$"))
                                .rateLimitDuration(Duration.ofMinutes(17))
                                .validDuration(Duration.ofDays(5))
                                .rateLimitHits(666)
                                .build()
                )
                .retrieval(
                        new IndexSettingsRetrieval()
                                .toBuilder()
                                .rateLimitWait(Duration.ofHours(16))
                                .timeout(Duration.ofSeconds(55))
                                .build()
                )
                .build();
    }

    private IndexSettings customSettings2() {
        IndexSettings settings = customSettings1();
        settings.getPing().setValidDuration(Duration.ofDays(14));
        settings.getRetrieval().setTimeout(Duration.ofMinutes(2));
        return settings;
    }

    private IndexSettingsUpdateDTO customSettingsUpdateDTO() {
        IndexSettings customSettings = customSettings1();
        IndexSettingsUpdateDTO dto = new IndexSettingsUpdateDTO();
        IndexSettingsPingDTO pingDTO = new IndexSettingsPingDTO();
        pingDTO.setDenyList(customSettings.getPing().getDenyList());
        pingDTO.setRateLimitDuration(customSettings.getPing().getRateLimitDuration().toString());
        pingDTO.setValidDuration(customSettings.getPing().getValidDuration().toString());
        pingDTO.setRateLimitHits(customSettings.getPing().getRateLimitHits());
        dto.setPing(pingDTO);
        IndexSettingsRetrievalDTO retrievalDTO = new IndexSettingsRetrievalDTO();
        retrievalDTO.setRateLimitWait(customSettings.getRetrieval().getRateLimitWait().toString());
        retrievalDTO.setTimeout(customSettings.getRetrieval().getTimeout().toString());
        dto.setRetrieval(retrievalDTO);
        return dto;
    }

    private IndexSettingsUpdateDTO invalidUpdateDTO1() {
        IndexSettingsUpdateDTO dto = customSettingsUpdateDTO();
        dto.getPing().setValidDuration("666");
        return dto;
    }

    private IndexSettingsUpdateDTO invalidUpdateDTO2() {
        IndexSettingsUpdateDTO dto = customSettingsUpdateDTO();
        dto.getPing().setDenyList(null);
        return dto;
    }

    @Test
    @DisplayName("HTTP 200: update settings from defaults")
    public void res200_updateSettingsFromDefaults() {
        // GIVEN: prepare data
        IndexSettings settings = customSettings1();
        indexSettingsRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(customSettingsUpdateDTO());

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("Settings are created", indexSettingsRepository.findAll().size(), is(equalTo(1)));
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Response contains default valid duration", Objects.requireNonNull(result.getBody()).getPing().getValidDuration(), is(equalTo(settings.getPing().getValidDuration().toString())));
        assertThat("Response contains default rate limit duration", Objects.requireNonNull(result.getBody()).getPing().getRateLimitDuration(), is(equalTo(settings.getPing().getRateLimitDuration().toString())));
        assertThat("Response contains default rate limit hits", Objects.requireNonNull(result.getBody()).getPing().getRateLimitHits(), is(equalTo(settings.getPing().getRateLimitHits())));
        assertThat("Response contains default deny list", Objects.requireNonNull(result.getBody()).getPing().getDenyList(), is(equalTo(settings.getPing().getDenyList())));
        assertThat("Response contains default timeout", Objects.requireNonNull(result.getBody()).getRetrieval().getTimeout(), is(equalTo(settings.getRetrieval().getTimeout().toString())));
        assertThat("Response contains default rate limit wait", Objects.requireNonNull(result.getBody()).getRetrieval().getRateLimitWait(), is(equalTo(settings.getRetrieval().getRateLimitWait().toString())));
    }

    @Test
    @DisplayName("HTTP 200: update settings from custom")
    public void res200_updateSettingsFromCustom() {
        // GIVEN: prepare data
        IndexSettingsUpdateDTO reqDTO = customSettingsUpdateDTO();
        IndexSettings settings = customSettings1();
        indexSettingsRepository.deleteAll();
        indexSettingsRepository.insert(customSettings2());

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTO);

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("Settings are created", indexSettingsRepository.findAll().size(), is(equalTo(1)));
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Response contains default valid duration", Objects.requireNonNull(result.getBody()).getPing().getValidDuration(), is(equalTo(settings.getPing().getValidDuration().toString())));
        assertThat("Response contains default rate limit duration", Objects.requireNonNull(result.getBody()).getPing().getRateLimitDuration(), is(equalTo(settings.getPing().getRateLimitDuration().toString())));
        assertThat("Response contains default rate limit hits", Objects.requireNonNull(result.getBody()).getPing().getRateLimitHits(), is(equalTo(settings.getPing().getRateLimitHits())));
        assertThat("Response contains default deny list", Objects.requireNonNull(result.getBody()).getPing().getDenyList(), is(equalTo(settings.getPing().getDenyList())));
        assertThat("Response contains default timeout", Objects.requireNonNull(result.getBody()).getRetrieval().getTimeout(), is(equalTo(settings.getRetrieval().getTimeout().toString())));
        assertThat("Response contains default rate limit wait", Objects.requireNonNull(result.getBody()).getRetrieval().getRateLimitWait(), is(equalTo(settings.getRetrieval().getRateLimitWait().toString())));
    }

    @Test
    @DisplayName("HTTP 400: invalid duration format")
    public void res400_invalidDurationFormat() {
        // GIVEN: prepare data
        IndexSettingsUpdateDTO reqDTO = invalidUpdateDTO1();
        indexSettingsRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTO);

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It indicates bad request", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat("No settings are created", indexSettingsRepository.findAll().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 400: invalid list")
    public void res400_invalidList() {
        // GIVEN: prepare data
        IndexSettingsUpdateDTO reqDTO = invalidUpdateDTO2();
        indexSettingsRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTO);

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It indicates bad request", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat("No settings are created", indexSettingsRepository.findAll().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 403: no token")
    public void res403_noToken() {
        // GIVEN: prepare data
        IndexSettingsUpdateDTO reqDTO = customSettingsUpdateDTO();
        indexSettingsRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .put(url())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTO);

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It is forbidden without auth", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
        assertThat("No settings are created", indexSettingsRepository.findAll().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 403: not admin")
    public void res403_notAdmin() {
        // GIVEN: prepare data
        IndexSettingsUpdateDTO reqDTO = customSettingsUpdateDTO();
        indexSettingsRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTO);

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It is forbidden for non-admin users", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
        assertThat("No settings are created", indexSettingsRepository.findAll().size(), is(equalTo(0)));
    }
}
