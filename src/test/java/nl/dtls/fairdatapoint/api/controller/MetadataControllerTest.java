/**
 * The MIT License
 * Copyright © 2017 DTL
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
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
package nl.dtls.fairdatapoint.api.controller;

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.filter.CORSFilter;
import nl.dtls.fairdatapoint.api.filter.LoggingFilter;
import nl.dtls.fairdatapoint.service.metadata.MetadataService;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.mockito.Mockito.when;

@DirtiesContext
public abstract class MetadataControllerTest extends BaseIntegrationTest {

    @Autowired
    @Qualifier("requestMappingHandlerAdapter")
    protected RequestMappingHandlerAdapter handlerAdapter;

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    protected RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private MetadataService fairMetaDataService;

    protected final String TEST_FDP_PATH = "/fdp";
    protected final String TEST_CATALOG_PATH = TEST_FDP_PATH + "/catalog/"
            + ExampleFilesUtils.CATALOG_ID;
    protected final String TEST_DATASET_PATH = TEST_FDP_PATH + "/dataset/"
            + ExampleFilesUtils.DATASET_ID;
    protected final String TEST_DATARECORD_PATH = TEST_FDP_PATH + "/datarecord/"
            + ExampleFilesUtils.DATARECORD_ID;
    protected final String TEST_DISTRIBUTION_PATH = TEST_FDP_PATH + "/distribution/"
            + ExampleFilesUtils.DISTRIBUTION_ID;
    protected final ValueFactory f = SimpleValueFactory.getInstance();

    @InjectMocks
    protected final LoggingFilter loggingFilter = new LoggingFilter();

    @InjectMocks
    protected final CORSFilter corsFilter = new CORSFilter();

    @Mock
    private MetadataService fairMDService4MockMVC;

    @InjectMocks
    private FdpController fdpController;

    protected MockMvc mvc;

    @Before
    public void storeExampleMetadata() throws Exception {

        MockitoAnnotations.initMocks(this);
        // setup mockmvc
        mvc = MockMvcBuilders.standaloneSetup(fdpController).build();
        MockHttpServletRequest request = new MockHttpServletRequest();

        // Store fdp metadata
        request.setRequestURI(TEST_FDP_PATH);
        String fdpUri = request.getRequestURL().toString();
        fairMetaDataService.storeFDPMetadata(ExampleFilesUtils.getFDPMetadata(fdpUri));

        // Store catalog metadata
        request.setRequestURI(TEST_CATALOG_PATH);
        String cUri = request.getRequestURL().toString();
        fairMetaDataService.storeCatalogMetadata(
                ExampleFilesUtils.getCatalogMetadata(cUri, fdpUri));

        // Store dataset metadata
        request.setRequestURI(TEST_DATASET_PATH);
        String dUri = request.getRequestURL().toString();
        fairMetaDataService.storeDatasetMetadata(
                ExampleFilesUtils.getDatasetMetadata(dUri, cUri));

        // Store datarecord metadata
        request.setRequestURI(TEST_DATARECORD_PATH);
        String dRecUri = request.getRequestURL().toString();
        fairMetaDataService.storeDataRecordMetadata(
                ExampleFilesUtils.getDataRecordMetadata(dRecUri, dUri));

        // Store distribution metadata
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        String disUri = request.getRequestURL().toString();
        fairMetaDataService.storeDistributionMetadata(
                ExampleFilesUtils.getDistributionMetadata(disUri, dUri));

        when(fairMDService4MockMVC.retrieveFDPMetadata(Mockito.any(IRI.class)))
                .thenReturn(fairMetaDataService.retrieveFDPMetadata(f.createIRI(fdpUri)));

    }


}
