/**
 * The MIT License
 * Copyright © 2016 DTL
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

import java.net.MalformedURLException;
import java.util.logging.Level;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * FairMetaDataServiceImpl class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-08
 * @version 0.4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class FairMetaDataServiceImplTest { 
    
    private final static Logger LOGGER = 
            LogManager.getLogger(FairMetaDataServiceImplTest.class.getName());    
    @Autowired
    private FairMetaDataService fairMetaDataService;    
    private final String TEST_FDP_URI = "http://example.com/fdp";
    private final String TEST_CATALOG_URI = "http://example.com/fdp/catalog";
    private final String TEST_DATASET_URI = 
            "http://example.com/fdp/catalog/dataset";
    private final String TEST_DISTRIBUTION_URI = 
            "http://example.com/fdp/catalog/dataset/distrubtion";
    
    @Before
    public void storeExampleMetadata() throws StoreManagerException, 
            MalformedURLException, DatatypeConfigurationException, 
            FairMetadataServiceException, MetadataException {        
        LOGGER.info("Storing example FDP metadata for service layer tests");
        fairMetaDataService.storeFDPMetaData(
                ExampleFilesUtils.getFDPMetadata(ExampleFilesUtils.FDP_URI));
        LOGGER.info("Storing example catalog metadata for service layer tests");
        fairMetaDataService.storeCatalogMetaData(ExampleFilesUtils.
                getCatalogMetadata(ExampleFilesUtils.CATALOG_URI, 
                        ExampleFilesUtils.FDP_URI));
        LOGGER.info("Storing example dataset metadata for service layer tests");
        fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                getDatasetMetadata(ExampleFilesUtils.DATASET_URI, 
                        ExampleFilesUtils.CATALOG_URI)); 
        LOGGER.info("Storing example distribution "
                + "metadata for service layer tests");
        fairMetaDataService.storeDistributionMetaData(
                ExampleFilesUtils.getDistributionMetadata(
                        ExampleFilesUtils.DISTRIBUTION_URI, 
                        ExampleFilesUtils.DATASET_URI));        
    }
    
    /**
     * Test to store FDP metadata, this test is excepted to throw error
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
     */
    @DirtiesContext
    @Test (expected = MetadataException.class)
    public void storeFDPMetaDataWithNoTitle() throws FairMetadataServiceException, 
            MetadataException {            
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setTitle(null);
        fairMetaDataService.storeFDPMetaData(metadata);
    }
    
    /**
     * Test to store FDP metadata, this test is excepted to throw error
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
     */
    @DirtiesContext
    @Test (expected = MetadataException.class)
    public void storeFDPMetaDataWithNoID() throws FairMetadataServiceException, 
            MetadataException {            
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setIdentifier(null);
        fairMetaDataService.storeFDPMetaData(metadata);
    }
    
    /**
     * Test to store FDP metadata, this test is excepted to throw error
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
     */
    @DirtiesContext
    @Test (expected = MetadataException.class)
    public void storeFDPMetaDataWithNoRepoID() throws FairMetadataServiceException, 
            MetadataException {            
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setRepostoryIdentifier(null);
        fairMetaDataService.storeFDPMetaData(metadata);
    }
    
    
    /**
     * Test to store FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaData() {            
        try {
            fairMetaDataService.storeFDPMetaData(ExampleFilesUtils.getFDPMetadata(
                    TEST_FDP_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        } 
    }
    
    
    /**
     * Test to update FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void updateFDPMetaData() {            
        try {
            FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                    TEST_FDP_URI);
            fairMetaDataService.storeFDPMetaData(metadata);
            fairMetaDataService.updateFDPMetaData(TEST_FDP_URI, metadata);
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        } 
    }
      
    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveFDPMetaData() throws FairMetadataServiceException {            
        assertNotNull(fairMetaDataService.retrieveFDPMetaData(
                ExampleFilesUtils.FDP_URI));
    }
    
    /**
     * Test to store catalog metadata, this test is excepted to throw erro
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
     */
    @DirtiesContext
    @Test (expected = IllegalStateException.class)
    public void storeCatalogMetaDataWithNOParentUri() throws 
            FairMetadataServiceException, MetadataException, 
            IllegalStateException {     
        CatalogMetadata metadata = ExampleFilesUtils.getCatalogMetadata(
                TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeCatalogMetaData(metadata);
    }
    
    /**
     * Test to store catalog metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaData() {            
        try {
            fairMetaDataService.storeCatalogMetaData(ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI,
                            ExampleFilesUtils.FDP_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        } 
    }
    
    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted
     * to throw an exception
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingCatalogMetaData() throws 
            FairMetadataServiceException {            
        String uri = ExampleFilesUtils.FDP_URI + "/dummpID676";           
        assertNull(fairMetaDataService.retrieveCatalogMetaData(uri));
    }
    
    /**
     * Test to retrieve catalog metadata, this test is excepted to pass
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveCatalogMetaData() throws FairMetadataServiceException {            
        assertNotNull(fairMetaDataService.retrieveCatalogMetaData(
                ExampleFilesUtils.CATALOG_URI));
    } 
    
    /**
     * Test to store dataset metadata, this test is excepted to throw error
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
     */
    @DirtiesContext
    @Test (expected = IllegalStateException.class)
    public void storeDatasetMetaDataWithNoParentURI() throws 
            FairMetadataServiceException, 
            MetadataException, IllegalStateException { 
        
        DatasetMetadata metadata = ExampleFilesUtils.getDatasetMetadata(
                TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDatasetMetaData(metadata);
    }
    
    /**
     * Test to store dataset metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDatasetMetaData() {            
        try {
            fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI,
                            ExampleFilesUtils.CATALOG_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }
    
    /**
     * Test to retrieve NonExiting dataset metadata, this test is excepted 
     * to throw an exception
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingdDatasetMetaData() throws 
            FairMetadataServiceException { 
        String uri = ExampleFilesUtils.CATALOG_URI + "/dummpID676";            
        assertNull(fairMetaDataService.retrieveDatasetMetaData(uri));       
    }
    
    /**
     * Test to retrieve dataset metadata, this test is excepted to pass
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetMetaData() throws FairMetadataServiceException {            
        assertNotNull(fairMetaDataService.retrieveDatasetMetaData(
                ExampleFilesUtils.DATASET_URI));
    }  
    
    /**
     * Test to store dataset distribution, this test is excepted to throw error
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
     */
    @DirtiesContext
    @Test (expected = IllegalStateException.class)
    public void storeDistributionMetaDataNoParentURI() throws 
            FairMetadataServiceException, MetadataException, 
            IllegalStateException {            
        DistributionMetadata metadata = ExampleFilesUtils.
                getDistributionMetadata(TEST_DISTRIBUTION_URI, 
                        ExampleFilesUtils.DATASET_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDistributionMetaData(metadata);
    } 
    
    /**
     * Test to store dataset distribution, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaData() {            
        try {
            fairMetaDataService.storeDistributionMetaData(ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI,
                            ExampleFilesUtils.DATASET_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }  
    
    /**
     * Test to retrieve non exiting distribution metadata, this test is 
     * to throw an exception
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingDatasetDistribution() throws 
            FairMetadataServiceException {        
        String uri = ExampleFilesUtils.DATASET_URI + "/dummpID676";
        assertNull(fairMetaDataService.retrieveDistributionMetaData(uri));    
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetDistribution() throws 
            FairMetadataServiceException {            
        assertNotNull(fairMetaDataService.retrieveDistributionMetaData(
                ExampleFilesUtils.DISTRIBUTION_URI));
    }
    
}
