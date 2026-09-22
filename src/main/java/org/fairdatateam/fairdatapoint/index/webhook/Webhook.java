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
package org.fairdatateam.fairdatapoint.index.webhook;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 * A URL the Index posts to when something happens to an entry it knows. A webhook listens either
 * to every event or to the events it names, and either for every entry or for the entries it
 * names by their client URL.
 *
 * <p>Note: no endpoint creates webhooks yet, so the table is only ever written by hand.</p>
 */
@Entity
@Table(name = "index_webhook")
@NoArgsConstructor
@Getter
@Setter
public class Webhook {

    @Id
    private UUID uuid;

    @Column(name = "payload_url", nullable = false)
    private String payloadUrl;

    @Column(nullable = false)
    private String secret;

    @Column(name = "all_events", nullable = false)
    private boolean allEvents;

    // Sets rather than lists, here and for the entries: the repository loads both collections in
    // one query, and a join over two collections repeats every row of one for every row of the
    // other. Lists would be bags and would keep those duplicates - and could not even be fetched
    // together, since Hibernate refuses two bags in one query. The primary key of the collection
    // table forbids duplicates in the database anyway. Sorted, because the collection table has
    // no position column: events come back in the order the enum declares them, entries in
    // alphabetical order.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "index_webhook_event",
            joinColumns = @JoinColumn(name = "webhook_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "event", nullable = false)
    @SortNatural
    // the builder copies this set; call sites only ever read it or mutate it through the getter,
    // so no setter is exposed
    @Setter(AccessLevel.NONE)
    private SortedSet<WebhookEvent> events = new TreeSet<>();

    @Column(name = "all_entries", nullable = false)
    private boolean allEntries;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "index_webhook_entry",
            joinColumns = @JoinColumn(name = "webhook_id")
    )
    @Column(name = "entry", nullable = false)
    @SortNatural
    @Setter(AccessLevel.NONE)
    private SortedSet<String> entries = new TreeSet<>();

    @Column(nullable = false)
    private boolean enabled;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Builder
    Webhook(final UUID uuid, final String payloadUrl, final String secret, final boolean allEvents,
            final Set<WebhookEvent> events, final boolean allEntries, final Set<String> entries,
            final boolean enabled) {
        this.uuid = uuid;
        this.payloadUrl = payloadUrl;
        this.secret = secret;
        this.allEvents = allEvents;
        this.allEntries = allEntries;
        this.enabled = enabled;
        if (events != null) {
            this.events = new TreeSet<>(events);
        }
        if (entries != null) {
            this.entries = new TreeSet<>(entries);
        }
    }
}
