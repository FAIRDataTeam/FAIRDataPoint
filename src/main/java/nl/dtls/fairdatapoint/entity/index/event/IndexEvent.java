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
package nl.dtls.fairdatapoint.entity.index.event;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntity;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.event.payload.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;

@Entity(name = "IndexEvent")
@Table(name = "index_event")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class IndexEvent extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", columnDefinition = "INDEX_EVENT_TYPE", nullable = false)
    private IndexEventType type;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "remote_addr")
    private String remoteAddr;

    @NotNull
    @Type(JsonType.class)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private IndexEventPayload payload;

    @ManyToOne
    @JoinColumn(name = "triggered_by")
    private IndexEvent triggeredBy;

    @ManyToOne
    @JoinColumn(name = "related_to")
    private IndexEntry relatedTo;

    @Column(name = "executed_at")
    private Instant executedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    public IndexEvent(Integer version, IncomingPing incomingPing) {
        this.type = IndexEventType.INCOMING_PING;
        this.version = version;
        this.remoteAddr = incomingPing.getExchange().getRemoteAddr();
        this.payload = IndexEventPayload.builder().incomingPing(incomingPing).build();
    }

    public IndexEvent(Integer version, IndexEvent triggerEvent, IndexEntry relatedTo,
                      MetadataRetrieval metadataRetrieval) {
        this.type = IndexEventType.METADATA_RETRIEVAL;
        this.version = version;
        this.triggeredBy = triggerEvent;
        this.relatedTo = relatedTo;
        this.payload = IndexEventPayload.builder().metadataRetrieval(metadataRetrieval).build();
    }

    public IndexEvent(Integer version, AdminTrigger adminTrigger) {
        this.type = IndexEventType.ADMIN_TRIGGER;
        this.version = version;
        this.payload = IndexEventPayload.builder().adminTrigger(adminTrigger).build();
    }

    public IndexEvent(Integer version, WebhookTrigger webhookTrigger, IndexEvent triggerEvent) {
        this.type = IndexEventType.WEBHOOK_TRIGGER;
        this.version = version;
        this.payload = IndexEventPayload.builder().webhookTrigger(webhookTrigger).build();
        this.triggeredBy = triggerEvent;
        this.relatedTo = triggerEvent.getRelatedTo();
    }

    public IndexEvent(Integer version, WebhookPing webhookPing) {
        this.type = IndexEventType.WEBHOOK_PING;
        this.version = version;
        this.payload = IndexEventPayload.builder().webhookPing(webhookPing).build();
    }

    public boolean isExecuted() {
        return getExecutedAt() != null;
    }

    public void execute() {
        setExecutedAt(Instant.now());
    }

    public boolean isFinished() {
        return getFinishedAt() != null;
    }

    public void finish() {
        setFinishedAt(Instant.now());
    }
}
