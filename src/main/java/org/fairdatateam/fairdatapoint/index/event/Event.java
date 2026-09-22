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
package org.fairdatateam.fairdatapoint.index.event;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fairdatateam.fairdatapoint.common.persistence.EventPayloadJsonConverter;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntry;
import org.fairdatateam.fairdatapoint.index.webhook.WebhookPing;
import org.fairdatateam.fairdatapoint.index.webhook.WebhookTrigger;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Something that happened to the Index: a ping came in, metadata was retrieved, an administrator
 * asked for a retrieval, a webhook was delivered or pinged.
 */
@Entity
@Table(name = "index_event")
@NoArgsConstructor
@Getter
@Setter
public class Event {

    @Id
    private UUID uuid = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private Integer version;

    // The event that caused this one. Nothing reads it back - it is recorded for the audit trail
    // and followed by hand - so it is loaded only on demand.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by")
    private Event triggeredBy;

    // The entry this event is about. Eager on purpose: the asynchronous triggers and the resume
    // of unfinished events at start-up read the entry, and its client URL and state, from events
    // that have long left the session they were loaded in, with open-in-view disabled.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "related_to")
    private IndexEntry relatedTo;

    @Convert(converter = EventPayloadJsonConverter.class)
    @Column(nullable = false)
    private EventPayload payload = new EventPayload();

    // Copied out of whichever branch of the payload carries it, so that the rate limit on
    // incoming pings stays a query over an indexed column instead of a search through JSON.
    @Setter(AccessLevel.NONE)
    @Column(name = "remote_addr")
    private String remoteAddr;

    // Set here rather than by Hibernate: an event is timestamped when it is created, which is
    // not when it is first saved - a metadata retrieval is prepared, then executed, then stored -
    // and the resume of unfinished events relies on that being the moment it was created.
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant created = Instant.now();

    @Column(name = "executed_at")
    private Instant executed;

    @Column(name = "finished_at")
    private Instant finished;

    // Only there because the column is NOT NULL for every table of the schema; not exposed.
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Event(Integer version, IncomingPing incomingPing) {
        this.type = EventType.IncomingPing;
        this.version = version;
        payload.setIncomingPing(incomingPing);
    }

    public Event(Integer version, Event triggerEvent, IndexEntry relatedTo,
                 MetadataRetrieval metadataRetrieval) {
        this.type = EventType.MetadataRetrieval;
        this.version = version;
        this.triggeredBy = triggerEvent;
        this.relatedTo = relatedTo;
        payload.setMetadataRetrieval(metadataRetrieval);
    }

    public Event(Integer version, AdminTrigger adminTrigger) {
        this.type = EventType.AdminTrigger;
        this.version = version;
        payload.setAdminTrigger(adminTrigger);
    }

    public Event(Integer version, WebhookTrigger webhookTrigger, Event triggerEvent) {
        this.type = EventType.WebhookTrigger;
        this.version = version;
        payload.setWebhookTrigger(webhookTrigger);
        this.triggeredBy = triggerEvent;
        this.relatedTo = triggerEvent.getRelatedTo();
    }

    public Event(Integer version, WebhookPing webhookPing) {
        this.type = EventType.WebhookPing;
        this.version = version;
        payload.setWebhookPing(webhookPing);
    }

    public IncomingPing getIncomingPing() {
        return payload.getIncomingPing();
    }

    public MetadataRetrieval getMetadataRetrieval() {
        return payload.getMetadataRetrieval();
    }

    public AdminTrigger getAdminTrigger() {
        return payload.getAdminTrigger();
    }

    public WebhookPing getWebhookPing() {
        return payload.getWebhookPing();
    }

    public WebhookTrigger getWebhookTrigger() {
        return payload.getWebhookTrigger();
    }

    public boolean isExecuted() {
        return executed != null;
    }

    public void execute() {
        executed = Instant.now();
    }

    public boolean isFinished() {
        return finished != null;
    }

    public void finish() {
        finished = Instant.now();
    }

    @PrePersist
    @PreUpdate
    void copyRemoteAddrFromPayload() {
        remoteAddr = findRemoteAddr();
    }

    private String findRemoteAddr() {
        final IncomingPing incomingPing = payload.getIncomingPing();
        if (incomingPing != null && incomingPing.getExchange() != null) {
            return incomingPing.getExchange().getRemoteAddr();
        }
        if (payload.getAdminTrigger() != null) {
            return payload.getAdminTrigger().getRemoteAddr();
        }
        if (payload.getWebhookPing() != null) {
            return payload.getWebhookPing().getRemoteAddr();
        }
        return null;
    }
}
