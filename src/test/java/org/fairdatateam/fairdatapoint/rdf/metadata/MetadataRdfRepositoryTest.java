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
import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.fairdatateam.fairdatapoint.testfixtures.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.*;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getLanguage;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getUri;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class MetadataRdfRepositoryTest extends BaseIntegrationTest {

    // The catalog1 fixture is amended with two dcat:dataset statements before it is persisted
    private final static int EXPECTED_CATALOG1_SIZE_TRANSIENT = 30;
    private final static int EXPECTED_CATALOG1_SIZE_PERSISTENT = 32;

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Autowired
    private MetadataRdfRepository metadataRdfRepository;

    @Test
    public void themesAreCached() throws RdfRepositoryException {
        // given
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);

        // and ensure cache is empty
        assertThat(getCache().get(catalogUri), is(nullValue()));

        // when
        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // then ensure cache is now populated
        assertThat(getCache().get(catalogUri), is(notNullValue()));
    }

    @Test
    public void saveEvictsCache() throws RdfRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri), is(notNullValue()));

        // WHEN:
        metadataRdfRepository.save(new ArrayList<>(dataset), getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri), is(nullValue()));
    }

    @Test
    public void removeStatementEvictsCache() throws RdfRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri), is(notNullValue()));

        // WHEN:
        metadataRdfRepository.removeStatement(getUri(dataset), DCTERMS.LANGUAGE, getLanguage(dataset), getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri), is(nullValue()));
    }

    @Test
    public void removeEvictsCache() throws RdfRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri), is(notNullValue()));

        // WHEN:
        metadataRdfRepository.remove(getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri), is(nullValue()));
    }

    private Cache getCache() {
        return cacheManager.getCache(CATALOG_THEMES_CACHE);
    }

    // The following tests were moved here from the existing MetadataRepositoryTest class, which was then removed.

    @Test
    public void findWorks() throws Exception {
        // GIVEN: catalog fixture
        // Note that this fixture does not include dcat:dataset statements, because these are only added when
        // GenericMetadataService.create() calls updateParent().
        Model catalog1 = testMetadataFixtures.catalog1();
        Set<String> expectedTriples = tripleSet(catalog1);

        // AND: verify expected size based on catalog1 fixture
        assertThat(expectedTriples.size(), is(equalTo(EXPECTED_CATALOG1_SIZE_TRANSIENT)));

        // WHEN: get the stored catalog fixture (created by the RdfDevelopmentMigrationRunner)
        // Note that the stored fixture *does* include dcat:dataset statements *and* has different timestamp values.
        Set<String> resultTriples = tripleSet(metadataRdfRepository.find(getUri(catalog1)));

        // THEN:
        for (String expectedTriple : expectedTriples) {
            // ignore timestamps because these are expected to be different due to the way "fixtures" are implemented
            if (!expectedTriple.contains("#dateTime")) {
                assertThat(resultTriples.contains(expectedTriple), is(true));
            }
        }
    }

    @Test
    public void findNonExistingResource() throws Exception {
        // GIVEN:
        IRI context = i("http://localhost/non-existing");

        // WHEN:
        List<Statement> result = metadataRdfRepository.find(context);

        // THEN:
        assertThat(result.size(), is(equalTo(0)));
    }

    @Test
    public void findChildTitlesWorks() throws RdfRepositoryException {
        // given
        final IRI parent = getUri(testMetadataFixtures.catalog1());

        // when
        final Map<String, String> result = metadataRdfRepository.findChildTitles(parent, DCAT.HAS_DATASET);

        // then
        assertThat(result.size(), is(greaterThan(0)));
    }

    @Test
    public void checkExistenceWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();

        // WHEN:
        boolean result = metadataRdfRepository.checkExistence(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata));

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
        metadataRdfRepository.save(statements, context);

        // THEN:
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(28)));
    }

    @Test
    public void removeWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(metadata);

        // AND: Check existence before delete
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(EXPECTED_CATALOG1_SIZE_PERSISTENT)));

        // WHEN:
        metadataRdfRepository.remove(context);

        // THEN:
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(0)));
    }

    @Test
    public void removeStatementWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(metadata);

        // AND: Check existence before delete
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(EXPECTED_CATALOG1_SIZE_PERSISTENT)));

        // WHEN:
        metadataRdfRepository.removeStatement(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata), context);

        // THEN:
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(EXPECTED_CATALOG1_SIZE_PERSISTENT - 1)));
    }

    /**
     * Returns a set of strings representing rdf triples (subject, predicate, object).
     * This is convenient for comparing graph contents.
     */
    private Set<String> tripleSet(Collection<Statement> statements) {
        Set<String> tripleStrings = new HashSet<>();
        for (Statement statement : statements) {
            tripleStrings.add(
                    "%s,%s,%s".formatted(statement.getSubject(), statement.getPredicate(), statement.getObject()));
        }
        return tripleStrings;
    }

}
