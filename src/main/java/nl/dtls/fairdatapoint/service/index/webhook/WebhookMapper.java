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

    public Event toPingEvent(HttpServletRequest request, Authentication authentication, UUID webhookUuid) {
        var webhookPing = new WebhookPing();
        webhookPing.setWebhookUuid(webhookUuid);
        webhookPing.setRemoteAddr(request.getRemoteAddr());
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
