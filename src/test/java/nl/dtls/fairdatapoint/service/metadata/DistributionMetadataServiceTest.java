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
package nl.dtls.fairdatapoint.service.metadata;

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.mongo.fixtures.UserFixtures;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DistributionMetadataServiceTest extends BaseIntegrationTest {
    private final static ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
    private final static String TEST_DISTRIBUTION_URI = "http://example.com/fdp/catalog/dataset/distrubtion";

    @Autowired
    private UserFixtures userFixtures;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Autowired
    private MetadataService<FDPMetadata> fdpMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata> datasetMetadataService;

    @Autowired
    private MetadataService<DistributionMetadata> distributionMetadataService;

    @Before
    public void createParents() throws MetadataServiceException {
        String albertUuid = userFixtures.albert().getUuid();
        Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
        SecurityContextHolder.getContext().setAuthentication(auth);
        fdpMetadataService.store(ExampleFilesUtils.getFDPMetadata(ExampleFilesUtils.FDP_URI));
        catalogMetadataService.store(ExampleFilesUtils.getCatalogMetadata(ExampleFilesUtils.CATALOG_URI,
                ExampleFilesUtils.FDP_URI));
        datasetMetadataService.store(ExampleFilesUtils.getDatasetMetadata(ExampleFilesUtils.DATASET_URI,
                ExampleFilesUtils.CATALOG_URI));
    }

    @DirtiesContext
    @Test
    public void storeAndRetrieve() throws MetadataServiceException {
        // WHEN:
        distributionMetadataService.store(createExampleMetadata());

        // THEN:
        assertNotNull(distributionMetadataService.retrieve(exampleIRI()));
    }

    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeWithNoParentURI() throws Exception {
        // WHEN:
        DistributionMetadata metadata = createExampleMetadata();
        metadata.setParentURI(null);
        distributionMetadataService.store(metadata);

        // THEN:
        // Expect exception
    }

    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeWithWrongParentURI() throws Exception {
        // WHEN:
        distributionMetadataService.store(ExampleFilesUtils.getDistributionMetadata(TEST_DISTRIBUTION_URI,
                ExampleFilesUtils.CATALOG_URI));

        // THEN:
        // Expect exception
    }

    @DirtiesContext
    @Test
    public void storeWithNoID() throws MetadataServiceException {
        // WHEN:
        DistributionMetadata metadata = createExampleMetadata();
        metadata.setIdentifier(null);
        distributionMetadataService.store(metadata);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getIdentifier());
    }

    @DirtiesContext
    @Test
    public void storeWithNoPublisher() throws MetadataServiceException {
        // WHEN:
        DistributionMetadata metadata = createExampleMetadata();
        metadata.setPublisher(null);
        distributionMetadataService.store(metadata);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getPublisher());
    }

    @DirtiesContext
    @Test
    public void storeWithNoLicense() throws MetadataServiceException {
        // WHEN:
        DistributionMetadata metadata = createExampleMetadata();
        metadata.setLicense(null);
        distributionMetadataService.store(metadata);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getLicense());
    }

    @DirtiesContext
    @Test
    public void storeWithNoLanguage() throws MetadataServiceException {
        // WHEN:
        DistributionMetadata metadata = createExampleMetadata();
        metadata.setLanguage(null);
        distributionMetadataService.store(metadata);

        // THEN:
        DistributionMetadata mdata = distributionMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getLanguage());
    }

    @DirtiesContext
    @Test
    public void specsLink() throws Exception {
        // WHEN:
        distributionMetadataService.store(createExampleMetadata());

        // THEN:
        DistributionMetadata metadata = distributionMetadataService.retrieve(exampleIRI());
        assertNotNull(metadata.getSpecification());
    }

    @DirtiesContext
    @Test
    public void updateParent() throws MetadataServiceException {
        // GIVEN:
        FDPMetadata fdpMetadata = ExampleFilesUtils.getFDPMetadata(ExampleFilesUtils.FDP_URI);
        fdpMetadataService.store(fdpMetadata);
        CatalogMetadata catalogMetadata = ExampleFilesUtils.getCatalogMetadata(ExampleFilesUtils.CATALOG_URI,
                ExampleFilesUtils.FDP_URI);
        catalogMetadataService.store(catalogMetadata);
        DatasetMetadata datasetMetadata = ExampleFilesUtils.getDatasetMetadata(ExampleFilesUtils.DATASET_URI,
                ExampleFilesUtils.CATALOG_URI);
        datasetMetadataService.store(datasetMetadata);

        // WHEN:
        DistributionMetadata distributionMetadata =
                ExampleFilesUtils.getDistributionMetadata(ExampleFilesUtils.DISTRIBUTION_URI,
                        ExampleFilesUtils.DATASET_URI);
        distributionMetadataService.store(distributionMetadata);

        // THEN:
        FDPMetadata updatedFdpMetadata =
                fdpMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.FDP_URI));
        CatalogMetadata updatedCatalogMetadata =
                catalogMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
        DatasetMetadata updatedDataset =
                datasetMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
        DistributionMetadata storedDistribution =
                distributionMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.DISTRIBUTION_URI));

        ZonedDateTime fdpModified = ZonedDateTime.parse(updatedFdpMetadata.getModified().stringValue());
        ZonedDateTime catalogModified = ZonedDateTime.parse(updatedCatalogMetadata.getModified().stringValue());
        ZonedDateTime datasetModified = ZonedDateTime.parse(updatedDataset.getModified().stringValue());
        ZonedDateTime distributionModified = ZonedDateTime.parse(storedDistribution.getModified().stringValue());

        assertFalse("Dataset modified is not after Distribution modified",
                datasetModified.isBefore(distributionModified));
        assertFalse("Catalog modified is not after Dataset modified", catalogModified.isBefore(distributionModified));
        assertFalse("FDP modified is not after Dataset modified", fdpModified.isBefore(distributionModified));
    }

    private static DistributionMetadata createExampleMetadata() {
        return ExampleFilesUtils.getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
    }

    private static IRI exampleIRI() {
        return VALUE_FACTORY.createIRI(TEST_DISTRIBUTION_URI);
    }
}
