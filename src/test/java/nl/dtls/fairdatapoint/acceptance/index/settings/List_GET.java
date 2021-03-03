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

@DisplayName("GET /index/settings")
public class List_GET extends WebIntegrationTest {

    @Autowired
    private IndexSettingsRepository indexSettingsRepository;

    private final ParameterizedTypeReference<IndexSettingsDTO> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/index/settings");
    }

    private IndexSettings customSettings() {
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

    @Test
    @DisplayName("HTTP 200: default settings")
    public void res200_defaultSettings() {
        // GIVEN: prepare data
        IndexSettings settings = IndexSettings.getDefault();
        indexSettingsRepository.deleteAll();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("No settings are created", indexSettingsRepository.findAll().size(), is(equalTo(0)));
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Response contains default valid duration", Objects.requireNonNull(result.getBody()).getPing().getValidDuration(), is(equalTo(settings.getPing().getValidDuration().toString())));
        assertThat("Response contains default rate limit duration", Objects.requireNonNull(result.getBody()).getPing().getRateLimitDuration(), is(equalTo(settings.getPing().getRateLimitDuration().toString())));
        assertThat("Response contains default rate limit hits", Objects.requireNonNull(result.getBody()).getPing().getRateLimitHits(), is(equalTo(settings.getPing().getRateLimitHits())));
        assertThat("Response contains default deny list", Objects.requireNonNull(result.getBody()).getPing().getDenyList(), is(equalTo(settings.getPing().getDenyList())));
        assertThat("Response contains default timeout", Objects.requireNonNull(result.getBody()).getRetrieval().getTimeout(), is(equalTo(settings.getRetrieval().getTimeout().toString())));
        assertThat("Response contains default rate limit wait", Objects.requireNonNull(result.getBody()).getRetrieval().getRateLimitWait(), is(equalTo(settings.getRetrieval().getRateLimitWait().toString())));
        assertThat("Response indicated default settings", Objects.requireNonNull(result.getBody()).getIsDefault(), is(Boolean.TRUE));
    }

    @Test
    @DisplayName("HTTP 200: custom settings")
    public void res200_customSettings() {
        // GIVEN: prepare data
        IndexSettings settings = customSettings();
        indexSettingsRepository.deleteAll();
        indexSettingsRepository.insert(settings);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("No settings are created", indexSettingsRepository.findAll().size(), is(equalTo(1)));
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Response contains custom valid duration", Objects.requireNonNull(result.getBody()).getPing().getValidDuration(), is(equalTo(settings.getPing().getValidDuration().toString())));
        assertThat("Response contains custom rate limit duration", Objects.requireNonNull(result.getBody()).getPing().getRateLimitDuration(), is(equalTo(settings.getPing().getRateLimitDuration().toString())));
        assertThat("Response contains custom rate limit hits", Objects.requireNonNull(result.getBody()).getPing().getRateLimitHits(), is(equalTo(settings.getPing().getRateLimitHits())));
        assertThat("Response contains custom deny list", Objects.requireNonNull(result.getBody()).getPing().getDenyList(), is(equalTo(settings.getPing().getDenyList())));
        assertThat("Response contains custom timeout", Objects.requireNonNull(result.getBody()).getRetrieval().getTimeout(), is(equalTo(settings.getRetrieval().getTimeout().toString())));
        assertThat("Response contains custom rate limit wait", Objects.requireNonNull(result.getBody()).getRetrieval().getRateLimitWait(), is(equalTo(settings.getRetrieval().getRateLimitWait().toString())));
        assertThat("Response indicated non-default settings", Objects.requireNonNull(result.getBody()).getIsDefault(), is(Boolean.FALSE));
    }

    @Test
    @DisplayName("HTTP 403: no token")
    public void res403_noToken() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It is forbidden without auth", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 403: not admin")
    public void res403_notAdmin() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<IndexSettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It is forbidden for non-admin users", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }
}
