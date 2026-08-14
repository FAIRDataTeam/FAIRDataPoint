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

import org.fairdatateam.fairdatapoint.config.HttpClientConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
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
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest
@ContextConfiguration(classes = HttpClientConfig.class)
@ExtendWith(MockitoExtension.class)
public class HarvesterServiceTest {

    @Autowired
    private MockRestServiceServer mockRemoteServer;

    @Autowired
    private RestClient restClient;

    @Mock
    private GenericMetadataRepository genericMetadataRepository;

    private HarvesterService harvesterService;

    private final String repositoryUrl = "http://fairdatapoint.example";

    private Model repository;

    private final String catalogUrl = "http://fairdatapoint.example/catalog/catalog-1";

    private Model catalog;

    @BeforeEach
    public void setup() {
        // Create a harvester service
        harvesterService = new HarvesterService(genericMetadataRepository, restClient);

        // Set up resource definition;
        ResourceDefinitionFixtures resourceDefinitionFixtures = new ResourceDefinitionFixtures();

        // Set up RDF fixtures
        RdfMetadataFixtures fixtures = new RdfMetadataFixtures(new MetadataFactoryImpl());

        // Create repository
        repository = fixtures.fdpMetadata(repositoryUrl);
        ResourceDefinition rdRepository = resourceDefinitionFixtures.fdpDefinition();

        // Create catalog
        catalog = fixtures.catalog1(repositoryUrl, getUri(repository));
        repository.add(i(repositoryUrl), FDP.METADATACATALOG, i(catalogUrl));
    }

    @Test
    public void harvestSucceeded() throws MetadataRepositoryException {
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
    public void harvestFailedDueToServerError() throws MetadataRepositoryException {
        // GIVEN: Mock webserver
        mockRemoteServer
                .expect(ExpectedCount.once(), requestTo(repositoryUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // WHEN:
        harvesterService.harvest(repositoryUrl);

        // THEN:
        mockRemoteServer.verify();
        verify(genericMetadataRepository, never()).save(any(), any());
    }

    @Test
    public void harvestFailedForLinkedChildren() throws MetadataRepositoryException {
        // GIVEN: Mock webserver
        mockEndpoint(repositoryUrl, repository);
        mockRemoteServer
                .expect(ExpectedCount.once(), requestTo(catalogUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withResourceNotFound());

        // WHEN:
        harvesterService.harvest(repositoryUrl);

        // THEN:
        mockRemoteServer.verify();
        verify(genericMetadataRepository, times(1)).save(anyList(), eq(i(repositoryUrl)));
    }

    private void mockEndpoint(String url, Model body) {
        // configure mock server expectations and response
        mockRemoteServer
                .expect(ExpectedCount.once(), requestTo(url))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess().body(write(body)).header("content-type", "text/turtle"));
    }

}
