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
package nl.dtls.fairdatapoint.service.metadata.dataset;

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatasetMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Test
    public void retrieveNonExitingMetadata() {
        assertThrows(ResourceNotFoundException.class, () -> {
            // GIVEN:
            IRI repositoryUri = testMetadataFixtures.repositoryMetadata().getUri();
            IRI datasetUri = i(format("%s/non-existing", repositoryUri));

            // WHEN:
            datasetMetadataService.retrieve(datasetUri);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void existenceDatasetMetaDataSpecsLink() throws Exception {
        // GIVEN:
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        DatasetMetadata metadata = datasetMetadataService.retrieve(dataset.getUri());
        assertNotNull(metadata.getSpecification());
    }

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        assertNotNull(datasetMetadataService.retrieve(dataset.getUri()));
    }

    @Test
    public void storeWithNoParentURI() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
            dataset.setParentURI(null);

            // WHEN:
            datasetMetadataService.store(dataset);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeDatasetMetaDataWrongParentUri() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
            DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
            dataset.setParentURI(repository.getUri());

            // WHEN:
            datasetMetadataService.store(dataset);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
        dataset.setIdentifier(null);

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        DatasetMetadata result = datasetMetadataService.retrieve(dataset.getUri());
        assertNotNull(result.getIdentifier());
    }

    @Test
    public void storeWithNoPublisher() throws Exception {
        // GIVEN:
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
        dataset.setPublisher(null);

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        DatasetMetadata result = datasetMetadataService.retrieve(dataset.getUri());
        assertNotNull(result.getPublisher());
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
        dataset.setLanguage(null);

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        DatasetMetadata result = datasetMetadataService.retrieve(dataset.getUri());
        assertNotNull(result.getLanguage());
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
        dataset.setLicense(null);

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        DatasetMetadata result = datasetMetadataService.retrieve(dataset.getUri());
        assertNotNull(result.getLicense());
    }

    @Test
    public void updateParent() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        CatalogMetadata catalog = testMetadataFixtures.catalog1();
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();

        // WHEN:
        datasetMetadataService.store(dataset);

        // THEN:
        FDPMetadata updatedRepository = repositoryMetadataService.retrieve(repository.getUri());
        CatalogMetadata updatedCatalog = catalogMetadataService.retrieve(catalog.getUri());
        DatasetMetadata updatedDataset = datasetMetadataService.retrieve(dataset.getUri());
        ZonedDateTime repositoryModified = ZonedDateTime.parse(updatedRepository.getModified().stringValue());
        ZonedDateTime catalogModified = ZonedDateTime.parse(updatedCatalog.getModified().stringValue());
        ZonedDateTime datasetModified = ZonedDateTime.parse(updatedDataset.getModified().stringValue());
        assertFalse("Catalog modified is not after Dataset modified", catalogModified.isBefore(datasetModified));
        assertFalse("FDP modified is not after Dataset modified", repositoryModified.isBefore(datasetModified));
    }

}
