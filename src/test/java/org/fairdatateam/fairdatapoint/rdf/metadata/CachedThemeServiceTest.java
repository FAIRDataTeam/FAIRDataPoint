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
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.impl.ListBindingSet;
import org.fairdatateam.fairdatapoint.common.config.CacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringJUnitConfig(classes = { CacheConfig.class, CachedThemeService.class })
public class CachedThemeServiceTest {

    private final IRI catalogUri = i("https://fdp.example.org/catalog/");

    private final IRI themeUri = i("https://example.org/theme");

    private final List<IRI> themes = List.of(themeUri);

    private final List<BindingSet> themesQueryResult = themes.stream().map(
            iri -> (BindingSet) new ListBindingSet(List.of("theme"), iri)
    ).toList();

    @Autowired
    private CachedThemeService cachedThemeService;

    @MockitoBean
    private GenericMetadataRdfRepository metadataRdfRepository;

    @BeforeEach
    public void setup() throws MetadataRdfRepositoryException {
        // mock query evaluation
        when(metadataRdfRepository.runSparqlQueryFromFile(any(), any())).thenReturn(themesQueryResult);
    }

    @Test
    public void themesAreCached() throws MetadataRdfRepositoryException {
        // when getDatasetThemesForCatalog is called multiple times
        final int numberOfCalls = 3;
        for (int j = 0; j < numberOfCalls; j++) {
            List<IRI> catalogThemes = cachedThemeService.getDatasetThemesForCatalog(catalogUri);
            // returns the expected themes for every call
            assertEquals(themes, catalogThemes);
        }
        // then runSparqlQueryFromFile is only called once
        verify(metadataRdfRepository, times(1)).runSparqlQueryFromFile(any(), any());
    }

}
