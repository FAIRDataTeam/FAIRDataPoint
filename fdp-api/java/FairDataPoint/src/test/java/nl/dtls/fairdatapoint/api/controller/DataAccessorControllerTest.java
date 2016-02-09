/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller;

import nl.dtls.fairdatapoint.api.config.RestApiConfiguration;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.MediaType;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
    
    @Autowired
    private DataAccessorController controller;
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() throws Exception {        
        MockitoAnnotations.initMocks(this);        
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();   
    }
    
    /**
     * The default content type is text/turtle, when the accept header is not
     * set the default content type is served. This test is excepted to pass.
     * 
     * @throws Exception 
     */
    @Test 
    public void nullAcceptHeader() throws Exception {       
        this.mockMvc.perform(get(
                "/textmining/gene-disease-association_lumc/sparql")).
                andExpect(status().isOk());
    }
    
}
