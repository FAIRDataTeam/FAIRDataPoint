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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.l;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class RdfMigrationLogTest {

    private static final IRI ENTRY_1 = i("https://w3id.org/fdp/fdp-ri-o/system/migrations/1");

    private Repository repository;

    private RdfMigrationLog migrationLog;

    @BeforeEach
    public void setup() {
        repository = new SailRepository(new MemoryStore());
        repository.init();
        migrationLog = new RdfMigrationLog(new SystemGraphStore(repository));
    }

    @AfterEach
    public void teardown() {
        repository.shutDown();
    }

    @Test
    @DisplayName("The log of an installation that never migrated is empty")
    public void emptyLog() {
        assertThat(migrationLog.appliedNumbers(), is(empty()));
    }

    @Test
    @DisplayName("'recordApplied' writes one entry per migration into the migrations graph")
    public void recordApplied() {
        // WHEN:
        migrationLog.recordApplied(1, "Init migration");
        migrationLog.recordApplied(2, "Metadata Draft");

        // THEN: both numbers are read back
        assertThat(migrationLog.appliedNumbers(), is(equalTo(Set.of(1, 2))));

        // AND: the entry of migration 1 is described as specified, in the migrations graph
        final Model entry = entryOfMigrationOne();
        assertThat(entry, hasSize(4));
        assertThat(entry.contains(ENTRY_1, RDF.TYPE, FDPRI.APPLIED_MIGRATION, FDPRI.MIGRATIONS_GRAPH), is(true));
        assertThat(entry.contains(ENTRY_1, DCTERMS.TITLE, null, FDPRI.MIGRATIONS_GRAPH), is(true));
        final Model numberStatements = entry.filter(ENTRY_1, FDPRI.MIGRATION_NUMBER, null);
        final Literal number = (Literal) numberStatements.objects().iterator().next();
        assertThat(number.getDatatype(), is(equalTo(XSD.INTEGER)));
        assertThat(number.intValue(), is(equalTo(1)));
        final Literal appliedAt = (Literal) entry.filter(ENTRY_1, FDPRI.APPLIED_AT, null).objects().iterator().next();
        assertThat(appliedAt.getDatatype(), is(equalTo(XSD.DATETIME)));
    }

    @Test
    @DisplayName("Recording a migration again replaces its entry instead of adding a second one")
    public void recordAppliedTwice() {
        // GIVEN:
        migrationLog.recordApplied(1, "Init migration");

        // WHEN:
        migrationLog.recordApplied(1, "Init migration, renamed");

        // THEN:
        assertThat(migrationLog.appliedNumbers(), is(equalTo(Set.of(1))));
        final Model entry = entryOfMigrationOne();
        assertThat(entry, hasSize(4));
        assertThat(
                entry.filter(ENTRY_1, DCTERMS.TITLE, null).objects().iterator().next().stringValue(),
                is(equalTo("Init migration, renamed"))
        );
    }

    @Test
    @DisplayName("'recordApplied' with a given timestamp stores it instead of the current time")
    public void recordAppliedWithGivenTimestamp() {
        // GIVEN: a moment distinct from "now", as the MongoDB importer would pass on
        final OffsetDateTime originalTimestamp = OffsetDateTime.parse("2020-01-31T12:00:00Z");

        // WHEN:
        migrationLog.recordApplied(1, "Init migration", originalTimestamp);

        // THEN: the entry carries the given timestamp, not the current time
        final Literal appliedAt =
                (Literal) entryOfMigrationOne().filter(ENTRY_1, FDPRI.APPLIED_AT, null).objects().iterator().next();
        assertThat(appliedAt.getDatatype(), is(equalTo(XSD.DATETIME)));
        assertThat(
                appliedAt.stringValue(),
                is(equalTo(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(originalTimestamp)))
        );
    }

    @Test
    @DisplayName("A number that is not a readable integer is ignored rather than failing the start-up")
    public void unreadableNumberIsIgnored() {
        // GIVEN: a log entry whose number was written by something else, as a non-literal value...
        migrationLog.recordApplied(1, "Init migration");
        try (var conn = repository.getConnection()) {
            conn.add(i("https://w3id.org/fdp/fdp-ri-o/system/migrations/x"), FDPRI.MIGRATION_NUMBER,
                    FDPRI.APPLIED_MIGRATION, FDPRI.MIGRATIONS_GRAPH);
            // ...and as a literal that cannot be read as an integer
            conn.add(i("https://w3id.org/fdp/fdp-ri-o/system/migrations/y"), FDPRI.MIGRATION_NUMBER,
                    l("seven"), FDPRI.MIGRATIONS_GRAPH);
        }

        // THEN: the readable entries are still returned
        assertThat(migrationLog.appliedNumbers(), is(equalTo(Set.of(1))));
    }

    private Model entryOfMigrationOne() {
        final Model entry = new LinkedHashModel();
        try (var conn = repository.getConnection()) {
            conn.getStatements(ENTRY_1, null, null, FDPRI.MIGRATIONS_GRAPH).forEach(entry::add);
        }
        return entry;
    }

}
