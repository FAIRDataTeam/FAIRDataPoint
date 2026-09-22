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
package org.fairdatateam.fairdatapoint.index.settings;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.fairdatateam.fairdatapoint.common.persistence.DurationStringConverter;

import java.time.Duration;

/**
 * How the Index retrieves metadata from the entries it knows: how long it waits before asking the
 * same entry again, and how long it waits for an answer. Stored in the columns of the settings
 * row itself.
 */
@Embeddable
@NoArgsConstructor
// Package-private for the same reason as in IndexSettings: @NoArgsConstructor stops Lombok from
// synthesizing the all-args constructor @Builder needs, so it is declared here explicitly.
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class IndexSettingsRetrieval {

    private static final int DEFAULT_WAIT_MIN = 10;
    private static final int DEFAULT_TIMEOUT_MIN = 1;

    @Convert(converter = DurationStringConverter.class)
    private Duration rateLimitWait;

    @Convert(converter = DurationStringConverter.class)
    private Duration timeout;

    public static IndexSettingsRetrieval getDefault() {
        final IndexSettingsRetrieval retrieval = new IndexSettingsRetrieval();
        retrieval.setRateLimitWait(Duration.ofMinutes(DEFAULT_WAIT_MIN));
        retrieval.setTimeout(Duration.ofMinutes(DEFAULT_TIMEOUT_MIN));
        return retrieval;
    }
}
