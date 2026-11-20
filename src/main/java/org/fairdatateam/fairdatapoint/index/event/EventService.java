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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.common.error.ErrorDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private static final int PAGE_SIZE = 10;

    private final JsonMapper jsonMapper;

    private final ThreadPoolTaskExecutor executor;

    private final EventRepository eventRepository;

    private final IndexEntryService indexEntryService;

    private final EventMapper eventMapper;

    private final IncomingPingUtils incomingPingUtils;

    private final IndexSettingsService indexSettingsService;

    public Iterable<Event> getEvents(IndexEntry indexEntry) {
        // TODO: make events pagination in the future
        return eventRepository.getAllByRelatedTo(indexEntry,
                PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "created")));
    }

    @RequiredEnabledIndexFeature
    public Iterable<Event> getEvents(String indexEntryUuid) {
        return indexEntryService
                .getEntry(indexEntryUuid)
                .map(this::getEvents).orElse(Collections.emptyList());
    }

    /**
     * Raises a <code>RateLimitException</code> if the rate limit is exceeded.
     */
    public void checkRateLimit(String remoteAddr) {
        final IndexSettingsPing pingSettings = indexSettingsService.getOrDefaults().getPing();
        final Instant rateLimitSince = Instant.now().minus(pingSettings.getRateLimitDuration());
        final List<Event> previousPings =
                eventRepository.findAllByIncomingPingExchangeRemoteAddrAndCreatedAfter(
                        remoteAddr, rateLimitSince
                );
        if (previousPings.size() > pingSettings.getRateLimitHits()) {
            log.warn("Rate limit for PING reached by {}", remoteAddr);
            throw new RateLimitException(String.format(
                    "Rate limit reached for %s (max. %d per %s) - PING ignored",
                    remoteAddr, pingSettings.getRateLimitHits(), pingSettings.getRateLimitDuration().toString())
            );
        }
    }

    /**
     * Creates a new ping event for a matching index entry in the database. Creates the index entry if necessary.
     * Aborts if the clientUrl from the ping body is denied, e.g. if it contains localhost.
     */
    @RequiredEnabledIndexFeature
    @SneakyThrows
    public Event handleIncomingPing(String clientUrl, HttpServletRequest request) {
        // todo: Note that the events stored in the database contain an exchange with copies of the ping request and
        //  corresponding response. These are never used internally and are not exposed via the API (EventDTO only
        //  contains uuid, type, created, and finished). So, it looks like this is only intended for audit or diagnostic
        //  purposes, but that is not documented anywhere. For example fdp home has over 60k events with full request
        //  info just sitting there doing nothing.

        // abort if clientUrl matches denyList
        if (indexSettingsService.isPingDenied(clientUrl)) {
            log.info("Received ping is denied");
            throw new PingDeniedException(clientUrl);
        }

        // create new event in database
        final Event event = incomingPingUtils.prepareEvent(clientUrl, request);
        eventRepository.save(event);
        event.setExecutedNow();

        // populate the event with an index entry
        event.setExecutedNow();
        try {
            final Optional<IndexEntry> optionalIndexEntry = indexEntryService.getEntryByClientUrl(clientUrl);
            // indicate if a new entry will be created for this ping
            event.getIncomingPing().setNewEntry(optionalIndexEntry.isEmpty());
            final IndexEntry indexEntry;
            if (optionalIndexEntry.isPresent()) {
                // update modification time for existing entry
                // todo: We're keeping the original behavior, but I think this is wrong:
                //  The entry is not modified, so why update the timestamp?
                indexEntry = optionalIndexEntry.orElseThrow();
                indexEntry.setModificationTime(Instant.now());
                indexEntryService.saveEntry(indexEntry);
            }
            else {
                // create new entry
                indexEntry = indexEntryService.createNewEntry(clientUrl);
            }
            event.setRelatedTo(indexEntry);
            event.getIncomingPing().getExchange().getResponse().setCode(HttpStatus.NO_CONTENT.value());
            log.info("Accepted incoming ping as a new event");
        }
        catch (Exception exception) {
            log.info("Error updating ping event with index entry: {}", exception.getMessage());
            final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            event.getIncomingPing().getExchange().getResponse().setCode(httpStatus.value());
            event.getIncomingPing().getExchange().getResponse()
                    .setBody(jsonMapper.writeValueAsString(new ErrorDTO(httpStatus, exception.getMessage())));
            event.setFinished(Instant.now());
            eventRepository.save(event);
            throw exception;
        }
        event.setFinished(Instant.now());
        return eventRepository.save(event);
    }

    private void processMetadataRetrieval(Event event) {
        final IndexSettingsRetrieval retrievalSettings = indexSettingsService.getOrDefaults().getRetrieval();
        final String clientUrl = event.getRelatedTo().getClientUrl();
        if (MetadataRetrievalUtils.shouldRetrieve(event, retrievalSettings.getRateLimitWait())) {
            indexEntryService.saveEntry(event.getRelatedTo());
            eventRepository.save(event);
            event.setExecutedNow();

            log.info("Retrieving metadata for {}", clientUrl);
            MetadataRetrievalUtils.retrieveRepositoryMetadata(event, retrievalSettings.getTimeout());
            final Exchange exchange = event.getMetadataRetrieval().getExchange();
            if (exchange.getState() == ExchangeState.Retrieved) {
                try {
                    log.info("Parsing metadata for {}", clientUrl);
                    final Optional<RepositoryMetadata> metadata =
                            MetadataRetrievalUtils.parseRepositoryMetadata(exchange.getResponse().getBody());
                    if (metadata.isPresent()) {
                        event.getMetadataRetrieval().setMetadata(metadata.orElseThrow());
                        event.getRelatedTo().setCurrentMetadata(metadata.orElseThrow());
                        event.getRelatedTo().setState(IndexEntryState.Valid);
                        log.info("Storing metadata for {}", clientUrl);
                        indexEntryService.saveEntry(event.getRelatedTo());
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
        event.setFinishedNow();
        final Event newEvent = eventRepository.save(event);
        indexEntryService.saveEntry(newEvent.getRelatedTo());
    }

    @Async
    @RequiredEnabledIndexFeature
    public void triggerMetadataRetrieval(Event triggerEvent) {
        log.info("Initiating metadata retrieval for {} (triggered by event {})",
                triggerEvent.getRelatedTo().getClientUrl(), triggerEvent.getUuid());
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
    public Event acceptAdminTrigger(HttpServletRequest request, @Valid PingDTO pingDTO) {
        final String clientUrl = pingDTO.getClientUrl();
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Event event = eventMapper.toAdminTriggerEvent(authentication, clientUrl, request.getRemoteAddr());
        final IndexEntry indexEntry = indexEntryService.getEntryByClientUrl(clientUrl)
                .orElseGet(() -> indexEntryService.createNewEntry(clientUrl));
        event.setRelatedTo(indexEntry);
        event.setFinishedNow();
        return eventRepository.save(event);
    }

    @RequiredEnabledIndexFeature
    public Event acceptAdminTriggerAll(HttpServletRequest request) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Event event = eventMapper.toAdminTriggerEvent(authentication, null, request.getRemoteAddr());
        event.setFinishedNow();
        return eventRepository.save(event);
    }
}
