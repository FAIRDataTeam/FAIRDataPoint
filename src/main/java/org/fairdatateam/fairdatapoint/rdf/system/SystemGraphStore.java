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
package org.fairdatateam.fairdatapoint.rdf.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Transactional access to the system graph, the named graph in the main repository where the
 * reference implementation keeps its own configuration (see {@link FDPRI#SYSTEM_GRAPH}).
 *
 * <p>Repositories for record state, schemas, resource definitions and settings are built on
 * this class rather than on the raw {@link Repository}, so that all writes to the system graph
 * go through one transaction boundary and all reads are scoped to the graph.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemGraphStore {

    private static final String MSG_READ_FAILED = "Reading the system graph '%s' failed";

    private static final String MSG_WRITE_FAILED = "Writing the system graph '%s' failed";

    private static final String SPARQL_EXCLUSION = "FILTER(!STRSTARTS(STR(?%s), \"%s\"))";

    private final Repository repository;

    /**
     * SPARQL filter that drops the bindings of a graph variable that point at a system graph.
     *
     * <p>Queries over the records have to exclude the system graphs, otherwise the configuration
     * of the implementation shows up in search results. The filter is inlined in the five query
     * files under {@code /sparql} (one per {@code GRAPH} variable each of them binds); this method
     * is the single place that defines its shape. {@code SearchServiceSystemGraphTest} reads those
     * files back and checks every inlined filter against this method, so the two cannot drift
     * apart unnoticed.
     *
     * @param graphVariable name of the SPARQL variable bound by {@code GRAPH}, without the '?'
     * @return the filter expression
     */
    public static String excludeSystemGraphs(String graphVariable) {
        return String.format(SPARQL_EXCLUSION, graphVariable, FDPRI.SYSTEM_GRAPH_PREFIX);
    }

    public IRI graph() {
        return FDPRI.SYSTEM_GRAPH;
    }

    /** Runs a read-only operation on a connection; the operation must scope queries to {@link #graph()}. */
    public <T> T read(Function<RepositoryConnection, T> operation) {
        return read(graph(), operation);
    }

    /** Runs a read-only operation on a connection; the operation must scope queries to {@code graph}. */
    public <T> T read(IRI graph, Function<RepositoryConnection, T> operation) {
        try (RepositoryConnection conn = repository.getConnection()) {
            return operation.apply(conn);
        }
        catch (RepositoryException exception) {
            throw new SystemGraphException(String.format(MSG_READ_FAILED, graph), exception);
        }
    }

    /** Runs a write operation inside one transaction; rolled back if the operation does not commit. */
    public void write(Consumer<RepositoryConnection> operation) {
        write(graph(), operation);
    }

    /** Runs a write operation on {@code graph} inside one transaction; rolled back if it does not commit. */
    public void write(IRI graph, Consumer<RepositoryConnection> operation) {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.begin();
            try {
                operation.accept(conn);
                conn.commit();
            }
            finally {
                // still active means the operation threw before commit: undo its partial writes
                if (conn.isActive()) {
                    conn.rollback();
                }
            }
        }
        catch (RepositoryException exception) {
            throw new SystemGraphException(String.format(MSG_WRITE_FAILED, graph), exception);
        }
    }

    /** Replaces the content of a system graph by the given model in one transaction. */
    public void replace(IRI graph, Model model) {
        write(graph, conn -> {
            conn.clear(graph);
            conn.add(model, graph);
        });
    }

    /** Removes every statement of a system graph. */
    public void clear(IRI graph) {
        write(graph, conn -> conn.clear(graph));
    }

    /** Returns every statement of the system graph, mainly for diagnostics and tests. */
    public Model dump() {
        return dump(graph());
    }

    /** Returns every statement of a system graph. */
    public Model dump(IRI graph) {
        return read(graph, conn -> {
            final Model model = new LinkedHashModel();
            conn.getStatements(null, null, null, graph).forEach(model::add);
            return model;
        });
    }

    /** Whether the system graph holds any statement yet, i.e. whether this store has been seeded. */
    public boolean isEmpty() {
        return read(conn -> !conn.hasStatement(null, null, null, false, graph()));
    }

}
