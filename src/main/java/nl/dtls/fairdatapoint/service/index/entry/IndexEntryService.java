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

import lombok.extern.log4j.Log4j2;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryInfoDTO;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryStateDTO;
import nl.dtls.fairdatapoint.api.dto.index.ping.PingDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.entity.index.config.EventsConfig;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryStateDTO.*;

@Service
@Validated
@Log4j2
public class IndexEntryService {

    @Autowired
    private IndexEntryRepository repository;

    @Autowired
    private EventsConfig eventsConfig;

    public Iterable<IndexEntry> getAllEntries() {
        return repository.findAll();
    }

    public Page<IndexEntry> getEntriesPage(Pageable pageable, String state) {
        if (state.equalsIgnoreCase(ACTIVE.name())) {
            return repository.findAllByStateEqualsAndLastRetrievalTimeAfter(pageable, IndexEntryState.Valid,
                    getValidThreshold());
        }
        if (state.equalsIgnoreCase(IndexEntryStateDTO.INACTIVE.name())) {
            return repository.findAllByStateEqualsAndLastRetrievalTimeBefore(pageable, IndexEntryState.Valid,
                    getValidThreshold());
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

    public Optional<IndexEntry> getEntry(String uuid) {
        return repository.findByUuid(uuid);
    }

    public IndexEntryInfoDTO getEntriesInfo() {
        Map<String, Long> entriesCount = new HashMap<>();
        entriesCount.put("ALL", repository.count());
        entriesCount.put(UNKNOWN.name(), repository.countAllByStateEquals(IndexEntryState.Unknown));
        entriesCount.put(ACTIVE.name(),
                repository.countAllByStateEqualsAndLastRetrievalTimeAfter(IndexEntryState.Valid, getValidThreshold()));
        entriesCount.put(INACTIVE.name(),
                repository.countAllByStateEqualsAndLastRetrievalTimeBefore(IndexEntryState.Valid, getValidThreshold()));
        entriesCount.put(UNREACHABLE.name(), repository.countAllByStateEquals(IndexEntryState.Unreachable));
        entriesCount.put(INVALID.name(), repository.countAllByStateEquals(IndexEntryState.Invalid));
        return new IndexEntryInfoDTO(entriesCount);
    }

    public IndexEntry storeEntry(@Valid PingDTO pingDTO) {
        var clientUrl = pingDTO.getClientUrl();
        var entity = repository.findByClientUrl(clientUrl);
        var now = Instant.now();

        final IndexEntry entry;
        if (entity.isPresent()) {
            log.info("Updating timestamp of existing entry {}", clientUrl);
            entry = entity.orElseThrow();
        } else {
            log.info("Storing new entry {}", clientUrl);
            entry = new IndexEntry();
            entry.setUuid(UUID.randomUUID().toString());
            entry.setClientUrl(clientUrl);
            entry.setRegistrationTime(now);
        }

        entry.setModificationTime(now);
        return repository.save(entry);
    }

    private Instant getValidThreshold() {
        return Instant.now().minus(eventsConfig.getPingValidDuration());
    }

}
