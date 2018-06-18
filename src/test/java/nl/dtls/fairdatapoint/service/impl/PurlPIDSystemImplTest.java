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
package nl.dtls.fairdatapoint.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * PurlPIDSystemImplTest class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2018-06-05
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
public class PurlPIDSystemImplTest {

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();
    private final FDPMetadata fdpMetadata = ExampleFilesUtils
            .getFDPMetadata(ExampleFilesUtils.FDP_URI);
    private final CatalogMetadata catalogMetadata = ExampleFilesUtils
            .getCatalogMetadata(ExampleFilesUtils.CATALOG_URI, ExampleFilesUtils.FDP_URI);
    private final DatasetMetadata datasetMetadata = ExampleFilesUtils
            .getDatasetMetadata(ExampleFilesUtils.DATASET_URI, ExampleFilesUtils.CATALOG_URI);
    private final DistributionMetadata distributionMetadata = ExampleFilesUtils
            .getDistributionMetadata(ExampleFilesUtils.DISTRIBUTION_URI,
                    ExampleFilesUtils.DATASET_URI);

    @Autowired
    private PurlPIDSystemImpl test;

    /**
     * Test of null fdpMetadata, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void testGetURIForNullMetadata() {
        test.getURI(null);
    }

    /**
     * Test of null fdpMetadata uri, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void testGetURIForNullMetadataUri() {
        FDPMetadata metadataCopy = fdpMetadata;
        metadataCopy.setUri(null);
        test.getURI(metadataCopy);
    }

    /**
     * Test of null fdp uri, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void testGetURIForNullFdpUri() {
        test.getURI(catalogMetadata);
    }

    /**
     * Test of null parent uri, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void testGetURIForNullParentUri() {
        CatalogMetadata mdata = catalogMetadata;
        mdata.setParentURI(null);
        test.getURI(mdata);
    }

    /**
     * Test of valid fdpMetadata uri, this test is excepted to pass
     *
     * @throws java.lang.NoSuchFieldException If the filed is not declared
     */
    @Test
    public void testGetURIForValidMetadata() throws NoSuchFieldException {

        PurlPIDSystemImpl mock = Mockito.mock(PurlPIDSystemImpl.class);
        // Setting up mock object
        when(mock.getURI(fdpMetadata)).thenReturn(valueFactory
                .createIRI("http://purl.org/biosemantics-lumc/fdp"));
        when(mock.getURI(catalogMetadata)).thenReturn(valueFactory
                .createIRI("http://purl.org/biosemantics-lumc/fdp/catalog/1"));
        when(mock.getURI(datasetMetadata)).thenReturn(valueFactory
                .createIRI("http://purl.org/biosemantics-lumc/fdp/dataset/1"));
        when(mock.getURI(distributionMetadata)).thenReturn(valueFactory
                .createIRI("http://purl.org/biosemantics-lumc/fdp/distribution/1"));

        assertTrue(mock.getURI(fdpMetadata).toString().contains("purl.org"));
        assertTrue(mock.getURI(catalogMetadata).toString().contains("purl.org"));
        assertTrue(mock.getURI(datasetMetadata).toString().contains("purl.org"));
        assertTrue(mock.getURI(distributionMetadata).toString().contains("purl.org"));
    }

    /**
     * Test of null pid iri, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void testGetIdForNullPIDIri() {
        test.getId(null);
    }

    /**
     * Test of invalid purl pid iri, this test is excepted to throw error
     */
    @Test(expected = IllegalStateException.class)
    public void testGetIdForInvalidPIDIri() {
        test.getId(valueFactory.createIRI("http://example.com/fdp"));
    }

    /**
     * Test of valid purl pid iri, this test is excepted to throw error
     */
    @Test
    public void testGetIdForValidPIDIri() {
        String id = "fdp";
        String resultId = test.getId(valueFactory.createIRI("http://purl.org/lumc/" + id));
        assertEquals(resultId, id);
    }

}
