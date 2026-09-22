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
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

// The runner is built by TripleStoreMigrationConfig and runs on the start-up thread, before the
// application is up: it is tested here without a Spring Boot context, on an in-memory triple store
// and a small application context holding the two migrations below.
public class RdfProductionMigrationRunnerTest {

    private static final List<Integer> EXECUTED = new ArrayList<>();

    private static final IRI RECORD_GRAPH = i("http://localhost:8080/catalog/some-catalog");

    private Repository repository;

    private RdfMigrationLog migrationLog;

    private AnnotationConfigApplicationContext appContext;

    @BeforeEach
    public void setup() {
        EXECUTED.clear();
        repository = new SailRepository(new MemoryStore());
        repository.init();
        migrationLog = new RdfMigrationLog(new SystemGraphStore(repository));
        appContext = new AnnotationConfigApplicationContext();
        // registered out of order on purpose: the runner orders them by their number
        appContext.register(SecondMigration.class, FirstMigration.class);
        appContext.refresh();
    }

    @AfterEach
    public void teardown() {
        appContext.close();
        repository.shutDown();
    }

    @Test
    @DisplayName("A fresh installation runs every migration in order and records each of them")
    public void runsEveryMigrationOfAFreshInstallation() {
        // WHEN:
        runner().run();

        // THEN:
        assertThat(EXECUTED, is(equalTo(List.of(1, 2))));
        assertThat(migrationLog.appliedNumbers(), is(equalTo(Set.of(1, 2))));
    }

    @Test
    @DisplayName("Only the migrations that the log does not list yet are run")
    public void runsOnlyTheUnappliedMigrations() {
        // GIVEN:
        migrationLog.recordApplied(1, "First");

        // WHEN:
        runner().run();

        // THEN:
        assertThat(EXECUTED, is(equalTo(List.of(2))));
        assertThat(migrationLog.appliedNumbers(), is(equalTo(Set.of(1, 2))));
    }

    @Test
    @DisplayName("Nothing is run twice when every migration is already recorded")
    public void runsNothingWhenEverythingIsApplied() {
        // GIVEN:
        migrationLog.recordApplied(1, "First");
        migrationLog.recordApplied(2, "Second");

        // WHEN:
        runner().run();

        // THEN:
        assertThat(EXECUTED, is(empty()));
    }

    @Test
    @DisplayName("An empty log on a store that already holds records aborts the start-up")
    public void refusesToMigrateAnInstallationWithoutItsLog() {
        // GIVEN: a store holding a record, as an instance upgraded from 1.x does
        try (var conn = repository.getConnection()) {
            conn.add(RECORD_GRAPH, FDPRI.HAS_STATE, FDPRI.PUBLISHED, RECORD_GRAPH);
        }

        // WHEN: the runner starts with an empty log, THEN: it refuses and says what to do
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> runner().run());
        assertThat(exception.getMessage(), containsString("Run the MongoDB import first"));
        assertThat(exception.getMessage(), containsString(RECORD_GRAPH.stringValue()));

        // AND: no migration was run and nothing was recorded
        assertThat(EXECUTED, is(empty()));
        assertThat(migrationLog.appliedNumbers(), is(empty()));
    }

    @Test
    @DisplayName("The guard ignores the system graphs: a store holding only those is a fresh install")
    public void migratesAStoreThatHoldsOnlySystemGraphs() {
        // GIVEN: a store whose only named graphs are system graphs
        try (var conn = repository.getConnection()) {
            conn.add(RECORD_GRAPH, FDPRI.HAS_STATE, FDPRI.PUBLISHED, FDPRI.STATE_GRAPH);
            conn.add(RECORD_GRAPH, FDPRI.HAS_STATE, FDPRI.PUBLISHED, FDPRI.SYSTEM_GRAPH);
        }

        // WHEN:
        runner().run();

        // THEN:
        assertThat(EXECUTED, is(equalTo(List.of(1, 2))));
    }

    @Test
    @DisplayName("A failing migration aborts the start-up and is not recorded")
    public void doesNotRecordAFailingMigration() {
        // GIVEN: a context whose only migration throws
        appContext.close();
        appContext = new AnnotationConfigApplicationContext(FailingMigration.class);

        // WHEN: the runner starts, THEN: the failure propagates and names the migration
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> runner().run());
        assertThat(exception.getMessage(), containsString("3"));
        assertThat(exception.getMessage(), containsString("Failing"));

        // AND: the migration is not recorded, so it is run again on the next start
        assertThat(migrationLog.appliedNumbers(), is(empty()));
    }

    @Test
    @DisplayName("Two migrations sharing the same number abort the start-up")
    public void duplicateNumberAbortsTheStartUp() {
        // GIVEN: two migrations registered under the same number
        appContext.close();
        appContext = new AnnotationConfigApplicationContext(DuplicateNumberConfig.class);

        // WHEN: the runner starts, THEN: it names the number and both classes
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> runner().run());
        assertThat(exception.getMessage(), containsString("9"));
        assertThat(exception.getMessage(), containsString(DuplicateFirst.class.getName()));
        assertThat(exception.getMessage(), containsString(DuplicateSecond.class.getName()));
    }

    @Test
    @DisplayName("An annotated bean that is not a RdfProductionMigration aborts the start-up")
    public void nonMigrationBeanAbortsTheStartUp() {
        // GIVEN: a bean carrying @RdfMigration without implementing RdfProductionMigration
        appContext.close();
        appContext = new AnnotationConfigApplicationContext(NotAMigrationConfig.class);

        // WHEN: the runner starts, THEN: it names the offending bean's class
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> runner().run());
        assertThat(exception.getMessage(), containsString(NotAMigration.class.getName()));
    }

    private RdfProductionMigrationRunner runner() {
        return new RdfProductionMigrationRunner(migrationLog, repository, appContext);
    }

    @RdfMigration(number = 1, name = "First", description = "The first migration")
    static class FirstMigration implements RdfProductionMigration {

        @Override
        public void runMigration() {
            EXECUTED.add(1);
        }

    }

    @RdfMigration(number = 2, name = "Second", description = "The second migration")
    static class SecondMigration implements RdfProductionMigration {

        @Override
        public void runMigration() {
            EXECUTED.add(2);
        }

    }

    @RdfMigration(number = 3, name = "Failing", description = "A migration that cannot finish")
    static class FailingMigration implements RdfProductionMigration {

        @Override
        public void runMigration() {
            throw new RepositoryException("migration refused");
        }

    }

    @Configuration
    static class DuplicateNumberConfig {

        @Bean
        DuplicateFirst duplicateFirst() {
            return new DuplicateFirst();
        }

        @Bean
        DuplicateSecond duplicateSecond() {
            return new DuplicateSecond();
        }

    }

    @RdfMigration(number = 9, name = "Duplicate first", description = "Shares its number with DuplicateSecond")
    static class DuplicateFirst implements RdfProductionMigration {

        @Override
        public void runMigration() {
        }

    }

    @RdfMigration(number = 9, name = "Duplicate second", description = "Shares its number with DuplicateFirst")
    static class DuplicateSecond implements RdfProductionMigration {

        @Override
        public void runMigration() {
        }

    }

    @Configuration
    static class NotAMigrationConfig {

        @Bean
        NotAMigration notAMigration() {
            return new NotAMigration();
        }

    }

    @RdfMigration(number = 10, name = "Not a migration", description = "Carries the annotation but implements nothing")
    static class NotAMigration {
    }

}
