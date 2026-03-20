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
package nl.dtls.fairdatapoint.database.mongo.repository;

import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryPermit;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntryState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IndexEntryRepository extends MongoRepository<IndexEntry, String> {

    Optional<IndexEntry> findByUuid(String uuid);

    Optional<IndexEntry> findByClientUrl(String clientUrl);

    Page<IndexEntry> findAllByStateEqualsAndPermitIn(
            Pageable pageable, IndexEntryState state, List<IndexEntryPermit> permit
    );

    Page<IndexEntry> findAllByStateEqualsAndLastRetrievalTimeBeforeAndPermitIn(
            Pageable pageable, IndexEntryState state, Instant when, List<IndexEntryPermit> permit
    );

    Page<IndexEntry> findAllByStateEqualsAndLastRetrievalTimeAfterAndPermitIn(
            Pageable pageable, IndexEntryState state, Instant when, List<IndexEntryPermit> permit
    );

    Page<IndexEntry> findAllByPermitIn(Pageable pageable, List<IndexEntryPermit> permit);

    Iterable<IndexEntry> findAllByPermitIn(List<IndexEntryPermit> permit);

    long countAllByPermitIn(List<IndexEntryPermit> permit);

    long countAllByStateEqualsAndPermitIn(IndexEntryState state, List<IndexEntryPermit> permit);

    long countAllByStateEqualsAndLastRetrievalTimeAfterAndPermitIn(
            IndexEntryState state, Instant when, List<IndexEntryPermit> permit);

    long countAllByStateEqualsAndLastRetrievalTimeBeforeAndPermitIn(
            IndexEntryState state, Instant when, List<IndexEntryPermit> permit
    );
}
