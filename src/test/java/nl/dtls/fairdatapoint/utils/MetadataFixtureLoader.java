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

import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.Model;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;

@Service
public class MetadataFixtureLoader {

    public static final String TEST_REPOSITORY_PATH = "";
    public static final String TEST_CATALOG_PATH = format("/catalog/%s", MetadataFixtureFilesHelper.CATALOG_ID);
    public static final String TEST_DATASET_PATH = format("/dataset/%s", MetadataFixtureFilesHelper.DATASET_ID);
    public static final String TEST_DISTRIBUTION_PATH = format("/distribution/%s",
            MetadataFixtureFilesHelper.DISTRIBUTION_ID);

    @Autowired
    @Qualifier("repositoryMetadataService")
    private MetadataService repositoryMetadataService;

    @Autowired
    @Qualifier("catalogMetadataService")
    private MetadataService catalogMetadataService;

    @Autowired
    @Qualifier("genericMetadataService")
    private MetadataService genericMetadataService;

    @Autowired
    protected ResourceDefinitionFixtures resourceDefinitionFixtures;

    public void storeExampleMetadata() throws MetadataServiceException {
        ResourceDefinition repositoryRd = resourceDefinitionFixtures.repositoryDefinition();
        ResourceDefinition catalogRd = resourceDefinitionFixtures.catalogDefinition(repositoryRd);
        ResourceDefinition datasetRd = resourceDefinitionFixtures.datasetDefinition(catalogRd);
        ResourceDefinition distributionRd = resourceDefinitionFixtures.distributionDefinition(datasetRd);

        MockitoAnnotations.initMocks(this);
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Store repository metadata
        request.setRequestURI(TEST_REPOSITORY_PATH);
        String repositoryUri = request.getRequestURL().toString();
        Model repository = MetadataFixtureFilesHelper.getFDPMetadata(repositoryUri);
        repositoryMetadataService.store(repository, i(repositoryUri), repositoryRd);

        // Store catalog metadata
        request.setRequestURI(TEST_CATALOG_PATH);
        String catalogUri = request.getRequestURL().toString();
        Model catalog = MetadataFixtureFilesHelper.getCatalogMetadata(catalogUri, repositoryUri);
        catalogMetadataService.store(catalog, i(catalogUri), catalogRd);

        // Store dataset metadata
        request.setRequestURI(TEST_DATASET_PATH);
        String datasetUri = request.getRequestURL().toString();
        Model dataset = MetadataFixtureFilesHelper.getDatasetMetadata(datasetUri, catalogUri);
        genericMetadataService.store(dataset, i(datasetUri), datasetRd);

        // Store distribution metadata
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        String distributionUri = request.getRequestURL().toString();
        Model distribution = MetadataFixtureFilesHelper.getDistributionMetadata(distributionUri, datasetUri);
        genericMetadataService.store(distribution, i(distributionUri), distributionRd);
    }


}
