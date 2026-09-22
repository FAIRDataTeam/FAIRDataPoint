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

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryRepository;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntry;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryState;
import org.fairdatateam.fairdatapoint.index.RepositoryMetadata;
import org.fairdatateam.fairdatapoint.index.event.dto.PingDTO;
import org.fairdatateam.fairdatapoint.index.exception.IncorrectPingFormatException;
import org.fairdatateam.fairdatapoint.index.exception.PingDeniedException;
import org.fairdatateam.fairdatapoint.index.exception.RateLimitException;
import org.fairdatateam.fairdatapoint.index.http.Exchange;
import org.fairdatateam.fairdatapoint.index.http.ExchangeState;
import org.fairdatateam.fairdatapoint.index.settings.IndexSettingsPing;
import org.fairdatateam.fairdatapoint.index.settings.IndexSettingsRetrieval;
import org.fairdatateam.fairdatapoint.index.RequiredEnabledIndexFeature;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryService;
import org.fairdatateam.fairdatapoint.index.settings.IndexSettingsService;
import org.fairdatateam.fairdatapoint.index.webhook.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class EventService {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    @Autowired
    @Lazy
    private IndexEntryService indexEntryService;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private IncomingPingUtils incomingPingUtils;

    @Autowired
    private IndexSettingsService indexSettingsService;

    public Iterable<Event> getEvents(IndexEntry indexEntry) {
        // TODO: make events pagination in the future
        return eventRepository.getAllByRelatedTo(indexEntry,
                PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "created")));
    }

    // Deliberately not transactional: a ping the Index cannot parse is still recorded, with the
    // response it produced, and only then rejected. One transaction around the whole method would
    // roll that record back together with the rejection. Every save below therefore commits on
    // its own, exactly as it did against the document store.
    @RequiredEnabledIndexFeature
    @SneakyThrows
    public Event acceptIncomingPing(PingDTO reqDto, HttpServletRequest request) {
        final String remoteAddr = request.getRemoteAddr();
        final IndexSettingsPing pingSettings = indexSettingsService.getOrDefaults().getPing();

        if (indexSettingsService.isPingDenied(reqDto)) {
            log.info("Received ping is denied");
            throw new PingDeniedException(reqDto.getClientUrl());
        }

        final Instant rateLimitSince = Instant.now().minus(pingSettings.getRateLimitDuration());
        final long previousPingCount = eventRepository.countByTypeAndRemoteAddrAndCreatedAfter(
                EventType.IncomingPing, remoteAddr, rateLimitSince);
        if (previousPingCount > pingSettings.getRateLimitHits()) {
            log.warn("Rate limit for PING reached by {}", remoteAddr);
            throw new RateLimitException(String.format(
                    "Rate limit reached for %s (max. %d per %s) - PING ignored",
                    remoteAddr, pingSettings.getRateLimitHits(), pingSettings.getRateLimitDuration().toString())
            );
        }

        final Event event = incomingPingUtils.prepareEvent(reqDto, request, remoteAddr);
        eventRepository.save(event);
        event.execute();
        try {
            final IndexEntry indexEntry = indexEntryService.storeEntry(reqDto);
            event.getIncomingPing()
                    .setNewEntry(indexEntry.getRegistrationTime().equals(indexEntry.getModificationTime()));
            event.getIncomingPing().getExchange().getResponse()
                    .setCode(HttpStatus.CREATED.value());
            event.setRelatedTo(indexEntry);
            log.info("Accepted incoming ping as a new event");
        }
        catch (Exception exception) {
            final IncorrectPingFormatException nextException =
                    new IncorrectPingFormatException("Could not parse PING: " + exception.getMessage());
            event.getIncomingPing().getExchange().getResponse()
                    .setCode(HttpStatus.BAD_REQUEST.value());
            event.getIncomingPing().getExchange().getResponse()
                    .setBody(jsonMapper.writeValueAsString(nextException.getErrorDTO()));
            event.setFinished(Instant.now());
            eventRepository.save(event);
            log.info("Incoming ping has incorrect format: {}", exception.getMessage());
            throw nextException;
        }
        event.setFinished(Instant.now());
        eventRepository.save(event);
        // The caller keeps working with this instance rather than with what save() returns:
        // saving a detached entity merges it into a copy, whose entry reference would be a proxy
        // that no longer has a session by the time the asynchronous triggers read it.
        return event;
    }

    private void processMetadataRetrieval(Event event) {
        final IndexSettingsRetrieval retrievalSettings = indexSettingsService.getOrDefaults().getRetrieval();
        final String clientUrl = event.getRelatedTo().getClientUrl();
        if (MetadataRetrievalUtils.shouldRetrieve(event, retrievalSettings.getRateLimitWait())) {
            indexEntryRepository.save(event.getRelatedTo());
            eventRepository.save(event);
            event.execute();

            log.info("Retrieving metadata for {}", clientUrl);
            MetadataRetrievalUtils.retrieveRepositoryMetadata(event, retrievalSettings.getTimeout());
            final Exchange exchange = event.getMetadataRetrieval().getExchange();
            if (exchange.getState() == ExchangeState.Retrieved) {
                try {
                    log.info("Parsing metadata for {}", clientUrl);
                    final Optional<RepositoryMetadata> metadata =
                            MetadataRetrievalUtils.parseRepositoryMetadata(exchange.getResponse().getBody());
                    if (metadata.isPresent()) {
                        event.getMetadataRetrieval().setMetadata(metadata.get());
                        event.getRelatedTo().setCurrentMetadata(metadata.get());
                        event.getRelatedTo().setState(IndexEntryState.Valid);
                        log.info("Storing metadata for {}", clientUrl);
                        indexEntryRepository.save(event.getRelatedTo());
                    }
                    else {
                        log.info("Repository not found in metadata for {}", clientUrl);
                        event.getRelatedTo().setState(IndexEntryState.Invalid);
                        event.getMetadataRetrieval().setError("Repository not found in metadata");
                    }
                }
                catch (Exception exception) {
                    log.info("Cannot parse metadata for {}", clientUrl);
                    event.getRelatedTo().setState(IndexEntryState.Invalid);
                    event.getMetadataRetrieval().setError("Cannot parse metadata");
                }
            }
            else {
                event.getRelatedTo().setState(IndexEntryState.Unreachable);
                log.info("Cannot retrieve metadata for {}: {}", clientUrl, exchange.getError());
            }
        }
        else {
            log.info("Rate limit reached for {} (skipping metadata retrieval)", clientUrl);
            event.getMetadataRetrieval().setError("Rate limit reached (skipping)");
        }
        event.getRelatedTo().setLastRetrievalTime(Instant.now());
        event.finish();
        eventRepository.save(event);
        indexEntryRepository.save(event.getRelatedTo());
        webhookService.triggerWebhooks(event);
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerMetadataRetrieval(Event triggerEvent) {
        log.info("Initiating metadata retrieval triggered by {}", triggerEvent.getUuid());
        final Iterable<Event> events = MetadataRetrievalUtils.prepareEvents(triggerEvent, indexEntryService);
        for (Event event : events) {
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
        for (Event event : eventRepository.getAllByFinishedIsNull()) {
            log.info("Resuming event {}", event.getUuid());

            try {
                if (event.getType() == EventType.MetadataRetrieval) {
                    processMetadataRetrieval(event);
                }
                else if (event.getType() == EventType.WebhookTrigger) {
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
    @Transactional
    public Event acceptAdminTrigger(HttpServletRequest request, PingDTO pingDTO) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Event event =
                eventMapper.toAdminTriggerEvent(authentication, pingDTO.getClientUrl(), request.getRemoteAddr());
        final IndexEntry entry = indexEntryService.storeEntry(pingDTO);
        event.setRelatedTo(entry);
        event.finish();
        eventRepository.save(event);
        // See acceptIncomingPing: the instance that was built here, not the merged copy.
        return event;
    }

    @RequiredEnabledIndexFeature
    @Transactional
    public Event acceptAdminTriggerAll(HttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Event event = eventMapper.toAdminTriggerEvent(authentication, null, request.getRemoteAddr());
        event.finish();
        eventRepository.save(event);
        return event;
    }
}
