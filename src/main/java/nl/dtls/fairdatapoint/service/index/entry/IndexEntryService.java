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

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDetailDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryInfoDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryStateDTO;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;
import nl.dtls.fairdatapoint.service.index.common.RequiredEnabledIndexFeature;
import nl.dtls.fairdatapoint.service.index.event.EventService;
import nl.dtls.fairdatapoint.service.index.harvester.HarvesterService;
import nl.dtls.fairdatapoint.service.index.settings.IndexSettingsService;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;
import java.util.*;
import java.util.stream.StreamSupport;

import static nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryStateDTO.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
@Service
@Validated
public class IndexEntryService {

    private static final String MSG_NOT_FOUND = "Index entry not found";

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

    @RequiredEnabledIndexFeature
    public Iterable<IndexEntry> getAllEntries() {
        return repository.findAll();
    }

    @RequiredEnabledIndexFeature
    public List<IndexEntryDTO> getAllEntriesAsDTOs() {
        final Instant validThreshold = getValidThreshold();
        return StreamSupport
                .stream(getAllEntries().spliterator(), true)
                .map(entry -> mapper.toDTO(entry, validThreshold))
                .toList();
    }

    @RequiredEnabledIndexFeature
    public Page<IndexEntry> getEntriesPage(Pageable pageable, String state) {
        final Instant validThreshold = getValidThreshold();
        if (state.equalsIgnoreCase(ACTIVE.name())) {
            return repository.findAllByStateEqualsAndLastRetrievalTimeAfter(pageable,
                    IndexEntryState.Valid, validThreshold);
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.INACTIVE.name())) {
            return repository.findAllByStateEqualsAndLastRetrievalTimeBefore(pageable,
                    IndexEntryState.Valid, validThreshold);
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.UNREACHABLE.name())) {
            return repository.findAllByStateEquals(pageable, IndexEntryState.Unreachable);
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.INVALID.name())) {
            return repository.findAllByStateEquals(pageable, IndexEntryState.Invalid);
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.UNKNOWN.name())) {
            return repository.findAllByStateEquals(pageable, IndexEntryState.Unknown);
        }
        return repository.findAll(pageable);
    }

    @RequiredEnabledIndexFeature
    public Page<IndexEntryDTO> getEntriesPageDTOs(Pageable pageable, String state) {
        final Instant validThreshold = getValidThreshold();
        return getEntriesPage(pageable, state)
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
    public IndexEntryInfoDTO getEntriesInfo() {
        final Instant validThreshold = getValidThreshold();
        final Map<String, Long> entriesCount = new HashMap<>();
        entriesCount.put("ALL", repository.count());
        entriesCount.put(UNKNOWN.name(), repository.countAllByStateEquals(IndexEntryState.Unknown));
        entriesCount.put(ACTIVE.name(),
                repository.countAllByStateEqualsAndLastRetrievalTimeAfter(
                        IndexEntryState.Valid, validThreshold));
        entriesCount.put(INACTIVE.name(),
                repository.countAllByStateEqualsAndLastRetrievalTimeBefore(
                        IndexEntryState.Valid, validThreshold));
        entriesCount.put(UNREACHABLE.name(), repository.countAllByStateEquals(
                IndexEntryState.Unreachable));
        entriesCount.put(INVALID.name(), repository.countAllByStateEquals(IndexEntryState.Invalid));
        return new IndexEntryInfoDTO(entriesCount);
    }

    @RequiredEnabledIndexFeature
    public IndexEntry storeEntry(@Valid PingDTO pingDTO) {
        final String clientUrl = pingDTO.getClientUrl();
        final Optional<IndexEntry> entity = repository.findByClientUrl(clientUrl);
        final Instant now = Instant.now();

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
}
