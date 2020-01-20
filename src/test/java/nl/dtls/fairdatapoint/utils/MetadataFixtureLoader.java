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
package nl.dtls.fairdatapoint.utils;

import nl.dtls.fairmetadata4j.model.*;
import nl.dtls.fairdatapoint.api.dto.metadata.*;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class MetadataFixtureLoader {

    public static final String TEST_REPOSITORY_PATH = "";
    public static final String TEST_CATALOG_PATH = format("/catalog/%s", MetadataFixtureFilesHelper.CATALOG_ID);
    public static final String TEST_DATASET_PATH = format("/dataset/%s", MetadataFixtureFilesHelper.DATASET_ID);
    public static final String TEST_DATARECORD_PATH = format("/datarecord/%s",
            MetadataFixtureFilesHelper.DATARECORD_ID);
    public static final String TEST_DISTRIBUTION_PATH = format("/distribution/%s",
            MetadataFixtureFilesHelper.DISTRIBUTION_ID);

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

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

        // Store repository metadata
        request.setRequestURI(TEST_REPOSITORY_PATH);
        String repositoryUri = request.getRequestURL().toString();
        repositoryMetadataService.store(MetadataFixtureFilesHelper.getFDPMetadata(repositoryUri));

        // Store catalog metadata
        request.setRequestURI(TEST_CATALOG_PATH);
        String cUri = request.getRequestURL().toString();
        catalogMetadataService.store(MetadataFixtureFilesHelper.getCatalogMetadata(cUri, repositoryUri));

        // Store dataset metadata
        request.setRequestURI(TEST_DATASET_PATH);
        String dUri = request.getRequestURL().toString();
        datasetMetadataService.store(MetadataFixtureFilesHelper.getDatasetMetadata(dUri, cUri));

        // Store datarecord metadata
        request.setRequestURI(TEST_DATARECORD_PATH);
        String dRecUri = request.getRequestURL().toString();
        dataRecordMetadataService.store(MetadataFixtureFilesHelper.getDataRecordMetadata(dRecUri, dUri));

        // Store distribution metadata
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        String disUri = request.getRequestURL().toString();
        distributionMetadataService.store(MetadataFixtureFilesHelper.getDistributionMetadata(disUri, dUri));
    }


}
