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
package nl.dtls.fairdatapoint.entity.index.webhook;

import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntity;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "IndexWebhook")
@Table(name = "index_webhook")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class IndexWebhook extends BaseEntity {

    @NotNull
    @Column(name = "payload_url", nullable = false)
    private String payloadUrl;

    @NotNull
    @Column(name = "secret", nullable = false)
    private String secret;

    @NotNull
    @Column(name = "all_events", nullable = false)
    private Boolean allEvents;

    @NotNull
    @Column(name = "all_entries", nullable = false)
    private Boolean allEntries;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @NotNull
    @Type(ListArrayType.class)
    @Column(name = "entries", columnDefinition = "text[]", nullable = false)
    private List<String> entries = new ArrayList<>();

    @NotNull
    @Type(EnumArrayType.class)
    @Column(name = "events", columnDefinition = "INDEX_WEBHOOK_EVENT_TYPE[]", nullable = false)
    private List<IndexWebhookEvent> events = new ArrayList<>();
}
