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
package org.fairdatateam.fairdatapoint.rdf.metadata;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class MetadataStateRepositoryTest extends WebIntegrationTest {

    private static final IRI RECORD_1 = i("http://example.com/record-1");

    private static final IRI RECORD_2 = i("http://example.com/record-2");

    private static final IRI RECORD_3 = i("http://example.com/record-3");

    @Autowired
    private MetadataStateRepository metadataStateRepository;

    @Autowired
    private Repository repository;

    @Test
    @DisplayName("'save' stores the state in the state graph and 'findByUri' reads it back")
    public void saveAndFindByUri() {
        // WHEN:
        metadataStateRepository.save(RECORD_1, MetadataState.DRAFT);

        // THEN:
        assertThat(metadataStateRepository.findByUri(RECORD_1), is(equalTo(Optional.of(MetadataState.DRAFT))));

        // AND: the statement is in the state graph, not in the graph of the record
        try (var conn = repository.getConnection()) {
            assertThat(
                    conn.hasStatement(RECORD_1, FDPRI.HAS_STATE, FDPRI.DRAFT, false, FDPRI.STATE_GRAPH),
                    is(true)
            );
            assertThat(conn.hasStatement(RECORD_1, null, null, false, RECORD_1), is(false));
        }
    }

    @Test
    @DisplayName("'findByUri' returns empty for a record without a state")
    public void findByUriWithoutState() {
        assertThat(metadataStateRepository.findByUri(i("http://example.com/unknown")), is(equalTo(Optional.empty())));
    }

    @Test
    @DisplayName("'save' replaces the previous state of a record")
    public void saveReplacesState() {
        // GIVEN:
        metadataStateRepository.save(RECORD_1, MetadataState.DRAFT);

        // WHEN:
        metadataStateRepository.save(RECORD_1, MetadataState.PUBLISHED);

        // THEN:
        assertThat(metadataStateRepository.findByUri(RECORD_1), is(equalTo(Optional.of(MetadataState.PUBLISHED))));
        try (var conn = repository.getConnection()) {
            assertThat(conn.hasStatement(RECORD_1, FDPRI.HAS_STATE, FDPRI.DRAFT, false, FDPRI.STATE_GRAPH), is(false));
        }
    }

    @Test
    @DisplayName("'findByUris' returns the state of the known records only")
    public void findByUris() {
        // GIVEN:
        metadataStateRepository.save(RECORD_1, MetadataState.DRAFT);
        metadataStateRepository.save(RECORD_2, MetadataState.PUBLISHED);

        // WHEN:
        final Map<IRI, MetadataState> states =
                metadataStateRepository.findByUris(List.of(RECORD_1, RECORD_2, RECORD_3));

        // THEN:
        assertThat(states.size(), is(equalTo(2)));
        assertThat(states, hasEntry(RECORD_1, MetadataState.DRAFT));
        assertThat(states, hasEntry(RECORD_2, MetadataState.PUBLISHED));
    }

    @Test
    @DisplayName("'findByUris' returns an empty map for no records")
    public void findByUrisWithoutRecords() {
        assertThat(metadataStateRepository.findByUris(List.of()), is(anEmptyMap()));
    }

    @Test
    @DisplayName("'delete' removes the state of one record")
    public void delete() {
        // GIVEN:
        metadataStateRepository.save(RECORD_1, MetadataState.DRAFT);
        metadataStateRepository.save(RECORD_2, MetadataState.DRAFT);

        // WHEN:
        metadataStateRepository.delete(RECORD_1);

        // THEN:
        assertThat(metadataStateRepository.findByUri(RECORD_1), is(equalTo(Optional.empty())));
        assertThat(metadataStateRepository.findByUri(RECORD_2), is(equalTo(Optional.of(MetadataState.DRAFT))));
    }

    @Test
    @DisplayName("'deleteAll' empties the state graph")
    public void deleteAll() {
        // GIVEN:
        metadataStateRepository.save(RECORD_1, MetadataState.DRAFT);

        // WHEN:
        metadataStateRepository.deleteAll();

        // THEN:
        assertThat(metadataStateRepository.findByUri(RECORD_1), is(equalTo(Optional.empty())));
        try (var conn = repository.getConnection()) {
            assertThat(conn.hasStatement(null, null, null, false, FDPRI.STATE_GRAPH), is(false));
        }
    }

}
