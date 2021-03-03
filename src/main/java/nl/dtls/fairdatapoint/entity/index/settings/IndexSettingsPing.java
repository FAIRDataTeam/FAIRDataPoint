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
package nl.dtls.fairdatapoint.entity.index.settings;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class IndexSettingsPing {
    @NotNull
    private Duration validDuration;

    @NotNull
    private Duration rateLimitDuration;

    @NotNull
    private Integer rateLimitHits;

    @NotNull
    private List<String> denyList;

    public static IndexSettingsPing getDefault() {
        IndexSettingsPing ping = new IndexSettingsPing();
        ping.setValidDuration(Duration.ofDays(7));
        ping.setRateLimitDuration(Duration.ofHours(6));
        ping.setRateLimitHits(10);
        ping.setDenyList(Collections.singletonList("^(http|https)://localhost(:[0-9]+){0,1}.*$"));
        return ping;
    }
}
