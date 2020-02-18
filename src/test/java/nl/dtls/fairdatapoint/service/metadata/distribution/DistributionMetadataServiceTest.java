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
package nl.dtls.fairdatapoint.service.metadata.distribution;

import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairmetadata4j.model.DatasetMetadata;
import nl.dtls.fairmetadata4j.model.DistributionMetadata;
import nl.dtls.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DistributionMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    private MetadataService<DistributionMetadata, DistributionMetadataChangeDTO> distributionMetadataService;

    @Test
    public void specsLink() throws Exception {
        // GIVEN:
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        DistributionMetadata metadata = distributionMetadataService.retrieve(distribution.getUri());
        assertNotNull(metadata.getSpecification());
    }

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        assertNotNull(distributionMetadataService.retrieve(distribution.getUri()));
    }

    @Test
    public void storeWithNoParentURI() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();
            distribution.setParentURI(null);

            // WHEN:
            distributionMetadataService.store(distribution);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithWrongParentURI() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
            DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();
            distribution.setParentURI(repository.getUri());

            // WHEN:
            distributionMetadataService.store(distribution);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();
        distribution.setIdentifier(null);

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(distribution.getUri());
        assertNotNull(mdata.getIdentifier());
    }

    @Test
    public void storeWithNoPublisher() throws Exception {
        // GIVEN:
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();
        distribution.setPublisher(null);

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(distribution.getUri());
        assertNotNull(mdata.getPublisher());
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();
        distribution.setLicense(null);

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(distribution.getUri());
        assertNotNull(mdata.getLicense());
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();
        distribution.setLanguage(null);

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(distribution.getUri());
        assertNotNull(mdata.getLanguage());
    }

    @Test
    public void updateParent() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        CatalogMetadata catalog = testMetadataFixtures.catalog1();
        DatasetMetadata dataset = testMetadataFixtures.c1_dataset1();
        DistributionMetadata distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        distributionMetadataService.store(distribution);

        // THEN:
        FDPMetadata updatedRepository = repositoryMetadataService.retrieve(repository.getUri());
        CatalogMetadata updatedCatalog = catalogMetadataService.retrieve(catalog.getUri());
        DatasetMetadata updatedDataset = datasetMetadataService.retrieve(dataset.getUri());
        DistributionMetadata storedDistribution = distributionMetadataService.retrieve(distribution.getUri());
        ZonedDateTime repositoryModified = ZonedDateTime.parse(updatedRepository.getModified().stringValue());
        ZonedDateTime catalogModified = ZonedDateTime.parse(updatedCatalog.getModified().stringValue());
        ZonedDateTime datasetModified = ZonedDateTime.parse(updatedDataset.getModified().stringValue());
        ZonedDateTime distributionModified = ZonedDateTime.parse(storedDistribution.getModified().stringValue());
        assertFalse("Dataset modified is not after Distribution modified",
                datasetModified.isBefore(distributionModified));
        assertFalse("Catalog modified is not after Dataset modified", catalogModified.isBefore(distributionModified));
        assertFalse("FDP modified is not after Dataset modified", repositoryModified.isBefore(distributionModified));
    }

}
