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
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.mongo.fixtures.UserFixtures;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatasetMetadataServiceTest extends BaseIntegrationTest {
    private final static ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
    private final static String TEST_DATASET_URI = "http://example.com/fdp/catalog/dataset";

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

    @BeforeEach
    public void createParents() throws MetadataServiceException {
        String albertUuid = userFixtures.albert().getUuid();
        Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
        SecurityContextHolder.getContext().setAuthentication(auth);
        fdpMetadataService.store(ExampleFilesUtils.getFDPMetadata(ExampleFilesUtils.FDP_URI));
        catalogMetadataService.store(ExampleFilesUtils.getCatalogMetadata(ExampleFilesUtils.CATALOG_URI,
                ExampleFilesUtils.FDP_URI));
    }

    @DirtiesContext
    @Test
    public void storeAndRetrieve() throws MetadataServiceException {
        // WHEN:
        datasetMetadataService.store(createExampleMetadata());

        // THEN:
        assertNotNull(datasetMetadataService.retrieve(exampleIRI()));
    }

    @DirtiesContext
    @Test
    public void storeWithNoParentURI() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            // WHEN:
            DatasetMetadata metadata = createExampleMetadata();
            metadata.setParentURI(null);
            datasetMetadataService.store(metadata);

            // THEN:
            // Expect exception
        });
    }

    @DirtiesContext
    @Test
    public void storeDatasetMetaDataWrongParentUri() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            // WHEN:
            datasetMetadataService.store(ExampleFilesUtils.getDatasetMetadata(TEST_DATASET_URI,
                    ExampleFilesUtils.FDP_URI));

            // THEN:
            // Expect exception
        });
    }

    @DirtiesContext
    @Test
    public void storeWithNoID() throws MetadataServiceException {
        // WHEN:
        DatasetMetadata metadata = createExampleMetadata();
        metadata.setIdentifier(null);
        datasetMetadataService.store(metadata);

        // THEN:
        DatasetMetadata mdata = datasetMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getIdentifier());
    }

    @DirtiesContext
    @Test
    public void storeWithNoPublisher() throws MetadataServiceException {
        // WHEN:
        DatasetMetadata metadata = createExampleMetadata();
        metadata.setPublisher(null);
        datasetMetadataService.store(metadata);

        // THEN:
        DatasetMetadata mdata = datasetMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getPublisher());
    }

    @DirtiesContext
    @Test
    public void storeWithNoLanguage() throws MetadataServiceException {
        // WHEN:
        DatasetMetadata metadata = createExampleMetadata();
        metadata.setLanguage(null);
        datasetMetadataService.store(metadata);

        // THEN:
        DatasetMetadata mdata = datasetMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getLanguage());
    }

    @DirtiesContext
    @Test
    public void storeWithNoLicense() throws MetadataServiceException {
        // WHEN:
        DatasetMetadata metadata = createExampleMetadata();
        metadata.setLicense(null);
        datasetMetadataService.store(metadata);

        // THEN:
        DatasetMetadata mdata = datasetMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getLicense());
    }

    @DirtiesContext
    @Test
    public void retrieveNonExitingMetadata() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            // WHEN:
            String uri = ExampleFilesUtils.CATALOG_URI + "/dummpID676";
            datasetMetadataService.retrieve(VALUE_FACTORY.createIRI(uri));

            // THEN:
            // Expect exception
        });
    }

    @DirtiesContext
    @Test
    public void existenceDatasetMetaDataSpecsLink() throws Exception {
        // WHEN:
        datasetMetadataService.store(createExampleMetadata());

        // THEN:
        DatasetMetadata metadata = datasetMetadataService.retrieve(exampleIRI());
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

        // WHEN:
        DatasetMetadata datasetMetadata = ExampleFilesUtils.getDatasetMetadata(ExampleFilesUtils.DATASET_URI,
                ExampleFilesUtils.CATALOG_URI);
        datasetMetadataService.store(datasetMetadata);

        // THEN:
        FDPMetadata updatedFdpMetadata =
                fdpMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.FDP_URI));
        CatalogMetadata updatedCatalogMetadata =
                catalogMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
        DatasetMetadata storedDataset =
                datasetMetadataService.retrieve(VALUE_FACTORY.createIRI(ExampleFilesUtils.DATASET_URI));

        ZonedDateTime fdpModified = ZonedDateTime.parse(updatedFdpMetadata.getModified().stringValue());
        ZonedDateTime catalogModified = ZonedDateTime.parse(updatedCatalogMetadata.getModified().stringValue());
        ZonedDateTime datasetModified = ZonedDateTime.parse(storedDataset.getModified().stringValue());

        assertFalse("Catalog modified is not after Dataset modified", catalogModified.isBefore(datasetModified));
        assertFalse("FDP modified is not after Dataset modified", fdpModified.isBefore(datasetModified));
    }

    private static DatasetMetadata createExampleMetadata() {
        return ExampleFilesUtils.getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
    }

    private static IRI exampleIRI() {
        return VALUE_FACTORY.createIRI(TEST_DATASET_URI);
    }
}
