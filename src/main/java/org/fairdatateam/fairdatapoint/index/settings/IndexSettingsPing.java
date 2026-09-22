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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.*;
import org.fairdatateam.fairdatapoint.common.persistence.DurationStringConverter;
import org.hibernate.annotations.SortNatural;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * How the Index treats incoming pings: how long a ping keeps an entry valid, how often the same
 * client may ping, and which client URLs are refused outright. Stored in the columns of the
 * settings row itself, except for the deny list, which is a table of patterns.
 */
@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class IndexSettingsPing {

    private static final int DEFAULT_VALID_DAYS = 7;
    private static final int DEFAULT_LIMIT_DURATION_HOURS = 6;
    private static final int DEFAULT_LIMIT_HITS = 10;

    @Convert(converter = DurationStringConverter.class)
    private Duration validDuration;

    @Convert(converter = DurationStringConverter.class)
    private Duration rateLimitDuration;

    private Integer rateLimitHits;

    // A set rather than a list, sorted for the same reasons as the entries in Webhook: the
    // collection table's primary key (index_settings_id, pattern) forbids duplicate patterns, so
    // a duplicate pattern in a PUT request would otherwise surface as a constraint violation
    // (HTTP 500) instead of being silently accepted. A sorted set also gives the API a stable
    // order and makes the "is this the default settings?" comparison order-independent.
    //
    // Eager on purpose: there is a single settings row, the set holds a handful of patterns, and
    // it is read on every incoming ping, outside any transaction of its own.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "index_settings_ping_deny",
            joinColumns = @JoinColumn(name = "index_settings_id")
    )
    @Column(name = "pattern", nullable = false)
    @SortNatural
    // the builder copies this set; call sites only ever read it or mutate it through the getter,
    // so no setter is exposed
    @Setter(AccessLevel.NONE)
    private SortedSet<String> denyList = new TreeSet<>();

    @Builder(toBuilder = true)
    IndexSettingsPing(final Duration validDuration, final Duration rateLimitDuration,
            final Integer rateLimitHits, final Set<String> denyList) {
        this.validDuration = validDuration;
        this.rateLimitDuration = rateLimitDuration;
        this.rateLimitHits = rateLimitHits;
        if (denyList != null) {
            this.denyList = new TreeSet<>(denyList);
        }
    }

    public static IndexSettingsPing getDefault() {
        return IndexSettingsPing
                .builder()
                .validDuration(Duration.ofDays(DEFAULT_VALID_DAYS))
                .rateLimitDuration(Duration.ofHours(DEFAULT_LIMIT_DURATION_HOURS))
                .rateLimitHits(DEFAULT_LIMIT_HITS)
                .denyList(Set.of("^(http|https)://localhost(:[0-9]+){0,1}.*$"))
                .build();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final IndexSettingsPing that = (IndexSettingsPing) other;
        // Hibernate's PersistentSortedSet compares by content, unlike PersistentBag, so the deny
        // list needs no copy-then-compare workaround here
        return Objects.equals(validDuration, that.validDuration)
                && Objects.equals(rateLimitDuration, that.rateLimitDuration)
                && Objects.equals(rateLimitHits, that.rateLimitHits)
                && Objects.equals(denyList, that.denyList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validDuration, rateLimitDuration, rateLimitHits, denyList);
    }
}
