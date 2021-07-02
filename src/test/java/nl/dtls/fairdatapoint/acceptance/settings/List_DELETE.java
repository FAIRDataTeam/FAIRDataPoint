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
package nl.dtls.fairdatapoint.acceptance.settings;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.settings.SettingsDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.SettingsRepository;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.entity.settings.SettingsMetricsEntry;
import nl.dtls.fairdatapoint.entity.settings.SettingsPing;
import nl.dtls.fairdatapoint.service.settings.SettingsCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("DELETE /settings")
public class List_DELETE extends WebIntegrationTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private SettingsCache settingsCache;

    private final ParameterizedTypeReference<SettingsDTO> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/settings");
    }

    private Settings customSettings() {
        return Settings.builder()
                .metadataMetrics(
                        List.of(new SettingsMetricsEntry(
                                "http://example.com/metric",
                                "http://example.com/resource"
                        ))
                )
                .ping(SettingsPing.builder()
                        .enabled(false)
                        .endpoints(List.of(
                                "https://home.fairdatapoint.org",
                                "https://example.com/index"
                        ))
                        .build()
                )
                .build();
    }

    @Test
    @DisplayName("HTTP 200: default settings")
    public void res200_defaultSettings() {
        // GIVEN: prepare data
        Settings defaultSettings = Settings.getDefault();
        settingsRepository.deleteAll();
        settingsCache.updateCachedSettings();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .delete(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<SettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Response contains default metrics", Objects.requireNonNull(result.getBody()).getMetadataMetrics(), is(equalTo(defaultSettings.getMetadataMetrics())));
        assertThat("Response contains default ping enabled", Objects.requireNonNull(result.getBody()).getPing().getEnabled(), is(equalTo(defaultSettings.getPing().isEnabled())));
        assertThat("Response contains default ping endpoints", Objects.requireNonNull(result.getBody()).getPing().getEndpoints(), is(equalTo(defaultSettings.getPing().getEndpoints())));
    }

    @Test
    @DisplayName("HTTP 200: custom settings")
    public void res200_customSettings() {
        // GIVEN: prepare data
        Settings defaultSettings = Settings.getDefault();
        Settings customSettings = customSettings();
        settingsRepository.deleteAll();
        settingsRepository.insert(customSettings);
        settingsCache.updateCachedSettings();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .delete(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<SettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("No settings are created", settingsRepository.findAll().size(), is(equalTo(1)));
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Response contains default metrics", Objects.requireNonNull(result.getBody()).getMetadataMetrics(), is(equalTo(defaultSettings.getMetadataMetrics())));
        assertThat("Response contains default ping enabled", Objects.requireNonNull(result.getBody()).getPing().getEnabled(), is(equalTo(defaultSettings.getPing().isEnabled())));
        assertThat("Response contains default ping endpoints", Objects.requireNonNull(result.getBody()).getPing().getEndpoints(), is(equalTo(defaultSettings.getPing().getEndpoints())));
    }

    @Test
    @DisplayName("HTTP 403: no token")
    public void res403_noToken() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .delete(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<SettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It is forbidden without auth", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 403: not admin")
    public void res403_notAdmin() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .delete(url())
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<SettingsDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("It is forbidden for non-admin users", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @AfterEach
    public void teardown() {
        settingsRepository.deleteAll();
        settingsCache.updateCachedSettings();
    }
}
