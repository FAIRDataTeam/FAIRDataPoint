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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.index.webhook.WebhookPayloadDTO;
import nl.dtls.fairdatapoint.database.db.repository.IndexEventRepository;
import nl.dtls.fairdatapoint.database.db.repository.IndexWebhookRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.index.event.IndexEvent;
import nl.dtls.fairdatapoint.entity.index.settings.SettingsIndexRetrieval;
import nl.dtls.fairdatapoint.entity.index.webhook.IndexWebhook;
import nl.dtls.fairdatapoint.entity.index.webhook.IndexWebhookEvent;
import nl.dtls.fairdatapoint.service.UtilityService;
import nl.dtls.fairdatapoint.service.index.common.RequiredEnabledIndexFeature;
import nl.dtls.fairdatapoint.service.index.settings.IndexSettingsService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final String SECRET_PLACEHOLDER = "*** HIDDEN ***";

    private final WebhookMapper webhookMapper;

    private final ObjectMapper objectMapper;

    private final IndexWebhookRepository webhookRepository;

    private final IndexEventRepository eventRepository;

    private final IndexSettingsService indexSettingsService;

    private final UtilityService utilityService;

    @RequiredEnabledIndexFeature
    public void processWebhookTrigger(IndexEvent event) {
        final SettingsIndexRetrieval retrievalSettings =
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
    public void triggerWebhook(IndexWebhook webhook, IndexWebhookEvent webhookEvent, IndexEvent triggerEvent) {
        final IndexEvent event = webhookMapper.toTriggerEvent(webhook, webhookEvent, triggerEvent);
        processWebhookTrigger(event);
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerWebhooks(IndexWebhookEvent webhookEvent, IndexEvent triggerEvent) {
        log.info("Triggered webhook event {} by event {}", webhookEvent, triggerEvent.getUuid());
        WebhookUtils
                .filterMatching(webhookRepository.findAll(), webhookEvent, triggerEvent)
                .forEach(webhook -> triggerWebhook(webhook, webhookEvent, triggerEvent));
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerWebhooks(IndexEvent triggerEvent) {
        switch (triggerEvent.getType()) {
            case ADMIN_TRIGGER:
                triggerWebhooks(IndexWebhookEvent.ADMIN_TRIGGER, triggerEvent);
                break;
            case INCOMING_PING:
                triggerWebhooks(IndexWebhookEvent.INCOMING_PING, triggerEvent);
                if (triggerEvent.getPayload().getIncomingPing().getNewEntry()) {
                    triggerWebhooks(IndexWebhookEvent.NEW_ENTRY, triggerEvent);
                }
                break;
            case METADATA_RETRIEVAL:
                switch (triggerEvent.getRelatedTo().getState()) {
                    case VALID -> triggerWebhooks(IndexWebhookEvent.ENTRY_VALID, triggerEvent);
                    case INVALID -> triggerWebhooks(IndexWebhookEvent.ENTRY_INVALID, triggerEvent);
                    case UNREACHABLE -> triggerWebhooks(
                            IndexWebhookEvent.ENTRY_UNREACHABLE, triggerEvent
                    );
                    default -> log.warn("Invalid state of MetadataRetrieval: {}",
                            triggerEvent.getRelatedTo().getState());
                }
                break;
            case WEBHOOK_PING:
                triggerWebhooks(IndexWebhookEvent.WEBHOOK_PING, triggerEvent);
                break;
            default:
                log.warn("Invalid event type for webhook trigger: {}", triggerEvent.getType());
        }
    }

    @RequiredEnabledIndexFeature
    public IndexEvent handleWebhookPing(HttpServletRequest request, UUID webhookUuid) {
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        final Optional<IndexWebhook> webhook = webhookRepository.findByUuid(webhookUuid);
        final IndexEvent event = eventRepository.save(
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
