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
package org.fairdatateam.fairdatapoint.service.index.harvester;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.fairdatateam.fairdatapoint.config.properties.InstanceProperties;
import org.fairdatateam.fairdatapoint.config.properties.PingProperties;
import org.fairdatateam.fairdatapoint.entity.settings.Settings;
import org.fairdatateam.fairdatapoint.service.ping.PingService;
import org.fairdatateam.fairdatapoint.service.settings.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
public class PingServiceTest {

    private static final String MOCK_ENDPOINT = "https://fdp-index.example.org";

    private static final String MOCK_CLIENT_URL = "https://fdp.example.org";

    private final JsonMapper jsonMapper = new JsonMapper();

    private MockRestServiceServer mockRemoteServer;

    private PingService pingService;

    @Mock
    private InstanceProperties instanceProperties;

    @Mock
    private PingProperties pingProperties;

    @Mock
    private SettingsService settingsService;

    @BeforeEach
    public void setup() {
        // Configure a mock remote server and bind it to a local RestClient builder
        RestClient.Builder restClientBuilder = RestClient.builder();
        mockRemoteServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        // Build a local RestClient instance instead of using the singleton from HttpClientConfig, because we have a
        // simple test without spring context, not a @SpringBootTest or @WebMvcTest.
        RestClient restClient = restClientBuilder.build();

        // Create a ping service using the local RestClient
        pingService = new PingService(instanceProperties, pingProperties, restClient, settingsService);

        // Configure mocks
        when(pingProperties.isEnabled()).thenReturn(true);
        when(pingProperties.getEndpoints()).thenReturn(List.of(MOCK_ENDPOINT));
        when(instanceProperties.getClientUrl()).thenReturn(MOCK_CLIENT_URL);
        when(settingsService.getOrDefaults()).thenReturn(Settings.getDefault());

    }

    @Test
    public void pingIsPosted() throws JsonProcessingException {
        // Configure mock server
        mockRemoteServer
                .expect(requestTo(MOCK_ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(jsonMapper.writeValueAsString(Map.of("clientUrl", MOCK_CLIENT_URL))))
                .andRespond(withSuccess());

        // Execute ping
        pingService.ping();

        // Check mock server expectations
        mockRemoteServer.verify();
    }
}
