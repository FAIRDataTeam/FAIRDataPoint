/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller;

import java.net.MalformedURLException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.service.impl.FairMetaDataServiceImplTest;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * MetadataController class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-11
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
@Ignore
public class MetadataControllerTest {
    
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;    
    @Autowired
    private FairMetaDataService fairMetaDataService;   
    private final String TEST_FDP_PATH = "/fdp";
    private final String TEST_CATALOG_PATH = TEST_FDP_PATH + "/" + 
            ExampleFilesUtils.CATALOG_ID;
    private final String TEST_DATASET_PATH = TEST_CATALOG_PATH + "/" + 
            ExampleFilesUtils.DATASET_ID;
    private final String TEST_DISTRIBUTION_PATH = TEST_DATASET_PATH + "/" + 
            ExampleFilesUtils.DISTRIBUTION_ID;
    private final static Logger LOGGER = 
            LogManager.getLogger(FairMetaDataServiceImplTest.class.getName());    
    MockHttpServletRequest request;
    
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
     * Check unsupported accept header.
     * 
     * @throws Exception 
     */    
    @DirtiesContext
    @Test(expected = Exception.class)    
    public void unsupportedAcceptHeader() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_FDP_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 
                response.getStatus());    
        request.setRequestURI(TEST_CATALOG_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 
                response.getStatus()); 
        request.setRequestURI(TEST_DATASET_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 
                response.getStatus()); 
        request.setRequestURI(TEST_DISTRIBUTION_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, 
                response.getStatus()); 
    }    
    /**
     * The default content type is text/turtle, when the accept header is not
     * set the default content type is served. This test is excepted to pass.
     * 
     * @throws Exception 
     */ 
    @DirtiesContext
    @Test    
    public void noAcceptHeader() throws Exception{  
        MockHttpServletResponse response;         
        Object handler;  
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        
        request.setRequestURI(TEST_FDP_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.setRequestURI(TEST_CATALOG_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.setRequestURI(TEST_DATASET_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.setRequestURI(TEST_DISTRIBUTION_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    /**
     * Check supported accept headers.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void supportedAcceptHeaders() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        
        request.setRequestURI(TEST_FDP_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.setRequestURI(TEST_CATALOG_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.setRequestURI(TEST_DATASET_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.setRequestURI(TEST_DISTRIBUTION_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    /**
     * Store catalog.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void storeCatalog() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        String metadata =  ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.CATALOG_METADATA_FILE);
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("catalogID", "cat1");
        request.setRequestURI(TEST_FDP_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }
    
    
    /**
     * Store catalog twice.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void storeCatalogTwice() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        String metadata =  ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.CATALOG_METADATA_FILE);
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("catalogID", "cat1");
        request.setRequestURI(TEST_FDP_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setContextPath("fdp");        
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("catalogID", "cat1");
        request.setRequestURI(TEST_FDP_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }
    
    /**
     * Check non existing catalog.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test 
    public void nonExistingCatalog() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_FDP_PATH + "/dumpy");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }
    
    /**
     * Check existing catalog.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void existingCatalog() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_CATALOG_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    /**
     * Store dataset.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void storeDataset() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        String metadata =  ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.DATASET_METADATA_FILE);
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("datasetID", "dat1");
        request.setRequestURI(TEST_CATALOG_PATH);        
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }
    
    
    /**
     * Store dataset twice.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void storeDatasetTwice() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        String metadata =  ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.DATASET_METADATA_FILE);
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("datasetID", "dat1");
        request.setRequestURI(TEST_CATALOG_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setContextPath("fdp");        
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("datasetID", "dat1");
        request.setRequestURI(TEST_CATALOG_PATH);       
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }
    
    /**
     * Check non existing dataset.
     * 
     * @throws Exception 
     */   
    @DirtiesContext
    @Test
    public void nonExistingDataset() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_CATALOG_PATH + "/dumpy");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }
    
    /**
     * Check existing Dataset.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void existingDataset() throws Exception{
        
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_DATASET_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    /**
     * Store distribution.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void storeDistribution() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        String metadata =  ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.DISTRIBUTION_METADATA_FILE);
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("distributionID", "dis1");
        request.setRequestURI(TEST_DATASET_PATH);       
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }
    
    
    /**
     * Store distribution twice.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test
    public void storeDistributionTwice() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        String metadata =  ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.DISTRIBUTION_METADATA_FILE);
        response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("distributionID", "dis1");
        request.setRequestURI(TEST_DATASET_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
        
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setContextPath("fdp");        
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("distributionID", "dis1");
        request.setRequestURI(TEST_DATASET_PATH);       
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }
    
    /**
     * Check non existing Content.
     * 
     * @throws Exception 
     */  
    @DirtiesContext
    @Test 
    public void nonExistingContentDistribution() throws Exception{
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_DATASET_PATH + "/dummy");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }
    
    /**
     * Check existing Content.
     * 
     * @throws Exception 
     */   
    @DirtiesContext
    @Test 
    public void existingContentDistribution() throws Exception{
        
        MockHttpServletResponse response;         
        Object handler;  
        
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_DISTRIBUTION_PATH);      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
}
