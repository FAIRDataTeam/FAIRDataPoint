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

import lombok.extern.log4j.Log4j2;
import org.fairdatateam.fairdatapoint.common.config.InstanceProperties;
import org.fairdatateam.fairdatapoint.settings.Settings;
import org.fairdatateam.fairdatapoint.settings.SettingsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Log4j2
@Service
@ConditionalOnProperty(name = "ping.enabled", havingValue = "true", matchIfMissing = true)
public class PingService {

    private final InstanceProperties instanceProperties;

    private final PingProperties pingProperties;

    private final RestClient restClient;

    private final SettingsService settingsService;

    /**
     * Constructor (autowired)
     */
    public PingService(
            InstanceProperties instanceProperties,
            PingProperties pingProperties,
            RestClient.Builder restClientBuilder,
            SettingsService settingsService
    ) {
        this.restClient = restClientBuilder.build();
        this.instanceProperties = instanceProperties;
        this.pingProperties = pingProperties;
        this.settingsService = settingsService;
    }

    @Scheduled(
            initialDelayString = "${ping.initDelay:#{10*1000}}",
            fixedRateString = "${ping.interval:P7D}"
    )
    public void ping() {
        final Settings settings = settingsService.getOrDefaults();
        if (!settings.getPing().isEnabled() || !pingProperties.isEnabled()) {
            return;
        }
        final List<String> endpoints = Stream.concat(
                pingProperties.getEndpoints().stream(),
                settings.getPing().getEndpoints().stream()
        ).distinct().toList();
        for (String endpoint : endpoints) {
            pingEndpoint(
                    endpoint.trim(),
                    Map.of("clientUrl", instanceProperties.getClientUrl())
            );
        }
    }

    @Async
    void pingEndpoint(String endpoint, Map<String, String> pingContent) {
        try {
            log.info("Pinging {}", endpoint);
            restClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(pingContent)
                    .retrieve()
                    .toEntity(String.class);
        }
        catch (Exception exception) {
            log.warn("Failed to ping {}: {}", endpoint, exception.getMessage());
        }
    }
}
