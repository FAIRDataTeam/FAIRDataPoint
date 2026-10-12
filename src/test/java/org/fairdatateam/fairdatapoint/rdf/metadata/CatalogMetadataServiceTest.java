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

import org.eclipse.rdf4j.query.BindingSet;
import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.fairdatateam.fairdatapoint.testfixtures.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getThemeTaxonomies;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getUri;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Mock
    private MetadataRdfRepository metadataRdfRepository;

    @InjectMocks
    private CatalogMetadataService catalogMetadataService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    public void retrieve() throws Exception {
        // GIVEN: Retrieve catalog from Repository
        Model catalog = testMetadataFixtures.catalog1();
        List<Statement> catalogStatements = new ArrayList<>(catalog);
        when(metadataRdfRepository.find(getUri(catalog))).thenReturn(catalogStatements);

        // AND: Retrieve themes from datasets
        IRI theme1 = i("http://localhost/my_theme_1");
        IRI theme2_duplicated = i("http://localhost/my_theme_2_duplicated");
        List<BindingSet> themes = List.of(
                (BindingSet) Map.of("theme", theme1),
                (BindingSet) Map.of("theme", theme2_duplicated),
                (BindingSet) Map.of("theme", theme2_duplicated));
        when(metadataRdfRepository.runSparqlQueryFromFile(any(), any())).thenReturn(themes);

        // WHEN:
        Model catalogMetadata = catalogMetadataService.retrieve(getUri(catalog));

        // THEN:
        List<IRI> themeTaxonomys = getThemeTaxonomies(catalogMetadata);
        assertThat(themeTaxonomys.size(), is(equalTo(2)));
        assertThat(themeTaxonomys.get(0), is(equalTo(theme1)));
        assertThat(themeTaxonomys.get(1), is(equalTo(theme2_duplicated)));
    }

//    private final IRI catalogUri = i("http://localhost/textmining");
//
//    @Spy
//    @InjectMocks
//    private CatalogMetadataRdfRepository catalogMetadataRdfRepository;
//
//    @Test
//    @DisplayName("Themes for catalog are in cache (no query to triple store)")
//    public void themesInCache() throws RdfRepositoryException {
//        // GIVEN:
//        when(cacheManager.getCache(CATALOG_THEMES_CACHE)).thenReturn(cache);
//        when(cache.get(catalogUri.toString(), List.class)).thenReturn(Collections.emptyList());
//
//        // WHEN:
//        catalogMetadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // THEN:
//        verify(catalogMetadataRdfRepository, never()).runSparqlQueryFromFile(any(), any());
//    }
//
//    @Test
//    @DisplayName("Themes for catalog are not in cache (we have to query to triple store)")
//    public void themesNotInCache() throws RdfRepositoryException {
//        // GIVEN:
//        when(cacheManager.getCache(CATALOG_THEMES_CACHE)).thenReturn(cache);
//        when(repository.getConnection()).thenReturn(repositoryConnection);
//        when(repositoryConnection.prepareTupleQuery(any())).thenReturn(tupleQuery);
//        when(tupleQuery.evaluate()).thenReturn(tupleQueryResult);
//
//        // WHEN:
//        catalogMetadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // THEN:
//        verify(catalogMetadataRdfRepository, times(1)).runSparqlQueryFromFile(any(), any());
//    }


}
