/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller;

import javax.servlet.http.HttpServletResponse;
import nl.dtls.fairdatapoint.api.config.RestApiConfiguration;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.impl.DataAccessorServiceImpl;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import org.apache.http.HttpHeaders;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * DataAccessorController class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-09
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiConfiguration.class})
@DirtiesContext
public class DataAccessorControllerTest {    
       
    @Mock
    private DataAccessorService dataAccessorService;  
    
    @InjectMocks
    private DataAccessorController controller;
    
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    
    private MockMvc mockMvc;
    
    public DataAccessorControllerTest() {
    }
    
    @Before
    public void setup() throws Exception {        
        MockitoAnnotations.initMocks(this);        
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        String exampleDistribution = ExampleTurtleFiles.getTurtleAsString(
                ExampleTurtleFiles.DATASET_METADATA.get(0));
        when(dataAccessorService.retrieveDatasetDistribution("catalogID", 
                "datasetID", "distributionID", RDFFormat.TURTLE)).
                thenReturn(exampleDistribution);
    }

    
    /**
     * Check unsupported accept header.
     * 
     * @throws Exception 
     */    
    @Test    
    public void unsupportedAcceptHeader() throws Exception{          
        this.mockMvc.perform(get(
                "/catalogID/datasetID/distributionID").
                accept("application/trig")).andExpect(status().
                        isUnsupportedMediaType());    
    }    
    /**
     * The default content type is text/turtle, when the accept header is not
     * set the default content type is served. This test is excepted to pass.
     * 
     * @throws Exception 
     */    
    @Test    
    public void noAcceptHeader() throws Exception{        
        this.mockMvc.perform(get("/catalogID/datasetID/distributionID")).
                andExpect(status().isOk());  
    }
    
    /**
     * Check supported accept headers.
     * 
     * @throws Exception 
     */    
    @Test    
    public void supportedAcceptHeaders() throws Exception{
        
        MockHttpServletRequest request;
        MockHttpServletResponse response;         
        Object handler;  
        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(
                "/textmining/gene-disease-association_lumc/sparql");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.addHeader(HttpHeaders.ACCEPT, "text/n3");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.addHeader(HttpHeaders.ACCEPT, "application/ld+json");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        request.addHeader(HttpHeaders.ACCEPT, "application/rdf+xml");      
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);          
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
}
