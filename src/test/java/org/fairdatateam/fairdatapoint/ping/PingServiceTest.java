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
package org.fairdatateam.fairdatapoint.ping;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.fairdatateam.fairdatapoint.Profiles;
import org.fairdatateam.fairdatapoint.common.config.InstanceProperties;
import org.fairdatateam.fairdatapoint.settings.Settings;
import org.fairdatateam.fairdatapoint.settings.SettingsPing;
import org.fairdatateam.fairdatapoint.settings.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(Profiles.TESTING)
@RestClientTest
@ContextConfiguration(classes = PingService.class)
public class PingServiceTest {

    private static final String MOCK_INDEX_URL = "https://fdp-index.example.org";

    private static final String MOCK_CLIENT_URL = "https://fdp.example.org";

    private final JsonMapper jsonMapper = new JsonMapper();

    @Autowired
    private MockRestServiceServer mockRemoteServer;

    @Autowired
    private PingService pingService;

    @MockitoBean
    private InstanceProperties instanceProperties;

    @MockitoBean
    private PingProperties pingProperties;

    @MockitoBean
    private SettingsService settingsService;

    @MockitoBean
    private ThreadPoolTaskScheduler scheduler;

    @BeforeEach
    public void setup() {
        // mock config properties
        when(pingProperties.isEnabled()).thenReturn(true);
        when(pingProperties.getEndpoints()).thenReturn(List.of(MOCK_INDEX_URL));
        when(pingProperties.getInitialDelay()).thenReturn(Duration.ZERO);
        when(instanceProperties.getClientUrl()).thenReturn(MOCK_CLIENT_URL);
        // mock settings
        final SettingsPing mockSettingsPing = mock(SettingsPing.class);
        final Settings mockSettings = mock(Settings.class);
        when(mockSettings.getPing()).thenReturn(mockSettingsPing);
        when(mockSettingsPing.isEnabled()).thenReturn(true);
        when(mockSettingsPing.getEndpoints()).thenReturn(List.of());
        when(settingsService.getOrDefaults()).thenReturn(mockSettings);
        // mock scheduler
        when(scheduler.schedule(any(Runnable.class), any(Instant.class))).thenAnswer(invocation -> {
            // run the runnable argument, i.e. the lambda that calls executePing()
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        });
    }

    @Test
    public void pingIsPosted() throws JsonProcessingException {
        // Configure mock server
        mockRemoteServer
                .expect(requestTo(MOCK_INDEX_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonMapper.writeValueAsString(Map.of("clientUrl", MOCK_CLIENT_URL))))
                .andRespond(withSuccess());

        // Execute ping
        // Note that schedulePings is actually called twice: First due to the ApplicationReadyEvent,
        // then due to the call below. The first call returns directly because settings are not available at that time.
        pingService.schedulePings();

        // Check mock server expectations
        mockRemoteServer.verify();
    }
}
