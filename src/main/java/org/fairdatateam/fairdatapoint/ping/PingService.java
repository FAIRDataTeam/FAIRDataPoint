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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
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

    private final ThreadPoolTaskScheduler scheduler;

    /**
     * Constructor (autowired)
     */
    public PingService(
            InstanceProperties instanceProperties,
            PingProperties pingProperties,
            RestClient.Builder restClientBuilder,
            SettingsService settingsService,
            ThreadPoolTaskScheduler scheduler
    ) {
        this.restClient = restClientBuilder.build();
        this.instanceProperties = instanceProperties;
        this.pingProperties = pingProperties;
        this.settingsService = settingsService;
        this.scheduler = scheduler;
    }

    /**
     * Schedules a single registration ping for each FDP index specified in the <code>endpoints</code> property,
     * as soon as the application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    void schedulePings() {
        final Settings settings = settingsService.getOrDefaults();
        // handle possibly missing settings
        final boolean settingsPingEnabled = settings != null
                && settings.getPing() != null
                && settings.getPing().isEnabled();
        // only continue if ping is enabled in both settings and config properties
        // (config determines if ping can be enabled via settings)
        final boolean configPingEnabled = pingProperties.isEnabled();
        if (!settingsPingEnabled || !configPingEnabled) {
            log.info("Index ping is disabled (settings: {}, config: {})", settingsPingEnabled, configPingEnabled);
            return;
        }
        // combine endpoints from both settings and config properties
        final List<String> indexUrls = Stream.concat(
                pingProperties.getEndpoints().stream(),
                settings.getPing().getEndpoints().stream()
        ).map(String::trim).distinct().toList();
        for (String indexUrl : indexUrls) {
            final Instant startTime = Instant.now().plus(pingProperties.getInitialDelay());
            scheduler.schedule(() -> executePing(indexUrl, 1), startTime);
        }
    }

    /**
     * POSTs the <code>pingContent</code> as JSON to the specified FDP-index endpoint.
     * Reschedules itself if the ping fails, up to specified maximum number of attempts.
     */
    @Async
    void executePing(String endpoint, int attempt) {
        if (attempt > pingProperties.getMaxAttempts()) {
            log.warn("Index registration failed {} times. Restart app to try again.", pingProperties.getMaxAttempts());
            return;
        }
        try {
            log.info("Pinging {}", endpoint);
            // blocking request, raises HttpClientErrorException for 4xx or HttpServerErrorException for 5xx response
            restClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("clientUrl", instanceProperties.getClientUrl()))
                    .retrieve()
                    .toEntity(String.class);
        }
        catch (Exception exception) {
            log.warn("Failed to ping {}: {}", endpoint, exception.getMessage());
        }
    }
}
