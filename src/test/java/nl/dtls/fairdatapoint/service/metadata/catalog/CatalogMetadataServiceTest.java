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

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairdatapoint.database.rdf.repository.catalog.CatalogMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper.CATALOG_METADATA_FILE;
import static nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper.getFileContentAsStatements;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogMetadataServiceTest {

    private static final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    private IRI catalogUri = VALUE_FACTORY.createIRI(MetadataFixtureFilesHelper.CATALOG_URI);

    private IRI datasetUri = VALUE_FACTORY.createIRI(MetadataFixtureFilesHelper.DATASET_URI);

    @Mock
    private MetadataRepository<CatalogMetadata> metadataRepository;

    @Mock
    private CatalogMetadataRepository catalogMetadataRepository;

    @InjectMocks
    private CatalogMetadataService catalogMetadataService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() throws MetadataServiceException, MetadataRepositoryException {
        // GIVEN: Retrieve catalog from Repository
        List<Statement> catalogStatements = getFileContentAsStatements(CATALOG_METADATA_FILE, catalogUri.toString());
        when(metadataRepository.retrieveResource(catalogUri)).thenReturn(catalogStatements);

        // AND: Retrieve themes from datasets
        IRI theme1 = VALUE_FACTORY.createIRI("http://localhost/my_theme_1");
        IRI theme2_duplicated = VALUE_FACTORY.createIRI("http://localhost/my_theme_2_duplicated");
        List<IRI> themes = List.of(theme1, theme2_duplicated, theme2_duplicated);
        when(catalogMetadataRepository.getDatasetThemesForCatalog(catalogUri)).thenReturn(themes);

        // WHEN:
        CatalogMetadata catalogMetadata = catalogMetadataService.retrieve(catalogUri);

        // THEN:
        List<IRI> themeTaxonomys = catalogMetadata.getThemeTaxonomys();
        assertThat(themeTaxonomys.size(), is(equalTo(4)));
        assertThat(themeTaxonomys.get(0).toString(), is(equalTo("http://dbpedia.org/resource/Text_mining")));
        assertThat(themeTaxonomys.get(1).toString(), is(equalTo("http://edamontology.org/topic_0218")));
        assertThat(themeTaxonomys.get(2).toString(), is(equalTo(theme1.toString())));
        assertThat(themeTaxonomys.get(3).toString(), is(equalTo(theme2_duplicated.toString())));
    }

}
