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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphException;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Runs the {@link RdfProductionMigration} beans that the {@link RdfMigrationLog} does not list yet.
 *
 * <p>The beans are collected from the application context by their {@link RdfMigration} annotation
 * and run one at a time, in ascending order of their number; each of them is recorded in the log
 * right after it returned. A migration that throws is not recorded and aborts the start-up, so it
 * is run again on the next start.
 *
 * <p>The application context is also checked for two kinds of mistakes that would otherwise fail
 * silently: two migrations sharing the same number, and a bean carrying the {@link RdfMigration}
 * annotation without implementing {@link RdfProductionMigration}. Both abort the start-up with an
 * {@link IllegalStateException}.
 */
@Slf4j
@RequiredArgsConstructor
public class RdfProductionMigrationRunner {

    private static final String MSG_UPGRADE_GUARD = """
            The RDF migration log in the triple store (%s) is empty, but the store already holds \
            metadata records (for instance the named graph <%s>). This is an existing installation \
            whose migration log is still kept in MongoDB, and re-running the RDF migrations on its \
            records could corrupt them. Run the MongoDB import first: it copies the migration log \
            into the triple store, after which this instance starts normally. If this is a fresh \
            installation whose first migration was interrupted, empty the triple store and start \
            again.""";

    private final RdfMigrationLog migrationLog;

    private final Repository repository;

    private final ApplicationContext appContext;

    public void run() {
        log.info("Production migration of the RDF store started");
        final Set<Integer> alreadyApplied = migrationLog.appliedNumbers();
        if (alreadyApplied.isEmpty()) {
            failIfStoreHoldsRecords();
        }
        final List<AnnotatedMigration> migrations = annotatedMigrations();
        final List<Integer> skipped = new ArrayList<>();
        final List<Integer> executed = new ArrayList<>();
        log.info(
                "Production migration of the RDF store: {} migration(s) known, already applied: {}",
                migrations.size(), new TreeSet<>(alreadyApplied)
        );
        for (AnnotatedMigration migration : migrations) {
            if (alreadyApplied.contains(migration.number())) {
                skipped.add(migration.number());
            }
            else {
                apply(migration);
                executed.add(migration.number());
            }
        }
        log.info(
                "Production migration of the RDF store ended; skipped: {}, executed: {}",
                skipped, executed
        );
    }

    /**
     * Runs one migration and records it as applied.
     *
     * <p>Both the migration itself and the recording can fail against the triple store; either way
     * the migration is not recorded, so it is retried on the next start. The failure is re-thrown
     * with the number and name of the migration, since the original exception says nothing about
     * which migration was running.
     *
     * @param migration the migration to run
     * @throws IllegalStateException if the migration or the recording of it fails
     */
    private void apply(AnnotatedMigration migration) {
        log.info("Production migration (n. {}, '{}') started", migration.number(), migration.name());
        try {
            migration.migration().runMigration();
            migrationLog.recordApplied(migration.number(), migration.name());
        }
        catch (RepositoryException | SystemGraphException | IllegalStateException exception) {
            throw new IllegalStateException(
                    "RDF migration %d ('%s') failed; it is not recorded and runs again at the next start"
                            .formatted(migration.number(), migration.name()),
                    exception
            );
        }
        log.info("Production migration (n. {}, '{}') ended", migration.number(), migration.name());
    }

    /**
     * Refuses to migrate an installation whose migration log has not been carried over yet.
     *
     * <p>An empty log on a store that already holds records means that the records were migrated by
     * a 1.x instance, which kept the log in MongoDB: migration 1 would seed the factory defaults
     * again and the later ones would rewrite existing statements. A fresh installation has an empty
     * store as well as an empty log and is migrated normally.
     *
     * @throws IllegalStateException if the store holds a record graph while the log is empty
     */
    private void failIfStoreHoldsRecords() {
        // TODO: the MongoDB importer (PR 16) has to start with this guard disabled while it copies
        //  the log; it adds the property 'migration.rdf.skip-upgrade-guard' for that. The property
        //  is deliberately not read here yet.
        final Optional<Resource> recordGraph = firstRecordGraph();
        if (recordGraph.isPresent()) {
            throw new IllegalStateException(String.format(
                    MSG_UPGRADE_GUARD, FDPRI.MIGRATIONS_GRAPH, recordGraph.get()
            ));
        }
    }

    /**
     * First named graph of the main repository that is not a system graph, i.e. that holds records.
     *
     * @return the graph, or empty if the store holds no record at all
     */
    private Optional<Resource> firstRecordGraph() {
        try (RepositoryConnection conn = repository.getConnection()) {
            try (RepositoryResult<Resource> contexts = conn.getContextIDs()) {
                return contexts
                        .stream()
                        .filter(context -> !FDPRI.isSystemGraph(context))
                        .findFirst();
            }
        }
    }

    /**
     * The migration beans of the application context, in ascending order of their number.
     *
     * @return the migrations, each paired with the values of its annotation
     * @throws IllegalStateException if a bean carries {@link RdfMigration} without implementing
     *     {@link RdfProductionMigration}, if the annotation cannot be read back from a bean that
     *     carries it, or if two migrations share the same number
     */
    private List<AnnotatedMigration> annotatedMigrations() {
        final List<AnnotatedMigration> migrations = appContext
                .getBeansWithAnnotation(RdfMigration.class)
                .entrySet()
                .stream()
                .map(entry -> toAnnotatedMigration(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(AnnotatedMigration::number))
                .toList();
        failOnDuplicateNumbers(migrations);
        return migrations;
    }

    /**
     * Pairs one migration bean with its annotation.
     *
     * @param beanName name of the bean in the application context, used to report a problem
     * @param bean the bean, possibly wrapped in a proxy
     * @return the pair
     * @throws IllegalStateException if the bean does not implement {@link RdfProductionMigration},
     *     or if the annotation cannot be read back from it
     */
    private AnnotatedMigration toAnnotatedMigration(String beanName, Object bean) {
        if (!(bean instanceof RdfProductionMigration migration)) {
            throw new IllegalStateException(
                    "Bean '%s' of class %s carries @RdfMigration but does not implement %s".formatted(
                            beanName, bean.getClass().getName(), RdfProductionMigration.class.getName()
                    )
            );
        }
        final RdfMigration annotation = appContext.findAnnotationOnBean(beanName, RdfMigration.class);
        if (annotation == null) {
            throw new IllegalStateException(
                    "Bean '%s' carries @RdfMigration, but the annotation could not be read back from it"
                            .formatted(beanName)
            );
        }
        return new AnnotatedMigration(annotation.number(), annotation.name(), migration);
    }

    /**
     * Fails loudly on two migrations sharing the same number, which would otherwise silently drop
     * one of them from the log and cause the other to be re-applied on every start.
     *
     * @param migrations the migrations, sorted in ascending order of their number
     * @throws IllegalStateException if two adjacent migrations share their number
     */
    private static void failOnDuplicateNumbers(List<AnnotatedMigration> migrations) {
        for (int index = 1; index < migrations.size(); index++) {
            final AnnotatedMigration previous = migrations.get(index - 1);
            final AnnotatedMigration current = migrations.get(index);
            if (previous.number() == current.number()) {
                throw new IllegalStateException(
                        "Migration number %d is used by both %s and %s".formatted(
                                current.number(),
                                previous.migration().getClass().getName(),
                                current.migration().getClass().getName()
                        )
                );
            }
        }
    }

    /** A migration bean together with the number and the name it is logged under. */
    private record AnnotatedMigration(int number, String name, RdfProductionMigration migration) {
    }

}
