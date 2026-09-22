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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fairdatateam.fairdatapoint.index.webhook.WebhookPing;
import org.fairdatateam.fairdatapoint.index.webhook.WebhookTrigger;

/**
 * What an event recorded, in the one shape that covers all five kinds of event: exactly one of
 * the five branches is filled, the one matching the event's {@link EventType}.
 *
 * <p>The branches used to be five separate fields of the event document. They are gathered here
 * because the relational store keeps them in a single JSON column, and {@code Event} still
 * exposes them one by one, so nothing outside this package notices.</p>
 *
 * <p>Value equality matters: Hibernate decides whether the payload column needs rewriting by
 * comparing the current payload with the one it read, which it copies by converting it to JSON
 * and back. Without equality by value, every flush would rewrite the column.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPayload {

    private IncomingPing incomingPing;

    private MetadataRetrieval metadataRetrieval;

    private AdminTrigger adminTrigger;

    private WebhookPing webhookPing;

    private WebhookTrigger webhookTrigger;

}
