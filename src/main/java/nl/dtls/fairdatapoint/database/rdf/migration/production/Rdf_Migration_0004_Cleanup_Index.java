/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.database.rdf.migration.production;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.config.properties.InstanceProperties;
import nl.dtls.fairdatapoint.database.mongo.repository.IndexEntryRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.index.entry.IndexEntry;
import org.fairdatateam.rdf.migration.entity.RdfMigrationAnnotation;
import org.fairdatateam.rdf.migration.runner.RdfProductionMigration;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@RdfMigrationAnnotation(
        number = 4,
        name = "Cleanup Index",
        description = "Cleanup harvested record stored in separate named graphs")
@Slf4j
@Service
public class Rdf_Migration_0004_Cleanup_Index implements RdfProductionMigration {

    @Autowired
    private Repository repository;

    @Autowired
    private GenericMetadataRepository genericMetadataRepository;

    @Autowired
    private String persistentUrl;

    @Autowired
    private IndexEntryRepository indexEntryRepository;

    @Autowired
    private InstanceProperties instanceProperties;

    @Override
    public void runMigration() {
        if (instanceProperties.isIndex()) {
            cleanupHarvestedRecords();
        }
    }

    public void cleanupHarvestedRecords() {
        indexEntryRepository.findAll().forEach(this::cleanupHarvestedRecordsFrom);
    }

    public void cleanupHarvestedRecordsFrom(IndexEntry entry) {
        log.debug("Deleting harvested records for '{}'", entry.getClientUrl());

        if (entry.getCurrentMetadata() == null) {
            log.debug("Deleting harvested records for '{}': no metadata retrieved", entry.getClientUrl());
            return;
        }

        final String repositoryUri = entry.getCurrentMetadata().getRepositoryUri();
        log.debug("Deleting harvested records for '{}': has repository URI '{}'", entry.getClientUrl(), repositoryUri);

        if (repositoryUri.equals(persistentUrl) || repositoryUri.equals(instanceProperties.getClientUrl())) {
            log.debug("Deleting harvested records for '{}': self-referenced repository", entry.getClientUrl());
            return;
        }

        try (RepositoryConnection conn = repository.getConnection()) {
            conn.getContextIDs()
                    .stream()
                    .filter(Value::isIRI)
                    .map(Object::toString)
                    .filter(uri -> isRemoteRepository(uri, repositoryUri, entry) && !isThisInstance(uri))
                    .forEach(contextId -> {
                        log.info("Deleting harvested records for '{}': {}", entry.getClientUrl(), contextId);
                        try {
                            genericMetadataRepository.remove(i(contextId));
                        }
                        catch (MetadataRepositoryException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private boolean isRemoteRepository(String uri, String repositoryUri, IndexEntry entry) {
        return uri.startsWith(repositoryUri) || uri.startsWith(entry.getClientUrl());
    }

    private boolean isThisInstance(String uri) {
        return uri.startsWith(persistentUrl) || uri.startsWith(instanceProperties.getClientUrl());
    }
}
