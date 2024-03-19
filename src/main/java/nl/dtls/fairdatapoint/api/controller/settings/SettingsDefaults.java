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
package nl.dtls.fairdatapoint.api.controller.settings;

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.config.properties.PingProperties;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.entity.settings.SettingsMetric;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SettingsDefaults {

    private final PingProperties pingProperties;

    public Settings getDefaults() {
        final Settings settings = defaultSettings();
        settings.setMetrics(defaultMetrics(settings));
        return settings;
    }

    public Settings defaultSettings() {
        return Settings.builder()
                .uuid(KnownUUIDs.SETTINGS_UUID)
                .appTitle(null)
                .appSubtitle(null)
                .pingEnabled(pingProperties.isEnabled())
                .pingEndpoints(List.of())
                .autocompleteSearchNamespace(true)
                .autocompleteSources(List.of())
                .metrics(List.of())
                .searchFilters(List.of())
                .build();
    }

    public List<SettingsMetric> defaultMetrics(Settings settings) {
        return List.of(
                SettingsMetric.builder()
                        .uuid(UUID.randomUUID())
                        .metricUri("https://purl.org/fair-metrics/FM_F1A")
                        .resourceUri("https://www.ietf.org/rfc/rfc3986.txt")
                        .settings(settings)
                        .orderPriority(1)
                        .build(),
                SettingsMetric.builder()
                        .uuid(UUID.randomUUID())
                        .metricUri("https://purl.org/fair-metrics/FM_A1.1")
                        .resourceUri("https://www.wikidata.org/wiki/Q8777")
                        .settings(settings)
                        .orderPriority(2)
                        .build()
        );
    }
}
