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
package nl.dtls.fairdatapoint.acceptance.metadata;

import nl.dtl.fairmetadata4j.model.*;
import nl.dtls.fairdatapoint.api.dto.metadata.*;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class TestMetadataFixtures {

    public static final String TEST_FDP_PATH = "/fdp";
    public static final String TEST_CATALOG_PATH = format("%s/catalog/%s", TEST_FDP_PATH, ExampleFilesUtils.CATALOG_ID);
    public static final String TEST_DATASET_PATH = format("%s/dataset/%s", TEST_FDP_PATH, ExampleFilesUtils.DATASET_ID);
    public static final String TEST_DATARECORD_PATH = format("%s/datarecord/%s", TEST_FDP_PATH,
            ExampleFilesUtils.DATARECORD_ID);
    public static final String TEST_DISTRIBUTION_PATH = format("%s/distribution/%s", TEST_FDP_PATH,
            ExampleFilesUtils.DISTRIBUTION_ID);

    @Autowired
    private MetadataService<FDPMetadata, FdpMetadataChangeDTO> fdpMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    private MetadataService<DistributionMetadata, DistributionMetadataChangeDTO> distributionMetadataService;

    @Autowired
    private MetadataService<DataRecordMetadata, DataRecordMetadataChangeDTO> dataRecordMetadataService;

    public void storeExampleMetadata() throws MetadataServiceException {
        MockitoAnnotations.initMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Store fdp metadata
        request.setRequestURI(TEST_FDP_PATH);
        String fdpUri = request.getRequestURL().toString();
        fdpMetadataService.store(ExampleFilesUtils.getFDPMetadata(fdpUri));

        // Store catalog metadata
        request.setRequestURI(TEST_CATALOG_PATH);
        String cUri = request.getRequestURL().toString();
        catalogMetadataService.store(ExampleFilesUtils.getCatalogMetadata(cUri, fdpUri));

        // Store dataset metadata
        request.setRequestURI(TEST_DATASET_PATH);
        String dUri = request.getRequestURL().toString();
        datasetMetadataService.store(ExampleFilesUtils.getDatasetMetadata(dUri, cUri));

        // Store datarecord metadata
        request.setRequestURI(TEST_DATARECORD_PATH);
        String dRecUri = request.getRequestURL().toString();
        dataRecordMetadataService.store(ExampleFilesUtils.getDataRecordMetadata(dRecUri, dUri));

        // Store distribution metadata
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        String disUri = request.getRequestURL().toString();
        distributionMetadataService.store(ExampleFilesUtils.getDistributionMetadata(disUri, dUri));
    }


}
