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
package nl.dtls.fairdatapoint.service.metadata.datarecord;

import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.DataRecordMetadataChangeDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataRecordMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private MetadataService<DataRecordMetadata, DataRecordMetadataChangeDTO> dataRecordMetadataService;

    @BeforeEach
    public void setUp() throws Exception {
        DataRecordMetadata dataRecord = testMetadataFixtures.c1_d1_datarecord1();
        dataRecordMetadataService.store(dataRecord);
    }

    @Test
    public void retrieveNonExitingDatasetDistribution() {
        assertThrows(ResourceNotFoundException.class, () -> {
            // GIVEN:
            IRI repositoryUri = testMetadataFixtures.repositoryMetadata().getUri();
            IRI dataRecordUri = i(format("%s/non-existing", repositoryUri));

            // WHEN:
            dataRecordMetadataService.retrieve(dataRecordUri);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        DataRecordMetadata dataRecord = testMetadataFixtures.c1_d1_datarecord1();

        // WHEN:
        dataRecordMetadataService.store(dataRecord);

        // THEN:
        assertNotNull(dataRecordMetadataService.retrieve(dataRecord.getUri()));
    }

    @Test
    public void storeWithNoParentURI() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            DataRecordMetadata dataRecord = testMetadataFixtures.c1_d1_datarecord1();
            dataRecord.setParentURI(null);

            // WHEN:
            dataRecordMetadataService.store(dataRecord);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithWrongParentURI() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
            DataRecordMetadata dataRecord = testMetadataFixtures.c1_d1_datarecord1();
            dataRecord.setParentURI(repository.getUri());

            // WHEN:
            dataRecordMetadataService.store(dataRecord);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        DataRecordMetadata dataRecord = testMetadataFixtures.c1_d1_datarecord1();
        dataRecord.setIdentifier(null);

        // WHEN:
        dataRecordMetadataService.store(dataRecord);

        // THEN:
        DataRecordMetadata result = dataRecordMetadataService.retrieve(dataRecord.getUri());
        assertNotNull(result.getIdentifier());
    }

    @Test
    public void storeDataRecordMetaDataWithNoPublisher() throws Exception {
        // GIVEN:
        DataRecordMetadata dataRecord = testMetadataFixtures.c1_d1_datarecord1();
        dataRecord.setPublisher(null);

        // WHEN:
        dataRecordMetadataService.store(dataRecord);

        // THEN:
        DataRecordMetadata result = dataRecordMetadataService.retrieve(dataRecord.getUri());
        assertNotNull(result.getPublisher());
    }

}
