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

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.testfixtures.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getLanguage;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getUri;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class GenericMetadataRdfRepositoryTest extends WebIntegrationTest {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Autowired
    private GenericMetadataRdfRepository genericMetadataRdfRepository;

    @Autowired
    private CatalogMetadataRdfRepository catalogMetadataRdfRepository;

    @Test
    @DisplayName("'save' should evict cache")
    public void saveEvictsCache() throws MetadataRdfRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        catalogMetadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));

        // WHEN:
        genericMetadataRdfRepository.save(new ArrayList<>(dataset), getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
    }

    @Test
    @DisplayName("'removeStatement' should evict cache")
    public void removeStatementEvictsCache() throws MetadataRdfRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        catalogMetadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));

        // WHEN:
        genericMetadataRdfRepository.removeStatement(getUri(dataset), DCTERMS.LANGUAGE, getLanguage(dataset), getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
    }

    @Test
    @DisplayName("'remove' should evict cache")
    public void removeEvictsCache() throws MetadataRdfRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        catalogMetadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));

        // WHEN:
        genericMetadataRdfRepository.remove(getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
    }

    private Cache getCache() {
        return cacheManager.getCache(CATALOG_THEMES_CACHE);
    }

    // The following tests were moved here from the existing MetadataRepositoryTest class, which was then removed.

    @Test
    public void findWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(testMetadataFixtures.catalog1());

        // WHEN:
        List<Statement> result = genericMetadataRdfRepository.find(context);

        // THEN:
        assertThat(result.size(), is(equalTo(metadata.size())));
    }

    @Test
    public void findNonExistingResource() throws Exception {
        // GIVEN:
        IRI context = i("http://localhost/non-existing");

        // WHEN:
        List<Statement> result = genericMetadataRdfRepository.find(context);

        // THEN:
        assertThat(result.size(), is(equalTo(0)));
    }

    @Test
    public void findChildTitlesWorks() throws MetadataRdfRepositoryException {
        // given
        final IRI parent = getUri(testMetadataFixtures.catalog1());

        // when
        final Map<String, String> result = genericMetadataRdfRepository.findChildTitles(parent, DCAT.HAS_DATASET);

        // then
        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void checkExistenceWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();

        // WHEN:
        boolean result = genericMetadataRdfRepository.checkExistence(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata));

        // THEN:
        assertThat(result, is(equalTo(true)));
    }

    @Test
    public void saveWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.c1_d2_distribution3();
        IRI context = getUri(metadata);
        ArrayList<Statement> statements = new ArrayList<>(metadata);

        // WHEN:
        genericMetadataRdfRepository.save(statements, context);

        // THEN:
        assertThat(genericMetadataRdfRepository.find(context).size(), is(equalTo(28)));
    }

    @Test
    public void removeWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(metadata);

        // AND: Check existence before delete
        assertThat(genericMetadataRdfRepository.find(context).size(), is(equalTo(metadata.size())));

        // WHEN:
        genericMetadataRdfRepository.remove(context);

        // THEN:
        assertThat(genericMetadataRdfRepository.find(context).size(), is(equalTo(0)));
    }

    @Test
    public void removeStatementWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(metadata);

        // AND: Check existence before delete
        assertThat(genericMetadataRdfRepository.find(context).size(), is(equalTo(metadata.size())));

        // WHEN:
        genericMetadataRdfRepository.removeStatement(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata), context);

        // THEN:
        assertThat(genericMetadataRdfRepository.find(context).size(), is(equalTo(metadata.size() - 1)));
    }

}
