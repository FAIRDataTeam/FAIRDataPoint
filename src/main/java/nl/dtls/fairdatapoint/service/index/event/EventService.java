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
package nl.dtls.fairdatapoint.service.index.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.db.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.database.db.repository.IndexEventRepository;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;
import nl.dtls.fairdatapoint.entity.index.entry.RepositoryMetadata;
import nl.dtls.fairdatapoint.entity.index.event.IndexEvent;
import nl.dtls.fairdatapoint.entity.index.event.IndexEventType;
import nl.dtls.fairdatapoint.entity.index.exception.IncorrectPingFormatException;
import nl.dtls.fairdatapoint.entity.index.exception.PingDeniedException;
import nl.dtls.fairdatapoint.entity.index.exception.RateLimitException;
import nl.dtls.fairdatapoint.entity.index.http.Exchange;
import nl.dtls.fairdatapoint.entity.index.http.ExchangeState;
import nl.dtls.fairdatapoint.entity.index.settings.SettingsIndexPing;
import nl.dtls.fairdatapoint.entity.index.settings.SettingsIndexRetrieval;
import nl.dtls.fairdatapoint.service.UtilityService;
import nl.dtls.fairdatapoint.service.index.common.RequiredEnabledIndexFeature;
import nl.dtls.fairdatapoint.service.index.entry.IndexEntryService;
import nl.dtls.fairdatapoint.service.index.settings.IndexSettingsService;
import nl.dtls.fairdatapoint.service.index.webhook.WebhookService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private static final int PAGE_SIZE = 10;

    private final ObjectMapper objectMapper;

    private final ThreadPoolTaskExecutor executor;

    private final IndexEventRepository eventRepository;

    private final IndexEntryRepository indexEntryRepository;

    private final IndexEntryService indexEntryService;

    private final WebhookService webhookService;

    private final EventMapper eventMapper;

    private final UtilityService utilityService;

    private final IncomingPingUtils incomingPingUtils;

    private final IndexSettingsService indexSettingsService;

    public Iterable<IndexEvent> getEvents(IndexEntry indexEntry) {
        // TODO: make events pagination in the future
        return eventRepository.getAllByRelatedTo(indexEntry,
                PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "created")));
    }

    @RequiredEnabledIndexFeature
    public Iterable<IndexEvent> getEvents(UUID indexEntryUuid) {
        return indexEntryService
                .getEntry(indexEntryUuid)
                .map(this::getEvents).orElse(Collections.emptyList());
    }

    @Transactional
    @RequiredEnabledIndexFeature
    @SneakyThrows
    public IndexEvent acceptIncomingPing(PingDTO reqDto, HttpServletRequest request) {
        final String remoteAddr = utilityService.getRemoteAddr(request);
        final SettingsIndexPing pingSettings = indexSettingsService.getOrDefaults().getPing();

        if (indexSettingsService.isPingDenied(reqDto)) {
            log.info("Received ping is denied");
            throw new PingDeniedException(reqDto.getClientUrl());
        }

        final Instant rateLimitSince = Instant.now().minus(pingSettings.getRateLimitDuration());
        final List<IndexEvent> previousPings =
                eventRepository.findAllByRemoteAddrAndCreatedAtAfter(
                        remoteAddr, rateLimitSince
                );
        if (previousPings.size() > pingSettings.getRateLimitHits()) {
            log.warn("Rate limit for PING reached by {}", remoteAddr);
            throw new RateLimitException(String.format(
                    "Rate limit reached for %s (max. %d per %s) - PING ignored",
                    remoteAddr, pingSettings.getRateLimitHits(), pingSettings.getRateLimitDuration().toString())
            );
        }

        final IndexEvent event = incomingPingUtils.prepareEvent(reqDto, request, remoteAddr);
        eventRepository.saveAndFlush(event);
        event.execute();
        try {
            final IndexEntry indexEntry = indexEntryService.storeEntry(reqDto);
            event.getPayload().getIncomingPing()
                    .setNewEntry(indexEntry.getCreatedAt().equals(indexEntry.getUpdatedAt()));
            event.getPayload().getIncomingPing().getExchange().getResponse()
                    .setCode(HttpStatus.CREATED.value());
            event.setRelatedTo(indexEntry);
            log.info("Accepted incoming ping as a new event");
        }
        catch (Exception exception) {
            final IncorrectPingFormatException nextException =
                    new IncorrectPingFormatException("Could not parse PING: " + exception.getMessage());
            event.getPayload().getIncomingPing().getExchange().getResponse()
                    .setCode(HttpStatus.BAD_REQUEST.value());
            event.getPayload().getIncomingPing().getExchange().getResponse()
                    .setBody(objectMapper.writeValueAsString(nextException.getErrorDTO()));
            event.setFinishedAt(Instant.now());
            eventRepository.save(event);
            log.info("Incoming ping has incorrect format: {}", exception.getMessage());
            throw nextException;
        }
        event.setFinishedAt(Instant.now());
        return eventRepository.save(event);
    }

    private void processMetadataRetrieval(IndexEvent event) {
        final SettingsIndexRetrieval retrievalSettings = indexSettingsService.getOrDefaults().getRetrieval();
        final String clientUrl = event.getRelatedTo().getClientUrl();
        if (MetadataRetrievalUtils.shouldRetrieve(event, retrievalSettings.getRateLimitWait())) {
            indexEntryRepository.save(event.getRelatedTo());
            eventRepository.save(event);
            event.execute();

            log.info("Retrieving metadata for {}", clientUrl);
            MetadataRetrievalUtils.retrieveRepositoryMetadata(event, retrievalSettings.getTimeout());
            final Exchange exchange = event.getPayload().getMetadataRetrieval().getExchange();
            if (exchange.getState() == ExchangeState.Retrieved) {
                try {
                    log.info("Parsing metadata for {}", clientUrl);
                    final Optional<RepositoryMetadata> metadata =
                            MetadataRetrievalUtils.parseRepositoryMetadata(exchange.getResponse().getBody());
                    if (metadata.isPresent()) {
                        event.getPayload().getMetadataRetrieval().setMetadata(metadata.get());
                        event.getRelatedTo().setCurrentMetadata(metadata.get());
                        event.getRelatedTo().setState(IndexEntryState.VALID);
                        log.info("Storing metadata for {}", clientUrl);
                        indexEntryRepository.save(event.getRelatedTo());
                    }
                    else {
                        log.info("Repository not found in metadata for {}", clientUrl);
                        event.getRelatedTo().setState(IndexEntryState.INVALID);
                        event.getPayload().getMetadataRetrieval().setError("Repository not found in metadata");
                    }
                }
                catch (Exception exception) {
                    log.info("Cannot parse metadata for {}", clientUrl);
                    event.getRelatedTo().setState(IndexEntryState.INVALID);
                    event.getPayload().getMetadataRetrieval().setError("Cannot parse metadata");
                }
            }
            else {
                event.getRelatedTo().setState(IndexEntryState.UNREACHABLE);
                log.info("Cannot retrieve metadata for {}: {}", clientUrl, exchange.getError());
            }
        }
        else {
            log.info("Rate limit reached for {} (skipping metadata retrieval)", clientUrl);
            event.getPayload().getMetadataRetrieval().setError("Rate limit reached (skipping)");
        }
        event.getRelatedTo().setCreatedAt(Instant.now());
        event.finish();
        final IndexEvent newEvent = eventRepository.save(event);
        indexEntryRepository.save(newEvent.getRelatedTo());
        webhookService.triggerWebhooks(newEvent);
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerMetadataRetrieval(IndexEvent triggerEvent) {
        log.info("Initiating metadata retrieval triggered by {}", triggerEvent.getUuid());
        final Iterable<IndexEvent> events = MetadataRetrievalUtils.prepareEvents(triggerEvent, indexEntryService);
        for (IndexEvent event : events) {
            log.info("Triggering metadata retrieval for {} as {}", event.getRelatedTo().getClientUrl(),
                    event.getUuid());
            try {
                processMetadataRetrieval(event);
            }
            catch (Exception exception) {
                log.error("Failed to retrieve metadata: {}", exception.getMessage());
            }
        }
        log.info("Finished metadata retrieval triggered by {}", triggerEvent.getUuid());
    }

    private void resumeUnfinishedEvents() {
        log.info("Resuming unfinished events");
        for (IndexEvent event : eventRepository.getAllByFinishedAtIsNull()) {
            log.info("Resuming event {}", event.getUuid());

            try {
                if (event.getType() == IndexEventType.METADATA_RETRIEVAL) {
                    processMetadataRetrieval(event);
                }
                else if (event.getType() == IndexEventType.WEBHOOK_TRIGGER) {
                    webhookService.processWebhookTrigger(event);
                }
                else {
                    log.warn("Unknown event type {} ({})", event.getUuid(), event.getType());
                }
            }
            catch (Exception exception) {
                log.error("Failed to resume event {}: {}", event.getUuid(), exception.getMessage());
            }
        }
        log.info("Finished unfinished events");
    }

    @PostConstruct
    public void startResumeUnfinishedEvents() {
        executor.submit(this::resumeUnfinishedEvents);
    }

    @RequiredEnabledIndexFeature
    public IndexEvent acceptAdminTrigger(HttpServletRequest request, PingDTO pingDTO) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final IndexEvent event =
                eventMapper.toAdminTriggerEvent(authentication, pingDTO.getClientUrl(),
                        utilityService.getRemoteAddr(request));
        final IndexEntry entry = indexEntryService.storeEntry(pingDTO);
        event.setRelatedTo(entry);
        event.finish();
        return eventRepository.save(event);
    }

    @RequiredEnabledIndexFeature
    public IndexEvent acceptAdminTriggerAll(HttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final IndexEvent event =
                eventMapper.toAdminTriggerEvent(authentication, null,
                        utilityService.getRemoteAddr(request));
        event.finish();
        return eventRepository.save(event);
    }
}
