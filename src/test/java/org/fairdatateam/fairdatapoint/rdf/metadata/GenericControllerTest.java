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

package org.fairdatateam.fairdatapoint.rdf.metadata;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.fairdatateam.fairdatapoint.migration.triplestore.development.MetadataFactory;
import org.fairdatateam.fairdatapoint.migration.triplestore.development.MetadataFactoryImpl;
import org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchemaService;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionService;
import org.fairdatateam.fairdatapoint.search.SearchFilterCache;
import org.fairdatateam.fairdatapoint.security.CurrentUserProvider;
import org.fairdatateam.fairdatapoint.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = { GenericController.class, GenericControllerTest.Config.class })
public class GenericControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        public String persistentUrl() {
            return "http://localhost:8088";
        }
    }

    @Autowired
    String persistentUrl;

    @Autowired
    GenericController genericController;

    Model catalog;
    Model datasetLower;
    Model datasetUpper;

    IRI catalogUrl;
    IRI datasetUpperUrl;
    IRI datasetLowerUrl;
    Map<String, String> titles;

    @MockitoBean
    CurrentUserProvider currentUserProvider;

    @MockitoBean
    GenericMetadataRdfRepository metadataRepository;

    @MockitoBean
    MetadataEnhancer metadataEnhancer;

    @MockitoBean
    MetadataSchemaService metadataSchemaService;

    @MockitoBean
    MetadataServiceFactory metadataServiceFactory;

    @MockitoBean
    MetadataService metadataService;

    @MockitoBean
    MetadataStateService metadataStateService;

    @MockitoBean
    ResourceDefinitionService resourceDefinitionService;

    @MockitoBean
    SearchFilterCache searchFilterCache;

    @BeforeEach
    public void setup() {
        final MetadataFactory factory = new MetadataFactoryImpl();
        catalogUrl = i(persistentUrl + "/catalog/my-catalog");
        datasetUpperUrl = i(persistentUrl + "/dataset/my-dataset-upper");
        datasetLowerUrl = i(persistentUrl + "/dataset/my-dataset-lower");
        titles = Map.of(
                datasetUpperUrl.stringValue(), "MY DATASET",
                datasetLowerUrl.stringValue(), "my dataset");
        catalog = factory.createCatalogMetadata(
                "My Catalog",
                "",
                "my-catalog",
                List.of(),
                persistentUrl,
                i(persistentUrl));
        datasetUpper = factory.createDatasetMetadata(
                titles.get(datasetUpperUrl.stringValue()),
                "",
                "my-dataset-upper",
                List.of(),
                List.of(),
                persistentUrl,
                catalogUrl);
        datasetLower = factory.createDatasetMetadata(
                titles.get(datasetLowerUrl.stringValue()),
                "",
                "my-dataset-lower",
                List.of(),
                List.of(),
                persistentUrl,
                catalogUrl);
        // add DCAT statements
        catalog.add(catalogUrl, DCAT.HAS_DATASET, datasetUpperUrl);
        catalog.add(catalogUrl, DCAT.HAS_DATASET, datasetLowerUrl);
    }

    @Test
    public void getChildResourceUrisReturnsSorted(
    ) throws MetadataRdfRepositoryException, MetadataServiceException {
        // given
        final String urlPrefix = "catalog";
        final String childPrefix = "dataset";
        final IRI entityUri = i(persistentUrl + "/catalog/my-catalog");
        final IRI relationUri = DCAT.HAS_DATASET;

        // set up mocks
        when(metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix)).thenReturn(metadataService);
        when(metadataService.retrieve(entityUri)).thenReturn(catalog);
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(new User()));
        when(metadataStateService.get(any(IRI.class))).thenReturn(new Metadata(null, null, MetadataState.PUBLISHED));
        when(metadataRepository.findChildTitles(entityUri, relationUri)).thenReturn(titles);

        // evaluate
        final List<IRI> actualUris = genericController.getChildResourceUris(
                urlPrefix, childPrefix, entityUri, relationUri);

        // based on default lexicographic order we expect uppercase before lowercase
        final List<IRI> expectedUris = List.of(datasetUpperUrl, datasetLowerUrl);
        assertEquals(expectedUris, actualUris);
    }
}
