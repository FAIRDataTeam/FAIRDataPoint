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
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertNotNull;

public class DataRecordMetadataServiceTest extends BaseIntegrationTest {
    private final static ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
    private final static String TEST_DATARECORD_URI = "http://example.com/fdp/catalog";

    @Autowired
    private MetadataService<FDPMetadata> fdpMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata> datasetMetadataService;

    @Autowired
    private MetadataService<DataRecordMetadata> dataRecordMetadataMetadataService;

    @Before
    public void createParents() throws MetadataServiceException {
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
        dataRecordMetadataMetadataService.store(createExampleMetadata());

        // THEN:
        assertNotNull(dataRecordMetadataMetadataService.retrieve(exampleIRI()));
    }

    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeWithNoParentURI() throws Exception {
        // WHEN:
        DataRecordMetadata metadata = createExampleMetadata();
        metadata.setParentURI(null);
        dataRecordMetadataMetadataService.store(metadata);

        // THEN:
        // Expect exception
    }

    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeWithWrongParentURI() throws Exception {
        // WHEN:
        dataRecordMetadataMetadataService.store(ExampleFilesUtils.getDataRecordMetadata(TEST_DATARECORD_URI,
                ExampleFilesUtils.CATALOG_URI));

        // THEN:
        // Expect exception
    }

    @DirtiesContext
    @Test
    public void storeWithNoID() throws MetadataServiceException {
        // WHEN:
        DataRecordMetadata metadata = createExampleMetadata();
        metadata.setIdentifier(null);
        dataRecordMetadataMetadataService.store(metadata);

        // THEN:
        DataRecordMetadata mdata = dataRecordMetadataMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getIdentifier());
    }

    @DirtiesContext
    @Test
    public void storeDataRecordMetaDataWithNoPublisher() throws MetadataServiceException {
        // WHEN:
        DataRecordMetadata metadata = createExampleMetadata();
        metadata.setPublisher(null);
        dataRecordMetadataMetadataService.store(metadata);

        // THEN:
        DataRecordMetadata mdata = dataRecordMetadataMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getPublisher());
    }

    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingDatasetDistribution() throws Exception {
        // WHEN:
        String uri = ExampleFilesUtils.DATASET_URI + "/dummpID676";
        dataRecordMetadataMetadataService.retrieve(VALUE_FACTORY.createIRI(uri));

        // THEN:
        // Expect exception
    }

    private static DataRecordMetadata createExampleMetadata() {
        return ExampleFilesUtils.getDataRecordMetadata(TEST_DATARECORD_URI, ExampleFilesUtils.DATASET_URI);
    }

    private static IRI exampleIRI() {
        return VALUE_FACTORY.createIRI(TEST_DATARECORD_URI);
    }
}
