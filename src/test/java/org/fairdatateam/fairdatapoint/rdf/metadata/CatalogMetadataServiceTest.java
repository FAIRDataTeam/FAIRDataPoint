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

import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.impl.ListBindingSet;
import org.fairdatateam.fairdatapoint.actuator.AppInfoContributor;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.ResourceDefinitionFixtures;
import org.fairdatateam.fairdatapoint.migration.triplestore.development.MetadataFactoryImpl;
import org.fairdatateam.fairdatapoint.profile.ProfileService;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionCache;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionService;
import org.fairdatateam.fairdatapoint.security.CurrentUserProvider;
import org.fairdatateam.fairdatapoint.security.membership.MemberService;
import org.fairdatateam.fairdatapoint.testfixtures.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {
        CatalogMetadataService.class,
        CatalogMetadataServiceTest.Config.class,
        MetadataEnhancer.class,
        MetadataFactoryImpl.class,
        ResourceDefinitionFixtures.class,
        TestRdfMetadataFixtures.class
})
public class CatalogMetadataServiceTest {

    @TestConfiguration
    static class Config {
        @Bean
        public String persistentUrl() {
            return "http://localhost:8088";
        }

        @Bean
        public IRI language() {
            return i("http://example.org/language");
        }

        @Bean
        public IRI license() {
            return i("http://example.org/license");
        }
    }

    @Autowired
    private CatalogMetadataService catalogMetadataService;

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @MockitoBean
    private AppInfoContributor appInfoContributor;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private MetadataRdfRepository metadataRdfRepository;

    @MockitoBean
    private MetadataStateService metadataStateService;

    @MockitoBean
    private MetadataValidator metadataValidator;

    @MockitoBean
    private MetricsMetadataService metricsMetadataService;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private ResourceDefinitionCache resourceDefinitionCache;

    @MockitoBean
    private ResourceDefinitionService resourceDefinitionService;

    @MockitoBean
    private SpringDocConfigProperties springDocConfigProperties;

    @MockitoBean
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

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
        IRI uniqueThemeIri = i("http://localhost/unique-theme");
        IRI duplicatedThemeIri = i("http://localhost/duplicated-theme");
        List<BindingSet> themes = Stream.of(uniqueThemeIri, duplicatedThemeIri, duplicatedThemeIri).map(
                iri -> (BindingSet) new ListBindingSet(List.of("theme"), i(iri))
        ).toList();
        when(metadataRdfRepository.runSparqlQueryFromFile(any(), any())).thenReturn(themes);

        // WHEN:
        Model catalogMetadata = catalogMetadataService.retrieve(getUri(catalog));

        // THEN:
        List<IRI> themeTaxonomies = getThemeTaxonomies(catalogMetadata);
        assertThat(themeTaxonomies.size(), is(equalTo(2)));
        assertThat(themeTaxonomies.get(0), is(equalTo(uniqueThemeIri)));
        assertThat(themeTaxonomies.get(1), is(equalTo(duplicatedThemeIri)));
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


//    @Test
//    @DisplayName("'save' should evict cache")
//    public void saveEvictsCache() throws RdfRepositoryException {
//        // GIVEN:
//        Model catalog = testMetadataFixtures.catalog1();
//        IRI catalogUri = getUri(catalog);
//        Model dataset = testMetadataFixtures.c1_dataset1();
//
//        // AND: Compute cache
//        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // AND: Check if cache is full
//        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));
//
//        // WHEN:
//        metadataRdfRepository.save(new ArrayList<>(dataset), getUri(dataset));
//
//        // THEN:
//        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
//    }
//
//    @Test
//    @DisplayName("'removeStatement' should evict cache")
//    public void removeStatementEvictsCache() throws RdfRepositoryException {
//        // GIVEN:
//        Model catalog = testMetadataFixtures.catalog1();
//        IRI catalogUri = getUri(catalog);
//        Model dataset = testMetadataFixtures.c1_dataset1();
//
//        // AND: Compute cache
//        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // AND: Check if cache is full
//        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));
//
//        // WHEN:
//        metadataRdfRepository.removeStatement(getUri(dataset), DCTERMS.LANGUAGE, getLanguage(dataset), getUri(dataset));
//
//        // THEN:
//        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
//    }
//
//    @Test
//    @DisplayName("'remove' should evict cache")
//    public void removeEvictsCache() throws RdfRepositoryException {
//        // GIVEN:
//        Model catalog = testMetadataFixtures.catalog1();
//        IRI catalogUri = getUri(catalog);
//        Model dataset = testMetadataFixtures.c1_dataset1();
//
//        // AND: Compute cache
//        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // AND: Check if cache is full
//        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));
//
//        // WHEN:
//        metadataRdfRepository.remove(getUri(dataset));
//
//        // THEN:
//        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
//    }
//
//    private Cache getCache() {
//        return cacheManager.getCache(CATALOG_THEMES_CACHE);
//    }


}
