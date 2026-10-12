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
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.fairdatateam.fairdatapoint.testfixtures.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getLanguage;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getUri;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class MetadataRdfRepositoryTest extends WebIntegrationTest {

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Autowired
    private MetadataRdfRepository metadataRdfRepository;

    // The following tests were moved here from the existing MetadataRepositoryTest class, which was then removed.

    @Test
    public void findWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(testMetadataFixtures.catalog1());

        // WHEN:
        List<Statement> result = metadataRdfRepository.find(context);

        // THEN:
        assertThat(result.size(), is(equalTo(metadata.size())));
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
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(metadata.size())));

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
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(metadata.size())));

        // WHEN:
        metadataRdfRepository.removeStatement(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata), context);

        // THEN:
        assertThat(metadataRdfRepository.find(context).size(), is(equalTo(metadata.size() - 1)));
    }

}
