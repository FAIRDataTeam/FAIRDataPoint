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
package org.fairdatateam.fairdatapoint.acceptance.index.settings;

import org.fairdatateam.fairdatapoint.index.settings.IndexSettings;
import org.fairdatateam.fairdatapoint.index.settings.IndexSettingsPing;
import org.fairdatateam.fairdatapoint.index.settings.IndexSettingsRetrieval;

import java.time.Duration;
import java.util.List;

class IndexSettingsTestFactory {

    private static final Duration DEFAULT_TIMEOUT_DURATION = Duration.ofSeconds(55);
    private static final Duration DEFAULT_VALID_DURATION = Duration.ofDays(5);
    private static final boolean DEFAULT_AUTO_PERMIT = false;

    /**
     * Returns an IndexSettings instance with the specified test settings
     */
    static IndexSettings createIndexSettings(
            boolean autoPermit, Duration validDuration, Duration timeoutDuration
    ) {
        final List<String> deniedHosts = List.of("localhost");
        final Duration rateLimitDuration = Duration.ofMinutes(17);
        final Duration rateLimitWait = Duration.ofHours(16);
        final int rateLimitHits = 666;
        final IndexSettingsPing indexSettingsPing = IndexSettingsPing.builder()
                .denyList(deniedHosts)
                .rateLimitDuration(rateLimitDuration)
                .validDuration(validDuration)
                .rateLimitHits(rateLimitHits)
                .build();
        final IndexSettingsRetrieval indexSettingsRetrieval = IndexSettingsRetrieval.builder()
                .rateLimitWait(rateLimitWait)
                .timeout(timeoutDuration)
                .build();
        return IndexSettings.builder()
                .ping(indexSettingsPing)
                .retrieval(indexSettingsRetrieval)
                .autoPermit(autoPermit)
                .build();
    }

    static IndexSettings createIndexSettings(boolean autoPermit) {
        return createIndexSettings(autoPermit, DEFAULT_VALID_DURATION, DEFAULT_TIMEOUT_DURATION);
    }

    static IndexSettings createIndexSettings() {
        return createIndexSettings(DEFAULT_AUTO_PERMIT, DEFAULT_VALID_DURATION, DEFAULT_TIMEOUT_DURATION);
    }
}
