/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.api.config.RestApiConfiguration;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-06
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiConfiguration.class})
public class FairMetaDataServiceImplTest {
    @Autowired
    private FairMetaDataService fairMetaDataService;
    @Ignore
    @Test
    public void retrieveExitingFDPMetaData() throws FairMetadataServiceException {
        
        String fdpMetadata = fairMetaDataService.
                retrieveFDPMetaData(RDFFormat.TURTLE);        
        assertNotNull(fdpMetadata);        
    }
    
}
