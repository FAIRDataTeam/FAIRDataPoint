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
package org.fairdatateam.fairdatapoint.migration.triplestore.production;

import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataState;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataStateRepository;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.fairdatateam.rdf.migration.entity.RdfMigrationAnnotation;
import org.fairdatateam.rdf.migration.runner.RdfProductionMigration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RdfMigrationAnnotation(
        number = 2,
        name = "Metadata Draft",
        description = "Support metadata in DRAFT state")
@Slf4j
@Service
public class Rdf_Migration_0002_Metadata_Draft implements RdfProductionMigration {

    @Autowired
    private Repository repository;

    @Autowired
    private MetadataStateRepository metadataStateRepository;

    public void runMigration() {
        createRepositoryInTripleStore();
    }

    private void createRepositoryInTripleStore() {
        // the contexts are collected first: the state is written to the same repository, and
        // writing while the iteration over the contexts is still open is not safe in every store
        final List<IRI> records = new ArrayList<>();
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.getContextIDs()
                    .stream()
                    .filter(IRI.class::isInstance)
                    .map(IRI.class::cast)
                    // the system graphs hold the configuration of the implementation, not records
                    .filter(context -> !context.stringValue().startsWith(FDPRI.SYSTEM_GRAPH_PREFIX))
                    .forEach(records::add);
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
            return;
        }
        metadataStateRepository.saveAll(
                records.stream().collect(Collectors.toMap(recordUri -> recordUri, recordUri -> MetadataState.PUBLISHED))
        );
    }

}
