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

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntity;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.util.List;

@Entity(name = "IndexSettings")
@Table(name = "index_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class IndexSettings extends BaseEntity {

    @NotNull
    @Column(name = "auto_permit", nullable = false)
    private Boolean autoPermit;

    @NotNull
    @Column(name = "retrieval_rate_limit_wait", nullable = false)
    private String retrievalRateLimitWait;

    @NotNull
    @Column(name = "retrieval_timeout", nullable = false)
    private String retrievalTimeout;

    @NotNull
    @Column(name = "ping_valid_duration", nullable = false)
    private String pingValidDuration;

    @NotNull
    @Column(name = "ping_rate_limit_duration", nullable = false)
    private String pingRateLimitDuration;

    @NotNull
    @Column(name = "ping_rate_limit_hits", nullable = false)
    private Integer pingRateLimitHits;

    @NotNull
    @Type(ListArrayType.class)
    @Column(name = "ping_deny_list", columnDefinition="text[]", nullable = false)
    private List<String> pingDenyList;

    public SettingsIndexPing getPing() {
        return SettingsIndexPing.builder()
                .validDuration(Duration.parse(this.getPingValidDuration()))
                .rateLimitDuration(Duration.parse(this.getPingRateLimitDuration()))
                .rateLimitHits(this.getPingRateLimitHits())
                .denyList(this.getPingDenyList())
                .build();
    }

    public void setPing(SettingsIndexPing ping) {
        this.setPingValidDuration(ping.getValidDuration().toString());
        this.setPingRateLimitDuration(ping.getRateLimitDuration().toString());
        this.setPingRateLimitHits(ping.getRateLimitHits());
        this.setPingDenyList(ping.getDenyList());
    }

    public SettingsIndexRetrieval getRetrieval() {
        return SettingsIndexRetrieval.builder()
                .rateLimitWait(Duration.parse(this.getRetrievalRateLimitWait()))
                .timeout(Duration.parse(this.getRetrievalTimeout()))
                .build();
    }

    public void setRetrieval(SettingsIndexRetrieval retrieval) {
        this.setRetrievalRateLimitWait(retrieval.getRateLimitWait().toString());
        this.setRetrievalTimeout(retrieval.getTimeout().toString());
    }
}
