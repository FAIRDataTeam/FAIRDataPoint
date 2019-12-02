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
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DataRecordMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataRecordMetadataServiceTest extends BaseIntegrationTest {
    private final static ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
    private final static String TEST_DATARECORD_URI = "http://example.com/fdp/catalog";

    @Autowired
    private UserFixtures userFixtures;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    private MetadataService<DataRecordMetadata, DataRecordMetadataChangeDTO> dataRecordMetadataService;

    @BeforeEach
    public void createParents() throws MetadataServiceException {
        String albertUuid = userFixtures.albert().getUuid();
        Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
        SecurityContextHolder.getContext().setAuthentication(auth);
        repositoryMetadataService.store(MetadataFixtureFilesHelper.getFDPMetadata(MetadataFixtureFilesHelper.REPOSITORY_URI));
        catalogMetadataService.store(MetadataFixtureFilesHelper.getCatalogMetadata(MetadataFixtureFilesHelper.CATALOG_URI,
                MetadataFixtureFilesHelper.REPOSITORY_URI));
        datasetMetadataService.store(MetadataFixtureFilesHelper.getDatasetMetadata(MetadataFixtureFilesHelper.DATASET_URI,
                MetadataFixtureFilesHelper.CATALOG_URI));
    }

    @DirtiesContext
    @Test
    public void storeAndRetrieve() throws MetadataServiceException {
        // WHEN:
        dataRecordMetadataService.store(createExampleMetadata());

        // THEN:
        assertNotNull(dataRecordMetadataService.retrieve(exampleIRI()));
    }

    @DirtiesContext
    @Test
    public void storeWithNoParentURI() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            // WHEN:
            DataRecordMetadata metadata = createExampleMetadata();
            metadata.setParentURI(null);
            dataRecordMetadataService.store(metadata);

            // THEN:
            // Expect exception
        });
    }

    @DirtiesContext
    @Test
    public void storeWithWrongParentURI() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            // WHEN:
            dataRecordMetadataService.store(MetadataFixtureFilesHelper.getDataRecordMetadata(TEST_DATARECORD_URI,
                    MetadataFixtureFilesHelper.CATALOG_URI));

            // THEN:
            // Expect exception
        });
    }

    @DirtiesContext
    @Test
    public void storeWithNoID() throws MetadataServiceException {
        // WHEN:
        DataRecordMetadata metadata = createExampleMetadata();
        metadata.setIdentifier(null);
        dataRecordMetadataService.store(metadata);

        // THEN:
        DataRecordMetadata mdata = dataRecordMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getIdentifier());
    }

    @DirtiesContext
    @Test
    public void storeDataRecordMetaDataWithNoPublisher() throws MetadataServiceException {
        // WHEN:
        DataRecordMetadata metadata = createExampleMetadata();
        metadata.setPublisher(null);
        dataRecordMetadataService.store(metadata);

        // THEN:
        DataRecordMetadata mdata = dataRecordMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getPublisher());
    }

    @DirtiesContext
    @Test
    public void retrieveNonExitingDatasetDistribution() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            // WHEN:
            String uri = MetadataFixtureFilesHelper.DATASET_URI + "/dummpID676";
            dataRecordMetadataService.retrieve(VALUE_FACTORY.createIRI(uri));

            // THEN:
            // Expect exception
        });
    }

    private static DataRecordMetadata createExampleMetadata() {
        return MetadataFixtureFilesHelper.getDataRecordMetadata(TEST_DATARECORD_URI,
                MetadataFixtureFilesHelper.DATASET_URI);
    }

    private static IRI exampleIRI() {
        return VALUE_FACTORY.createIRI(TEST_DATARECORD_URI);
    }
}
