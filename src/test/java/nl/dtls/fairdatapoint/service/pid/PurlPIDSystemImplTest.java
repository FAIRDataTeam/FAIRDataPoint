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

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.rdf.repository.repository.RepositoryMetadataRepository;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * PurlPIDSystemImplTest class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @version 0.1
 * @since 2018-06-05
 */
public class PurlPIDSystemImplTest extends BaseIntegrationTest {

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();
    private final FDPMetadata fdpMetadata = MetadataFixtureFilesHelper
            .getFDPMetadata(MetadataFixtureFilesHelper.REPOSITORY_URI);
    private final CatalogMetadata catalogMetadata = MetadataFixtureFilesHelper
            .getCatalogMetadata(MetadataFixtureFilesHelper.CATALOG_URI, MetadataFixtureFilesHelper.REPOSITORY_URI);
    private final DatasetMetadata datasetMetadata = MetadataFixtureFilesHelper
            .getDatasetMetadata(MetadataFixtureFilesHelper.DATASET_URI, MetadataFixtureFilesHelper.CATALOG_URI);
    private final DistributionMetadata distributionMetadata = MetadataFixtureFilesHelper
            .getDistributionMetadata(MetadataFixtureFilesHelper.DISTRIBUTION_URI,
                    MetadataFixtureFilesHelper.DATASET_URI);

    @Autowired
    private PurlPIDSystemImpl test;

    @Mock
    private RepositoryMetadataRepository repositoryMetadataRepository;

    @Mock
    private IRI purlBaseUrl;

    @InjectMocks
    private PurlPIDSystemImpl purlSys;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test of null fdpMetadata, this test is excepted to throw error
     */
    @Test
    public void testGetURIForNullMetadata() {
        assertThrows(NullPointerException.class, () -> {
            test.getURI(null);
        });
    }

    /**
     * Test of null fdpMetadata uri, this test is excepted to throw error
     */
    @Test
    public void testGetURIForNullMetadataUri() {
        assertThrows(NullPointerException.class, () -> {
            FDPMetadata metadataCopy = fdpMetadata;
            metadataCopy.setUri(null);
            test.getURI(metadataCopy);
        });
    }

    /**
     * Test of null repository uri, this test is excepted to throw error
     */
    @Test
    public void testGetURIForNullFdpUri() {
        assertThrows(NullPointerException.class, () -> {
            test.getURI(catalogMetadata);
        });
    }

    /**
     * Test of null parent uri, this test is excepted to throw error
     */
    @Test
    public void testGetURIForNullParentUri() {
        assertThrows(NullPointerException.class, () -> {
            CatalogMetadata mdata = catalogMetadata;
            mdata.setParentURI(null);
            test.getURI(mdata);
        });
    }

    /**
     * Test of null purl base uri, this test is excepted to throw error
     */
    @Test
    public void testNullBaseUrl() throws Exception {
        assertThrows(NullPointerException.class, () -> {
            PurlPIDSystemImpl testInstance = new PurlPIDSystemImpl();
            testInstance.getURI(fdpMetadata);
        });
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
        when(repositoryMetadataRepository.getRepositoryIri(catalogMetadata.getParentURI()))
                .thenReturn(fdpMetadata.getUri());
        when(repositoryMetadataRepository.getRepositoryIri(datasetMetadata.getParentURI()))
                .thenReturn(fdpMetadata.getUri());
        when(repositoryMetadataRepository.getRepositoryIri(distributionMetadata.getParentURI()))
                .thenReturn(fdpMetadata.getUri());

        assertTrue(purlSys.getURI(fdpMetadata).toString().contains("purl.org"));
        assertTrue(purlSys.getURI(catalogMetadata).toString().contains("purl.org"));
        assertTrue(purlSys.getURI(datasetMetadata).toString().contains("purl.org"));
        assertTrue(purlSys.getURI(distributionMetadata).toString().contains("purl.org"));
    }

    /**
     * Test of null pid iri, this test is excepted to throw error
     */
    @Test
    public void testGetIdForNullPIDIri() {
        assertThrows(NullPointerException.class, () -> {
            test.getId(null);
        });
    }

    /**
     * Test of invalid purl pid iri, this test is excepted to throw error
     */
    @Test
    public void testGetIdForInvalidPIDIri() {
        assertThrows(IllegalStateException.class, () -> {
            test.getId(valueFactory.createIRI("http://example.com/fdp"));
        });
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
