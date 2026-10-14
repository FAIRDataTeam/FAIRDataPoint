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
import org.eclipse.rdf4j.query.impl.ListBindingSet;
import org.fairdatateam.fairdatapoint.actuator.AppInfoContributor;
import org.fairdatateam.fairdatapoint.common.config.CacheConfig;
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
import org.junit.jupiter.api.Test;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {
        CacheConfig.class,
        CatalogMetadataService.class,
        CatalogMetadataServiceTest.Config.class,
        MetadataEnhancer.class,
        MetadataFactoryImpl.class,
        ResourceDefinitionFixtures.class,
        TestRdfMetadataFixtures.class,
        ThemesService.class
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
            return i("https://example.org/language");
        }

        @Bean
        public IRI license() {
            return i("https://example.org/license");
        }
    }

    private Model catalog;

    private final IRI uniqueThemeIri = i("http://localhost/unique-theme");

    private final IRI duplicatedThemeIri = i("http://localhost/duplicated-theme");

    private final List<IRI> themes = List.of(uniqueThemeIri, duplicatedThemeIri, duplicatedThemeIri);

    private final List<BindingSet> themesQueryResult = themes.stream().map(
            iri -> (BindingSet) new ListBindingSet(List.of("theme"), i(iri))
    ).toList();

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
    public void setup() throws RdfRepositoryException {
        catalog = testMetadataFixtures.catalog1();
        List<Statement> catalogStatements = new ArrayList<>(catalog);
        when(metadataRdfRepository.find(getUri(catalog))).thenReturn(catalogStatements);
        when(metadataRdfRepository.runSparqlQueryFromFile(any(), any())).thenReturn(themesQueryResult);
    }

    @Test
    public void retrieveCatalogThemesAreCached() throws Exception {
        // given multiple calls to retrieve(), each call returns the expected set of themes
        final int n = 3;
        for (int i = 0; i<n; i++) {
            // WHEN:
            Model catalogMetadata = catalogMetadataService.retrieve(getUri(catalog));

            // THEN:
            List<IRI> themeTaxonomies = getThemeTaxonomies(catalogMetadata);
            assertThat(themeTaxonomies.size(), is(equalTo(2)));
            assertThat(themeTaxonomies.get(0), is(equalTo(uniqueThemeIri)));
            assertThat(themeTaxonomies.get(1), is(equalTo(duplicatedThemeIri)));
        }
        // but the actual query has only been executed once, so subsequent calls came from cache
        verify(metadataRdfRepository, times(1)).runSparqlQueryFromFile(any(), any());
    }

}
