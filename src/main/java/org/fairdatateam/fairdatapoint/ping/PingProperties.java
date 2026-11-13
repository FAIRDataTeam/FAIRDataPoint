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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ConfigurationProperties(prefix = "ping")
public class PingProperties {
    // defaults
    // beware: @ConfigurationProperties binding is a one-way thing, so these defaults only apply if the property is not
    // defined externally, e.g. in application.yaml or in an environment variable
    private static final int DEFAULT_DELAY_SECONDS = 10;
    private static final boolean DEFAULT_ENABLED = true;
    private static final String DEFAULT_INDEX_URL = "https://home.fairdatapoint.org";
    private static final int DEFAULT_MAX_ATTEMPTS = 5;

    // config properties
    private boolean enabled = DEFAULT_ENABLED;
    private Duration delay = Duration.ofSeconds(DEFAULT_DELAY_SECONDS);
    private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
    private List<String> endpoints = List.of(DEFAULT_INDEX_URL);
}
