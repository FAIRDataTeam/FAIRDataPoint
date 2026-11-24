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

    // this is the old regex pattern from IndexSettingsPing <=1.23.2
    static final String INVALID_HOSTNAME = "^(http|https)://localhost(:[0-9]+){0,1}.*$";

    static final String VALID_HOSTNAME = "localhost";

    /**
     * Returns an IndexSettings instance with the specified test settings
     */
    static IndexSettings createIndexSettings() {
        final boolean autoPermit = false;
        final List<String> deniedHosts = List.of(VALID_HOSTNAME);
        final Duration rateLimitDuration = Duration.ofMinutes(17);
        final int rateLimitHits = 666;
        final Duration rateLimitWait = Duration.ofHours(16);
        final Duration timeoutDuration = Duration.ofSeconds(55);
        final Duration validDuration = Duration.ofDays(5);

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
}
