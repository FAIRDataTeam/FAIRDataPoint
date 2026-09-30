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

    private final String CATALOG = "catalog";
    private final String DATASET = "dataset";
    private Model catalog0;
    private IRI catalog0Url;
    private IRI dataset0Url;
    private IRI dataset1Url;
    private IRI dataset2Url;
    private IRI dataset3Url;
    private Map<String, String> datasetTitles;

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

    /**
     * Returns an absolute resource IRI
     */
    private IRI resourceUrl(String resourceType, String resourceId) {
        return i("%s/%s/%s".formatted(persistentUrl, resourceType, resourceId));
    }

    @BeforeEach
    public void setup() {
        // test data
        final String catalogId = "my-catalog-0";
        final String dataset0Id = "my-dataset-0";
        final String dataset1Id = "my-dataset-1";
        final String dataset2Id = "my-dataset-2";
        final String dataset3Id = "my-dataset-3";

        catalog0Url = resourceUrl(CATALOG, catalogId);
        dataset0Url = resourceUrl(DATASET, dataset0Id);
        dataset1Url = resourceUrl(DATASET, dataset1Id);
        dataset2Url = resourceUrl(DATASET, dataset2Id);
        dataset3Url = resourceUrl(DATASET, dataset3Id);

        datasetTitles = Map.of(
                dataset0Url.stringValue(), "B",
                dataset1Url.stringValue(), "b",
                dataset2Url.stringValue(), "a",
                dataset3Url.stringValue(), "A");

        // create in-memory rdf graphs for catalog and datasets
        final MetadataFactory factory = new MetadataFactoryImpl();

        catalog0 = factory.createCatalogMetadata(
                "My Catalog",
                "",
                catalogId,
                List.of(),
                persistentUrl,
                i(persistentUrl));

        datasetTitles.forEach((datasetUrl, datasetTitle) -> {
            final String datasetId = List.of(datasetUrl.split("/")).getLast();
            factory.createDatasetMetadata(
                    datasetTitle,
                    "",
                    datasetId,
                    List.of(),
                    List.of(),
                    persistentUrl,
                    catalog0Url);
            catalog0.add(catalog0Url, DCAT.HAS_DATASET, i(datasetUrl));
        });
    }

    @Test
    public void childResourceUrisCaseInsensitiveSortingIsStable(
    ) throws MetadataRdfRepositoryException, MetadataServiceException {
        // given
        final String urlPrefix = CATALOG;
        final String childPrefix = DATASET;
        final IRI entityUri = catalog0Url;
        final IRI relationUri = DCAT.HAS_DATASET;

        // set up mocks
        when(metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix)).thenReturn(metadataService);
        when(metadataService.retrieve(entityUri)).thenReturn(catalog0);
        when(currentUserProvider.getCurrentUser()).thenReturn(Optional.of(new User()));
        when(metadataStateService.get(any(IRI.class))).thenReturn(new Metadata(null, null, MetadataState.PUBLISHED));
        when(metadataRepository.findChildTitles(entityUri, relationUri)).thenReturn(datasetTitles);

        // evaluate
        final List<IRI> actualUris = genericController.getChildResourceUris(
                urlPrefix, childPrefix, entityUri, relationUri);

        // expect case-insensitive but stable sorting "A", "a", "B", "b"
        final List<IRI> expectedUris = List.of(dataset3Url, dataset2Url, dataset0Url, dataset1Url);
        assertEquals(expectedUris, actualUris);
    }
}
