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
package nl.dtls.fairdatapoint.acceptance.exception;

import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairdatapoint.api.controller.exception.ExceptionControllerAdvice;
import nl.dtls.fairdatapoint.api.controller.metadata.catalog.CatalogController;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.http.HttpHeaders;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExceptionControllerAdviceTest {

    private static final String PATH = "/fdp/catalog/invalid";

    @Mock
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @InjectMocks
    private CatalogController catalogController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(catalogController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    public void notFoundExceptionHandlerError() throws Exception {
        Mockito.when(catalogMetadataService.retrieve(Mockito.any(IRI.class)))
                .thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get(PATH)).andExpect(status().is(HttpStatus.NOT_FOUND.value())).andReturn();
    }

    @Disabled
    @Test
    public void badRequestExceptionHandlerError() throws Exception {

        doThrow(new MetadataException("Invalid metadata"))
                .when(catalogMetadataService).store(Mockito.any(CatalogMetadata.class));

        mockMvc.perform(post("/fdp/catalog")
                .content(ExampleFilesUtils
                        .getFileContentAsString(ExampleFilesUtils.CATALOG_METADATA_FILE))
                .param("id", "test")
                .accept(RDFFormat.TURTLE.getDefaultMIMEType())
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();
    }

    @Test
    public void internalServerErrorExceptionHandlerError() throws Exception {
        Mockito.when(catalogMetadataService.retrieve(Mockito.any(IRI.class)))
                .thenThrow(new MetadataServiceException("Internal server error"));

        mockMvc.perform(get(PATH)).andExpect(status()
                .is(HttpStatus.INTERNAL_SERVER_ERROR.value())).andReturn();
    }

}
