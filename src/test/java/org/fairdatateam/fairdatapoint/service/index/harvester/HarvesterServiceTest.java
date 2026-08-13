/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.service.index.harvester;

import org.fairdatateam.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import org.fairdatateam.fairdatapoint.database.rdf.migration.development.metadata.data.RdfMetadataFixtures;
import org.fairdatateam.fairdatapoint.database.rdf.migration.development.metadata.factory.MetadataFactoryImpl;
import org.fairdatateam.fairdatapoint.database.rdf.repository.MetadataRepositoryException;
import org.fairdatateam.fairdatapoint.database.rdf.repository.GenericMetadataRepository;
import org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.vocabulary.FDP;
import org.eclipse.rdf4j.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static org.fairdatateam.fairdatapoint.util.RdfIOUtil.write;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.i;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
public class HarvesterServiceTest {

    private MockRestServiceServer mockRemoteServer;

    @Mock
    private GenericMetadataRepository genericMetadataRepository;

    private HarvesterService harvesterService;

    private final String repositoryUrl = "http://fairdatapoint.example";

    private Model repository;

    private final String catalogUrl = "http://fairdatapoint.example/catalog/catalog-1";

    private Model catalog;

    @BeforeEach
    public void setup() {
        // Configure a mock remote server for the RestClient to be used by the harvester:
        // 1. create a local RestClient builder
        RestClient.Builder restClientBuilder = RestClient.builder();
        // 2. bind the builder to a MockRestServiceServer
        mockRemoteServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        // 3. build a local RestClient instance (instead of using the singleton from HttpClientConfig, because we have a
        // simple test without spring context, not a @SpringBootTest or @WebMvcTest)
        RestClient client = restClientBuilder.build();
        // 4. pass the client into the harvester service
        harvesterService = new HarvesterService(genericMetadataRepository, client);

        // Setup resource definition;
        ResourceDefinitionFixtures resourceDefinitionFixtures = new ResourceDefinitionFixtures();

        // Setup RDF fixtures
        RdfMetadataFixtures fixtures = new RdfMetadataFixtures(new MetadataFactoryImpl());

        // Create repository
        repository = fixtures.fdpMetadata(repositoryUrl);
        ResourceDefinition rdRepository = resourceDefinitionFixtures.fdpDefinition();

        // Create catalog
        catalog = fixtures.catalog1(repositoryUrl, getUri(repository));
        repository.add(i(repositoryUrl), FDP.METADATACATALOG, i(catalogUrl));
    }

    @Test
    public void harvestSucceed() throws MetadataRepositoryException {
        // GIVEN: Mock webserver
        mockEndpoint(repositoryUrl, repository);
        mockEndpoint(catalogUrl, catalog);

        // WHEN:
        harvesterService.harvest(repositoryUrl);

        // THEN:
        mockRemoteServer.verify();
        verify(genericMetadataRepository, times(2)).save(anyList(), eq(i(repositoryUrl)));
    }

    @Test
    public void harvestFailedForLinkedChildren() throws MetadataRepositoryException {
        // GIVEN: Mock webserver
        mockEndpoint(repositoryUrl, repository);
        mockEndpoint404(catalogUrl);

        // WHEN:
        harvesterService.harvest(repositoryUrl);

        // THEN:
        mockRemoteServer.verify();
        verify(genericMetadataRepository, times(1)).save(anyList(), eq(i(repositoryUrl)));
    }

    private void mockEndpoint(String url, Model body) {
        // configure mock server expectations and response
        this.mockRemoteServer
                .expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess().body(write(body)).header("content-type", "text/turtle"));
    }

    private void mockEndpoint404(String url) {
        // configure mock server expectations and response
        this.mockRemoteServer
                .expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());
    }

}
