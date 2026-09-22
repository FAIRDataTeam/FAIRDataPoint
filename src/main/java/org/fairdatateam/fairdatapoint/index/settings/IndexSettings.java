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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The single row of settings the Index runs on. There is at most one: the service reads the
 * oldest row and falls back to in-memory defaults when the table is empty, and never writes a row
 * of its own accord.
 */
@Entity
@Table(name = "index_settings")
@NoArgsConstructor
// Package-private for the same reason as in User: Lombok's @Builder needs an all-args
// constructor to build from, and @NoArgsConstructor stops it from being synthesized implicitly.
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@Builder(toBuilder = true)
public class IndexSettings {

    // Assigned by Hibernate when the row is first inserted: settings are anonymous, nothing ever
    // refers to them by identifier, and the caller that saves them (the service, or a test) has
    // no identifier to offer.
    @Id
    @GeneratedValue
    private UUID uuid;

    // The two groups of settings live in the columns of this very row, so they are embeddables
    // rather than tables of their own. The column names are given here instead of in the
    // embeddables, which only know the names of their own fields.
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
                name = "rateLimitWait", column = @Column(name = "retrieval_rate_limit_wait")
        ),
        @AttributeOverride(name = "timeout", column = @Column(name = "retrieval_timeout"))
    })
    private IndexSettingsRetrieval retrieval;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "validDuration", column = @Column(name = "ping_valid_duration")),
        @AttributeOverride(
                name = "rateLimitDuration", column = @Column(name = "ping_rate_limit_duration")
        ),
        @AttributeOverride(name = "rateLimitHits", column = @Column(name = "ping_rate_limit_hits"))
    })
    private IndexSettingsPing ping;

    @Column(name = "auto_permit")
    private Boolean autoPermit;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    // Deliberately compares the settings themselves and neither the identifier nor the
    // timestamps: the API reports whether the current settings are still the default ones, and a
    // stored row that happens to hold the defaults counts as default.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IndexSettings that = (IndexSettings) o;
        return Objects.equals(retrieval, that.retrieval) && Objects.equals(ping, that.ping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retrieval, ping);
    }
}
