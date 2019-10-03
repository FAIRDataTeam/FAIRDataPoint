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
package nl.dtls.fairdatapoint.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtls.fairdatapoint.service.metadata.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(description = "Data Record Metadata")
@RequestMapping("/fdp/datarecord")
public class DataRecordController extends MetadataController {

    /**
     * Get datarecord metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the dataset in one of the acceptable formats (RDF Turtle, JSON-LD, RDF
     * XML and RDF N3)
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = {"text/turtle", "application/ld+json", "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public DataRecordMetadata getDataRecordMetaData(@PathVariable final String id,
                                                    HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get DATARECORD metadata,request url : {}", request.getRequestURL());
        return dataRecordMetadataService.retrieve(getRequestURLasIRI(request));
    }

    @ApiIgnore
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlDataRecordMetadata(HttpServletRequest request) throws
            ResourceNotFoundException, MetadataException, MetadataServiceException {

        ModelAndView mav = new ModelAndView("dataset");
        IRI uri = getRequestURLasIRI(request);
        DataRecordMetadata metadata = dataRecordMetadataService.retrieve(uri);
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata, RDFFormat.JSONLD));

        mav.addObject("contextPath", request.getContextPath());

        return mav;
    }

    /**
     * To handle POST datarecord metadata request.
     *
     * @param request  Http request
     * @param response Http response
     * @param metadata datarecord metadata
     * @return created message
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "POST datarecord metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public DataRecordMetadata storeDataRecord(final HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody(required = true) DataRecordMetadata metadata)
            throws MetadataServiceException {

        IRI uri = generateNewIRI(request);
        LOGGER.info("Request to store datarecord metatdata with IRI {}", uri.toString());
        metadata.setUri(uri);
        dataRecordMetadataService.store(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return dataRecordMetadataService.retrieve(uri);
    }

}
