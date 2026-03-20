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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.index.webhook.WebhookPayloadDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.EventRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.WebhookRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.index.event.Event;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettingsRetrieval;
import nl.dtls.fairdatapoint.entity.index.webhook.Webhook;
import nl.dtls.fairdatapoint.entity.index.webhook.WebhookEvent;
import nl.dtls.fairdatapoint.service.UtilityService;
import nl.dtls.fairdatapoint.service.index.common.RequiredEnabledIndexFeature;
import nl.dtls.fairdatapoint.service.index.settings.IndexSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class WebhookService {

    private static final String SECRET_PLACEHOLDER = "*** HIDDEN ***";

    @Autowired
    private WebhookMapper webhookMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IndexSettingsService indexSettingsService;

    @Autowired
    private UtilityService utilityService;

    @RequiredEnabledIndexFeature
    public void processWebhookTrigger(Event event) {
        final IndexSettingsRetrieval retrievalSettings =
                indexSettingsService.getOrDefaults().getRetrieval();
        event.execute();
        eventRepository.save(event);
        final WebhookPayloadDTO webhookPayload = webhookMapper.toWebhookPayloadDTO(event);
        try {
            final String payloadWithSecret = objectMapper.writeValueAsString(webhookPayload);
            final String signature = WebhookUtils.computeHashSignature(payloadWithSecret);
            webhookPayload.setSecret(SECRET_PLACEHOLDER);
            final String payloadWithoutSecret = objectMapper.writeValueAsString(webhookPayload);
            WebhookUtils.postWebhook(
                    event,
                    retrievalSettings.getTimeout(),
                    payloadWithoutSecret,
                    signature
            );
        }
        catch (JsonProcessingException exception) {
            log.error("Failed to convert webhook payload to string");
        }
        catch (NoSuchAlgorithmException exception) {
            log.error("Could not compute SHA-1 signature of payload");
        }
        event.finish();
        eventRepository.save(event);
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerWebhook(Webhook webhook, WebhookEvent webhookEvent, Event triggerEvent) {
        final Event event = webhookMapper.toTriggerEvent(webhook, webhookEvent, triggerEvent);
        processWebhookTrigger(event);
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerWebhooks(WebhookEvent webhookEvent, Event triggerEvent) {
        log.info("Triggered webhook event {} by event {}", webhookEvent, triggerEvent.getUuid());
        WebhookUtils
                .filterMatching(webhookRepository.findAll(), webhookEvent, triggerEvent)
                .forEach(webhook -> triggerWebhook(webhook, webhookEvent, triggerEvent));
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerWebhooks(Event triggerEvent) {
        switch (triggerEvent.getType()) {
            case AdminTrigger:
                triggerWebhooks(WebhookEvent.AdminTrigger, triggerEvent);
                break;
            case IncomingPing:
                triggerWebhooks(WebhookEvent.IncomingPing, triggerEvent);
                if (triggerEvent.getIncomingPing().getNewEntry()) {
                    triggerWebhooks(WebhookEvent.NewEntry, triggerEvent);
                }
                break;
            case MetadataRetrieval:
                switch (triggerEvent.getRelatedTo().getState()) {
                    case Valid -> triggerWebhooks(WebhookEvent.EntryValid, triggerEvent);
                    case Invalid -> triggerWebhooks(WebhookEvent.EntryInvalid, triggerEvent);
                    case Unreachable -> triggerWebhooks(
                            WebhookEvent.EntryUnreachable, triggerEvent
                    );
                    default -> log.warn("Invalid state of MetadataRetrieval: {}",
                            triggerEvent.getRelatedTo().getState());
                }
                break;
            case WebhookPing:
                triggerWebhooks(WebhookEvent.WebhookPing, triggerEvent);
                break;
            default:
                log.warn("Invalid event type for webhook trigger: {}", triggerEvent.getType());
        }
    }

    @RequiredEnabledIndexFeature
    public Event handleWebhookPing(HttpServletRequest request, UUID webhookUuid) {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        final Optional<Webhook> webhook = webhookRepository.findByUuid(webhookUuid);
        final Event event = eventRepository.save(
                webhookMapper.toPingEvent(
                        authentication,
                        webhookUuid,
                        utilityService.getRemoteAddr(request)
                )
        );
        if (webhook.isEmpty()) {
            throw new ResourceNotFoundException("There is no such webhook: " + webhookUuid);
        }
        return event;
    }
}
