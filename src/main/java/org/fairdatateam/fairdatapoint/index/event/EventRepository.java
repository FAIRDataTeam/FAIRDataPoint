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

import org.fairdatateam.fairdatapoint.index.entry.IndexEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> getAllByType(EventType type);

    // The entry is read from every one of these events, at start-up, without a session left to
    // fetch it lazily; the graph turns that into one join instead of one secondary select per
    // event.
    @EntityGraph(attributePaths = "relatedTo")
    List<Event> getAllByFinishedIsNull();

    @EntityGraph(attributePaths = "relatedTo")
    Page<Event> getAllByRelatedTo(IndexEntry indexEntry, Pageable pageable);

    /**
     * Counts what a caller has done recently, for the rate limit on incoming pings. The address
     * used to be read out of the ping payload; it is now copied into a column of its own when the
     * event is written, so that this stays an indexed lookup rather than a search through JSON.
     *
     * @param type only events of this type count
     * @param remoteAddr the address the events came from
     * @param after only events created after this moment count
     * @return how many matching events there are
     */
    long countByTypeAndRemoteAddrAndCreatedAfter(EventType type, String remoteAddr, Instant after);
}
