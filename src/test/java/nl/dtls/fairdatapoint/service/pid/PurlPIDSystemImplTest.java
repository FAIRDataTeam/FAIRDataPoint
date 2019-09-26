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
package nl.dtls.fairdatapoint.service.pid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.service.metadata.MetadataService;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PurlPIDSystemImplTest class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2018-06-05
 * @version 0.1
 */
public class PurlPIDSystemImplTest extends BaseIntegrationTest {

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

    @Mock
    private MetadataService fairMetaDataService;
    
    @Mock
    private IRI purlBaseUrl;

    @InjectMocks
    private PurlPIDSystemImpl purlSys;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

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
     * Test of null purl base uri, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void testNullBaseUrl() throws Exception {
        PurlPIDSystemImpl testInstance = new PurlPIDSystemImpl();
        testInstance.getURI(fdpMetadata);
    }

    /**
     * Test of valid fdpMetadata uri, this test is excepted to pass
     *
     * @throws java.lang.NoSuchFieldException If the filed is not declared
     */
    @Test
    public void testGetURIForValidMetadata() throws Exception {
        
        // Setting up mock object
        
        when(purlBaseUrl.toString()).thenReturn("http://purl.org/biosemantics-lumc/fdp");
        when(fairMetaDataService.getFDPIri(catalogMetadata.getParentURI()))
                .thenReturn(fdpMetadata.getUri());
        when(fairMetaDataService.getFDPIri(datasetMetadata.getParentURI()))
                .thenReturn(fdpMetadata.getUri());
        when(fairMetaDataService.getFDPIri(distributionMetadata.getParentURI()))
                .thenReturn(fdpMetadata.getUri());

        assertTrue(purlSys.getURI(fdpMetadata).toString().contains("purl.org"));
        assertTrue(purlSys.getURI(catalogMetadata).toString().contains("purl.org"));
        assertTrue(purlSys.getURI(datasetMetadata).toString().contains("purl.org"));
        assertTrue(purlSys.getURI(distributionMetadata).toString().contains("purl.org"));
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
