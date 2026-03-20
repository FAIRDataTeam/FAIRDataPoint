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
package nl.dtls.fairdatapoint.service.metadata.catalog;

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.rdf.repository.catalog.CatalogMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepositoryImpl;
import nl.dtls.fairdatapoint.utils.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getThemeTaxonomies;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogMetadataServiceMockTest extends BaseIntegrationTest {

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Mock
    private GenericMetadataRepositoryImpl metadataRepository;

    @Mock
    private CatalogMetadataRepository catalogMetadataRepository;

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
        when(metadataRepository.find(getUri(catalog))).thenReturn(catalogStatements);

        // AND: Retrieve themes from datasets
        IRI theme1 = i("http://localhost/my_theme_1");
        IRI theme2_duplicated = i("http://localhost/my_theme_2_duplicated");
        List<IRI> themes = List.of(theme1, theme2_duplicated, theme2_duplicated);
        when(catalogMetadataRepository.getDatasetThemesForCatalog(getUri(catalog))).thenReturn(themes);

        // WHEN:
        Model catalogMetadata = catalogMetadataService.retrieve(getUri(catalog));

        // THEN:
        List<IRI> themeTaxonomys = getThemeTaxonomies(catalogMetadata);
        assertThat(themeTaxonomys.size(), is(equalTo(2)));
        assertThat(themeTaxonomys.get(0), is(equalTo(theme1)));
        assertThat(themeTaxonomys.get(1), is(equalTo(theme2_duplicated)));
    }

}
