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
package org.fairdatateam.fairdatapoint.index.entry;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.fairdatateam.fairdatapoint.index.RepositoryMetadata;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * A FAIR Data Point the Index knows about, identified by the URL it pings from.
 */
@Entity
@Table(name = "index_entry")
@NoArgsConstructor
// Package-private for the same reason as in User: Lombok's @Builder needs an all-args
// constructor to build from, and @NoArgsConstructor stops it from being synthesized implicitly.
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@Builder(toBuilder = true)
public class IndexEntry {

    @Id
    private UUID uuid;

    @Column(name = "client_url", nullable = false, unique = true)
    private String clientUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IndexEntryState state = IndexEntryState.Unknown;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IndexEntryPermit permit = IndexEntryPermit.PENDING;

    // Named after what they mean to the REST API rather than after their columns: the API hands
    // the names of these very fields to Spring Data as sort properties.
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant registrationTime;

    @Column(name = "updated_at", nullable = false)
    private Instant modificationTime;

    @Column(name = "last_retrieval_at")
    private Instant lastRetrievalTime;

    // Never null, unlike in the document store: the harvested properties are a NOT NULL column,
    // so an entry that has never been retrieved carries an empty metadata record rather than
    // none at all. Callers tell the two apart by the absent repository URI.
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
                name = "metadataVersion", column = @Column(name = "metadata_version", nullable = false)
        ),
        @AttributeOverride(name = "repositoryUri", column = @Column(name = "repository_uri")),
        @AttributeOverride(name = "metadata", column = @Column(name = "metadata", nullable = false))
    })
    @Builder.Default
    private RepositoryMetadata currentMetadata = new RepositoryMetadata();

    public Duration getLastRetrievalAgo() {
        if (lastRetrievalTime == null) {
            return null;
        }
        return Duration.between(lastRetrievalTime, Instant.now());
    }
}
