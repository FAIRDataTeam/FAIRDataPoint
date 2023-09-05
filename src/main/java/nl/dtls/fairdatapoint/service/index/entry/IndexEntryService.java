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
package nl.dtls.fairdatapoint.service.index.entry;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.index.entry.*;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryPermit;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;
import nl.dtls.fairdatapoint.entity.index.settings.IndexSettings;
import nl.dtls.fairdatapoint.service.index.common.RequiredEnabledIndexFeature;
import nl.dtls.fairdatapoint.service.index.event.EventService;
import nl.dtls.fairdatapoint.service.index.harvester.HarvesterService;
import nl.dtls.fairdatapoint.service.index.settings.IndexSettingsService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryStateDTO.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
@Service
@Validated
public class IndexEntryService {

    private static final String MSG_NOT_FOUND = "Index entry not found";

    private static final String FILTER_ALL = "ALL";

    @Autowired
    private IndexEntryRepository repository;

    @Autowired
    private IndexSettingsService indexSettingsService;

    @Autowired
    private EventService eventService;

    @Autowired
    private IndexEntryMapper mapper;

    @Autowired
    private GenericMetadataRepository genericMetadataRepository;

    @Autowired
    private HarvesterService harvesterService;

    @Autowired
    private CurrentUserService currentUserService;

    @RequiredEnabledIndexFeature
    public Iterable<IndexEntry> getAllEntries() {
        return repository.findAll();
    }

    @RequiredEnabledIndexFeature
    public Iterable<IndexEntry> getAllEntries(String permitQuery) {
        final List<IndexEntryPermit> permit = getPermits(permitQuery);
        return repository.findAllByPermitIn(permit);
    }

    @RequiredEnabledIndexFeature
    public List<IndexEntryDTO> getAllEntriesAsDTOs(String permitQuery) {
        final Instant validThreshold = getValidThreshold();
        return StreamSupport
                .stream(getAllEntries(permitQuery).spliterator(), true)
                .map(entry -> mapper.toDTO(entry, validThreshold))
                .toList();
    }

    @RequiredEnabledIndexFeature
    public Page<IndexEntry> getEntriesPage(Pageable pageable, String state, String permitQuery) {
        return getEntriesPageWithPermits(pageable, state, getPermits(permitQuery));
    }

    private List<IndexEntryPermit> getPermits(String permitQuery) {
        if (currentUserService.getCurrentUser().isEmpty()
                || !currentUserService.getCurrentUser().get().isAdmin()) {
            // not admin -> can use just ACCEPTED entries
            return List.of(IndexEntryPermit.ACCEPTED);
        }
        final Set<String> permitStrings = Arrays
                .stream(permitQuery.split(","))
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        if (permitStrings.contains(FILTER_ALL)) {
            return Arrays.stream(IndexEntryPermit.values()).toList();
        }
        return Arrays
                .stream(IndexEntryPermit.values())
                .filter(permitValue -> permitStrings.contains(permitValue.name()))
                .toList();
    }

    private Page<IndexEntry> getEntriesPageWithPermits(Pageable pageable, String state,
                                                       List<IndexEntryPermit> permit) {
        final Instant validThreshold = getValidThreshold();
        if (state.equalsIgnoreCase(ACTIVE.name())) {
            return repository.findAllByStateEqualsAndLastRetrievalTimeAfterAndPermitIn(
                    pageable, IndexEntryState.Valid, validThreshold, permit
            );
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.INACTIVE.name())) {
            return repository.findAllByStateEqualsAndLastRetrievalTimeBeforeAndPermitIn(
                    pageable, IndexEntryState.Valid, validThreshold, permit
            );
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.UNREACHABLE.name())) {
            return repository.findAllByStateEqualsAndPermitIn(
                    pageable, IndexEntryState.Unreachable, permit
            );
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.INVALID.name())) {
            return repository.findAllByStateEqualsAndPermitIn(
                    pageable, IndexEntryState.Invalid, permit
            );
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.UNKNOWN.name())) {
            return repository.findAllByStateEqualsAndPermitIn(
                    pageable, IndexEntryState.Unknown, permit
            );
        }
        return repository.findAllByPermitIn(pageable, permit);
    }

    @RequiredEnabledIndexFeature
    public Page<IndexEntryDTO> getEntriesPageDTOs(Pageable pageable, String state, String permitQuery) {
        final Instant validThreshold = getValidThreshold();
        return getEntriesPage(pageable, state, permitQuery)
                .map(entry -> mapper.toDTO(entry, validThreshold));
    }

    @RequiredEnabledIndexFeature
    public Optional<IndexEntry> getEntry(String uuid) {
        return repository.findByUuid(uuid);
    }

    @RequiredEnabledIndexFeature
    public Optional<IndexEntryDetailDTO> getEntryDetailDTO(String uuid) {
        final Instant validThreshold = getValidThreshold();
        return getEntry(uuid)
                .map(entry -> {
                    return mapper.toDetailDTO(
                            entry, eventService.getEvents(entry.getUuid()), validThreshold
                    );
                });
    }

    @RequiredEnabledIndexFeature
    public IndexEntryInfoDTO getEntriesInfo(String permitQuery) {
        final List<IndexEntryPermit> permit = getPermits(permitQuery);
        final Instant validThreshold = getValidThreshold();
        final Map<String, Long> entriesCount = new HashMap<>();
        entriesCount.put(
                FILTER_ALL,
                repository.countAllByPermitIn(permit)
        );
        entriesCount.put(
                UNKNOWN.name(),
                repository.countAllByStateEqualsAndPermitIn(IndexEntryState.Unknown, permit)
        );
        entriesCount.put(
                ACTIVE.name(),
                repository.countAllByStateEqualsAndLastRetrievalTimeAfterAndPermitIn(
                        IndexEntryState.Valid, validThreshold, permit
                )
        );
        entriesCount.put(
                INACTIVE.name(),
                repository.countAllByStateEqualsAndLastRetrievalTimeBeforeAndPermitIn(
                        IndexEntryState.Valid, validThreshold, permit
                )
        );
        entriesCount.put(
                UNREACHABLE.name(),
                repository.countAllByStateEqualsAndPermitIn(IndexEntryState.Unreachable, permit)
        );
        entriesCount.put(
                INVALID.name(),
                repository.countAllByStateEqualsAndPermitIn(IndexEntryState.Invalid, permit)
        );
        return new IndexEntryInfoDTO(entriesCount);
    }

    @RequiredEnabledIndexFeature
    public IndexEntry storeEntry(@Valid PingDTO pingDTO) {
        final String clientUrl = pingDTO.getClientUrl();
        final Optional<IndexEntry> entity = repository.findByClientUrl(clientUrl);
        final Instant now = Instant.now();
        final IndexSettings settings = indexSettingsService.getOrDefaults();

        final IndexEntry entry;
        if (entity.isPresent()) {
            log.info("Updating timestamp of existing entry {}", clientUrl);
            entry = entity.orElseThrow();
        }
        else {
            log.info("Storing new entry {}", clientUrl);
            entry = new IndexEntry();
            entry.setUuid(UUID.randomUUID().toString());
            entry.setClientUrl(clientUrl);
            entry.setRegistrationTime(now);
            if (settings.getAutoPermit()) {
                entry.setPermit(IndexEntryPermit.ACCEPTED);
            }
            else {
                entry.setPermit(IndexEntryPermit.PENDING);
            }
        }

        entry.setModificationTime(now);
        return repository.save(entry);
    }

    @RequiredEnabledIndexFeature
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEntry(String uuid) throws MetadataRepositoryException {
        final IndexEntry entry = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_NOT_FOUND));
        harvesterService.deleteHarvestedData(entry.getClientUrl());
        repository.delete(entry);
    }

    @Async
    public void harvest(String clientUrl) throws MetadataRepositoryException {
        log.info("Checking index entry for '{}'", clientUrl);
        final Optional<IndexEntry> indexEntry = getEntryByClientUrl(clientUrl);
        if (indexEntry.isEmpty() || indexEntry.get().getPermit() != IndexEntryPermit.ACCEPTED) {
            log.info("Skipping (not ACCEPTED entry) '{}'", clientUrl);
            return;
        }
        harvesterService.harvest(clientUrl);
    }

    private Instant getValidThreshold() {
        return Instant.now()
                .minus(indexSettingsService.getOrDefaults().getPing().getValidDuration());
    }

    @RequiredEnabledIndexFeature
    public Model getEntryHarvestedData(String uuid) throws MetadataRepositoryException {
        final IndexEntry entry = repository
                .findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_NOT_FOUND));
        final Model model = new TreeModel();
        model.addAll(genericMetadataRepository.find(i(entry.getClientUrl())));
        return model;
    }

    public Optional<IndexEntryDetailDTO> updateEntry(String uuid, IndexEntryUpdateDTO reqDto) {
        final Optional<IndexEntry> entry = getEntry(uuid);
        if (entry.isPresent() && !reqDto.getPermit().equals(entry.get().getPermit())) {
            entry.get().setPermit(reqDto.getPermit());
            repository.save(entry.get());
        }
        return getEntryDetailDTO(uuid);
    }

    public Optional<IndexEntry> getEntryByClientUrl(String clientUrl) {
        return repository.findByClientUrl(clientUrl);
    }
}
