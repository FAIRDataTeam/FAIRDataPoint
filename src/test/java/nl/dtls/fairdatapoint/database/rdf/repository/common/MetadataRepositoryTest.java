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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.database.rdf.repository.common;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.utils.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getLanguage;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class MetadataRepositoryTest extends WebIntegrationTest {

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Test
    public void findWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(testMetadataFixtures.catalog1());

        // WHEN:
        List<Statement> result = metadataRepository.find(context);

        // THEN:
        assertThat(result.size(), is(equalTo(metadata.size())));
    }

    @Test
    public void findNonExistingResource() throws Exception {
        // GIVEN:
        IRI context = i("http://localhost/non-existing");

        // WHEN:
        List<Statement> result = metadataRepository.find(context);

        // THEN:
        assertThat(result.size(), is(equalTo(0)));
    }

    @Test
    public void checkExistenceWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();

        // WHEN:
        boolean result = metadataRepository.checkExistence(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata));

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
        metadataRepository.save(statements, context);

        // THEN:
        assertThat(metadataRepository.find(context).size(), is(equalTo(28)));
    }

    @Test
    public void removeWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(metadata);

        // AND: Check existence before delete
        assertThat(metadataRepository.find(context).size(), is(equalTo(metadata.size())));

        // WHEN:
        metadataRepository.remove(context);

        // THEN:
        assertThat(metadataRepository.find(context).size(), is(equalTo(0)));
    }

    @Test
    public void removeStatementWorks() throws Exception {
        // GIVEN:
        Model metadata = testMetadataFixtures.catalog1();
        IRI context = getUri(metadata);

        // AND: Check existence before delete
        assertThat(metadataRepository.find(context).size(), is(equalTo(metadata.size())));

        // WHEN:
        metadataRepository.removeStatement(getUri(metadata), DCTERMS.LANGUAGE, getLanguage(metadata), context);

        // THEN:
        assertThat(metadataRepository.find(context).size(), is(equalTo(metadata.size() - 1)));
    }

}
