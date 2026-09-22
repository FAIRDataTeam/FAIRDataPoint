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
package org.fairdatateam.fairdatapoint.search;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SearchSavedQueryRepository extends JpaRepository<SearchSavedQuery, UUID> {

    // The owner is mapped lazily but the mapper reads its identifier after the query has
    // returned, and open-in-view is disabled, so it has to be loaded while the query runs.
    @EntityGraph(attributePaths = "user")
    Optional<SearchSavedQuery> findByUuid(UUID uuid);

    /**
     * Looks a saved query up by the string form of its identifier, as it arrives from the REST
     * API. A malformed identifier is treated as "no such saved query" instead of an error.
     *
     * @param uuid identifier in string form, possibly malformed
     * @return the saved query, or empty if the identifier is malformed or unknown
     */
    default Optional<SearchSavedQuery> findByUuid(String uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        try {
            return findByUuid(UUID.fromString(uuid));
        }
        catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    // Mongo, the previous store, listed saved queries in insertion order; Postgres makes no such
    // guarantee, so the order the API reports has to be pinned explicitly. Ordering by creation
    // time with the identifier as a tie-breaker gives a stable order, though not strictly
    // insertion order for two queries created within the same clock tick.
    @EntityGraph(attributePaths = "user")
    List<SearchSavedQuery> findAllByOrderByCreatedAtAscUuidAsc();

}
