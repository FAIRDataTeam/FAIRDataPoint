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
package org.fairdatateam.fairdatapoint.migration.triplestore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.l;

/**
 * Log of the RDF migrations that were applied to this installation, kept in the migrations
 * sub-graph of the system graph.
 *
 * <p>One entry per applied migration, e.g. for migration 2:
 *
 * <pre>
 * &lt;https://w3id.org/fdp/fdp-ri-o/system/migrations/2&gt;
 *     a fdp-ri:AppliedMigration ;
 *     fdp-ri:migrationNumber 2 ;
 *     dcterms:title "Metadata Draft" ;
 *     fdp-ri:appliedAt "2026-01-31T12:00:00Z"^^xsd:dateTime .
 * </pre>
 *
 * <p>Up to version 1.x this log lived in the MongoDB collection {@code rdfMigration}; keeping it
 * next to the data it describes means that the triple store alone tells which migrations its
 * content has been through.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RdfMigrationLog {

    private final SystemGraphStore systemGraphStore;

    /**
     * Numbers of the migrations that have been applied, in no particular order.
     *
     * @return the applied numbers; empty for an installation that never ran a migration
     */
    public Set<Integer> appliedNumbers() {
        return systemGraphStore.read(FDPRI.MIGRATIONS_GRAPH, conn -> {
            try (RepositoryResult<Statement> statements =
                         conn.getStatements(null, FDPRI.MIGRATION_NUMBER, null, FDPRI.MIGRATIONS_GRAPH)) {
                return statements
                        .stream()
                        .map(Statement::getObject)
                        .map(RdfMigrationLog::toNumber)
                        .flatMap(Optional::stream)
                        .collect(toUnmodifiableSet());
            }
        });
    }

    /**
     * Records a migration as applied at the current time; replaces the entry if the number is
     * already logged.
     *
     * @param number number of the migration, see {@link RdfMigration#number()}
     * @param name name of the migration, see {@link RdfMigration#name()}
     */
    public void recordApplied(int number, String name) {
        recordApplied(number, name, OffsetDateTime.now());
    }

    /**
     * Records a migration as applied at the given time; replaces the entry if the number is
     * already logged.
     *
     * <p>Used by the MongoDB importer (PR 16) to preserve the moment an already-applied migration
     * was originally recorded, rather than stamping it with the moment of the import.
     *
     * @param number number of the migration, see {@link RdfMigration#number()}
     * @param name name of the migration, see {@link RdfMigration#name()}
     * @param appliedAt moment at which the migration was applied
     */
    public void recordApplied(int number, String name, OffsetDateTime appliedAt) {
        final IRI entry = entry(number);
        systemGraphStore.write(FDPRI.MIGRATIONS_GRAPH, conn -> {
            conn.remove(entry, null, null, FDPRI.MIGRATIONS_GRAPH);
            conn.add(entry, RDF.TYPE, FDPRI.APPLIED_MIGRATION, FDPRI.MIGRATIONS_GRAPH);
            conn.add(entry, FDPRI.MIGRATION_NUMBER, l(number), FDPRI.MIGRATIONS_GRAPH);
            conn.add(entry, DCTERMS.TITLE, l(name), FDPRI.MIGRATIONS_GRAPH);
            conn.add(entry, FDPRI.APPLIED_AT, l(appliedAt), FDPRI.MIGRATIONS_GRAPH);
        });
    }

    /**
     * IRI of the log entry of one migration.
     *
     * @param number number of the migration
     * @return the IRI of the entry, a resource inside the migrations graph
     */
    private static IRI entry(int number) {
        return i(FDPRI.MIGRATIONS_GRAPH.stringValue() + "/" + number);
    }

    /**
     * Reads the number of a log entry, ignoring a value that is not an integer literal.
     *
     * @param value object of a {@code fdp-ri:migrationNumber} statement
     * @return the number, or empty if the value cannot be read as one
     */
    private static Optional<Integer> toNumber(Value value) {
        if (!(value instanceof Literal literal)) {
            log.warn("The migration log holds a non-literal migration number '{}'; it is ignored", value);
            return Optional.empty();
        }
        try {
            return Optional.of(literal.intValue());
        }
        catch (NumberFormatException exception) {
            log.warn("The migration log holds an unreadable migration number '{}'; it is ignored", literal);
            return Optional.empty();
        }
    }

}
