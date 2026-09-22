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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.helpers.NTriplesUtil;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * State (draft or published) of the metadata records, kept in the state sub-graph of the system
 * graph as {@code <record> fdp-ri:hasState fdp-ri:Draft|fdp-ri:Published}.
 *
 * <p>Nothing is written to the graph of the record itself, so the RDF served for a record is not
 * affected by its state.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataStateRepository {

    private static final String VAR_RECORD = "record";

    private static final String VAR_STATE = "state";

    /**
     * Maximum number of IRIs bound in one {@code VALUES} block. Chunking keeps the query string,
     * and the triple store's parsing of it, bounded regardless of how many records are asked for.
     */
    private static final int VALUES_CHUNK_SIZE = 500;

    private static final String FIND_BY_URIS = """
            SELECT ?record ?state
            WHERE {
                VALUES ?record { %s }
                GRAPH <%s> {
                    ?record <%s> ?state .
                }
            }""";

    /**
     * Characters (and whitespace) that must not appear in an IRI interpolated into a SPARQL
     * {@code VALUES} block. {@link NTriplesUtil#toNTriplesString} does not escape '>', and the
     * {@code SimpleValueFactory} used to build an {@link IRI} does not validate it either, so a
     * crafted value could otherwise close the IRIref early and inject arbitrary SPARQL.
     */
    private static final Pattern FORBIDDEN_IRI_CHARACTERS = Pattern.compile("[<>\"{}|^`\\\\]|\\s");

    private static final Map<MetadataState, IRI> STATE_IRIS = Map.of(
            MetadataState.DRAFT, FDPRI.DRAFT,
            MetadataState.PUBLISHED, FDPRI.PUBLISHED
    );

    private static final Map<IRI, MetadataState> STATES_BY_IRI = STATE_IRIS
            .entrySet()
            .stream()
            .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));

    private final SystemGraphStore systemGraphStore;

    public Optional<MetadataState> findByUri(IRI uri) {
        return systemGraphStore.read(FDPRI.STATE_GRAPH, conn -> {
            try (RepositoryResult<Statement> statements =
                         conn.getStatements(uri, FDPRI.HAS_STATE, null, FDPRI.STATE_GRAPH)) {
                final List<MetadataState> states = statements
                        .stream()
                        .map(Statement::getObject)
                        .map(MetadataStateRepository::toState)
                        .flatMap(Optional::stream)
                        .toList();
                if (states.size() > 1) {
                    log.warn(
                            "The state graph holds {} states for record '{}'; the first one is used",
                            states.size(), uri
                    );
                }
                return states.stream().findFirst();
            }
        });
    }

    /**
     * States of the given records, in chunks of at most {@value #VALUES_CHUNK_SIZE} records per
     * query; records without a state are absent from the map.
     *
     * @param uris the records to look up
     * @return the state of each record that has one
     */
    public Map<IRI, MetadataState> findByUris(Collection<IRI> uris) {
        if (uris.isEmpty()) {
            return Map.of();
        }
        final List<IRI> distinctUris = uris.stream().distinct().toList();
        final Map<IRI, MetadataState> states = new HashMap<>();
        for (List<IRI> chunk : chunk(distinctUris)) {
            states.putAll(findByUrisChunk(chunk));
        }
        return states;
    }

    private Map<IRI, MetadataState> findByUrisChunk(List<IRI> uris) {
        final String values = uris
                .stream()
                .map(uri -> {
                    validateForValuesBlock(uri);
                    return NTriplesUtil.toNTriplesString(uri);
                })
                .collect(joining(" "));
        final String query = String.format(
                FIND_BY_URIS, values, FDPRI.STATE_GRAPH.stringValue(), FDPRI.HAS_STATE.stringValue()
        );
        return systemGraphStore.read(FDPRI.STATE_GRAPH, conn -> {
            final Map<IRI, MetadataState> states = new HashMap<>();
            try (TupleQueryResult result = conn.prepareTupleQuery(query).evaluate()) {
                for (BindingSet bindings : result) {
                    toState(bindings.getValue(VAR_STATE)).ifPresent(state -> {
                        states.put((IRI) bindings.getValue(VAR_RECORD), state);
                    });
                }
            }
            return states;
        });
    }

    public void save(IRI uri, MetadataState state) {
        saveAll(Map.of(uri, state));
    }

    /**
     * States of several records at once, saved in one transaction.
     *
     * @param states the state to set for each record
     */
    public void saveAll(Map<IRI, MetadataState> states) {
        systemGraphStore.write(FDPRI.STATE_GRAPH, conn -> {
            states.forEach((uri, state) -> {
                conn.remove(uri, FDPRI.HAS_STATE, null, FDPRI.STATE_GRAPH);
                conn.add(uri, FDPRI.HAS_STATE, STATE_IRIS.get(state), FDPRI.STATE_GRAPH);
            });
        });
    }

    public void delete(IRI uri) {
        systemGraphStore.write(
                FDPRI.STATE_GRAPH,
                conn -> conn.remove(uri, FDPRI.HAS_STATE, null, FDPRI.STATE_GRAPH)
        );
    }

    public void deleteAll() {
        systemGraphStore.clear(FDPRI.STATE_GRAPH);
    }

    private static Optional<MetadataState> toState(Value value) {
        if (value instanceof IRI iri) {
            return Optional.ofNullable(STATES_BY_IRI.get(iri));
        }
        return Optional.empty();
    }

    /**
     * Rejects an IRI that could break out of the {@code VALUES} block once interpolated as an
     * N-Triples term (see {@link #FORBIDDEN_IRI_CHARACTERS}).
     *
     * @param uri the IRI to validate
     * @throws IllegalArgumentException if the IRI contains a character forbidden in this context
     */
    private static void validateForValuesBlock(IRI uri) {
        if (FORBIDDEN_IRI_CHARACTERS.matcher(uri.stringValue()).find()) {
            throw new IllegalArgumentException("IRI cannot be safely embedded in a SPARQL query: " + uri);
        }
    }

    /**
     * Splits a list into chunks of at most {@value #VALUES_CHUNK_SIZE} elements.
     *
     * @param uris the list to split
     * @return the chunks, in the original order
     */
    private static List<List<IRI>> chunk(List<IRI> uris) {
        final List<List<IRI>> chunks = new ArrayList<>();
        for (int start = 0; start < uris.size(); start += VALUES_CHUNK_SIZE) {
            chunks.add(uris.subList(start, Math.min(start + VALUES_CHUNK_SIZE, uris.size())));
        }
        return chunks;
    }

}
