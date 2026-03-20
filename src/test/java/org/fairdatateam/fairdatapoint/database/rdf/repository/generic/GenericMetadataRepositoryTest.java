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
package nl.dtls.fairdatapoint.database.rdf.repository.generic;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.database.rdf.repository.catalog.CatalogMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.utils.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.ArrayList;

import static nl.dtls.fairdatapoint.config.CacheConfig.CATALOG_THEMES_CACHE;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getLanguage;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class GenericMetadataRepositoryTest extends WebIntegrationTest {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Autowired
    private CatalogMetadataRepository catalogMetadataRepository;

    @Test
    @DisplayName("'save' should evict cache")
    public void saveEvictsCache() throws MetadataRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        catalogMetadataRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));

        // WHEN:
        metadataRepository.save(new ArrayList<>(dataset), getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
    }

    @Test
    @DisplayName("'removeStatement' should evict cache")
    public void removeStatementEvictsCache() throws MetadataRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        catalogMetadataRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));

        // WHEN:
        metadataRepository.removeStatement(getUri(dataset), DCTERMS.LANGUAGE, getLanguage(dataset), getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
    }

    @Test
    @DisplayName("'remove' should evict cache")
    public void removeEvictsCache() throws MetadataRepositoryException {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog1();
        IRI catalogUri = getUri(catalog);
        Model dataset = testMetadataFixtures.c1_dataset1();

        // AND: Compute cache
        catalogMetadataRepository.getDatasetThemesForCatalog(catalogUri);

        // AND: Check if cache is full
        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));

        // WHEN:
        metadataRepository.remove(getUri(dataset));

        // THEN:
        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
    }

    private Cache getCache() {
        return cacheManager.getCache(CATALOG_THEMES_CACHE);
    }

}
