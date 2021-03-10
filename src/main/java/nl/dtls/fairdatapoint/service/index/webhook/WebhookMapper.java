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
package nl.dtls.fairdatapoint.service.index.webhook;

import nl.dtls.fairdatapoint.api.dto.index.webhook.WebhookPayloadDTO;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.entity.index.event.WebhookPing;
import nl.dtls.fairdatapoint.entity.index.event.WebhookTrigger;
import nl.dtls.fairdatapoint.entity.index.webhook.Webhook;
import nl.dtls.fairdatapoint.entity.index.webhook.WebhookEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;

@Service
public class WebhookMapper {

    private static final Integer VERSION = 1;

    public Event toTriggerEvent(Webhook webhook, WebhookEvent webhookEvent, Event triggerEvent) {
        var webhookTrigger = new WebhookTrigger();
        webhookTrigger.setWebhook(webhook);
        webhookTrigger.setMatchedEvent(webhookEvent);
        return new Event(VERSION, webhookTrigger, triggerEvent);
    }

    public Event toPingEvent(HttpServletRequest request, Authentication authentication, UUID webhookUuid, String remoteAddr) {
        var webhookPing = new WebhookPing();
        webhookPing.setWebhookUuid(webhookUuid);
        webhookPing.setRemoteAddr(remoteAddr);
        webhookPing.setTokenName(authentication.getName());
        return new Event(VERSION, webhookPing);
    }

    public WebhookPayloadDTO toWebhookPayloadDTO(Event event) {
        WebhookPayloadDTO webhookPayload = new WebhookPayloadDTO();
        webhookPayload.setEvent(event.getWebhookTrigger().getMatchedEvent());
        webhookPayload.setClientUrl(event.getRelatedTo().getClientUrl());
        webhookPayload.setSecret(event.getWebhookTrigger().getWebhook().getSecret());
        webhookPayload.setUuid(event.getUuid().toString());
        webhookPayload.setTimestamp(Instant.now().toString());
        return webhookPayload;
    }

}
