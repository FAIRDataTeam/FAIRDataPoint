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
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtls.fairdatapoint.service.metadata.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(description = "FDP metadata")
@RequestMapping("/fdp")
public class FdpController extends MetadataController {

    /**
     * To handle GET FDP metadata request. (Note:) The first value in the produces annotation is
     * used as a fallback value, for the request with the accept header value (* / *), manually
     * setting the contentType of the response is not working.
     *
     * @param request  Http request
     * @param response Http response
     * @return Metadata about the FDP in one of the acceptable formats (RDF Turtle, JSON-LD, RDF XML
     * and RDF N3)
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @ApiOperation(value = "FDP metadata")
    @RequestMapping(method = RequestMethod.GET, produces = {"text/turtle", "application/ld+json",
            "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata getFDPMetaData(final HttpServletRequest request,
                                      HttpServletResponse response) throws MetadataServiceException,
            ResourceNotFoundException, MetadataException {

        LOGGER.info("Request to get FDP metadata, request url : {}", request.getRequestURL());
        String uri = getRequestURL(request);

        return fdpMetadataService.retrieve(VALUEFACTORY.createIRI(uri));
    }

    @ApiIgnore
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlFdpMetadata(HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException, MetadataException {

        LOGGER.info("Request to get FDP metadata, request url : {}", request.getRequestURL());

        ModelAndView mav = new ModelAndView("pages/repository");
        String uri = getRequestURL(request);
        mav.addObject("contextPath", request.getContextPath());

        // Retrieve FDP metadata
        FDPMetadata metadata = fdpMetadataService.retrieve(VALUEFACTORY.createIRI(uri));
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata, RDFFormat.JSONLD,
                MetadataUtils.SCHEMA_DOT_ORG_MODEL));

        // Retrieve Catalogs details
        mav.addObject("catalogs", catalogMetadataService.retrieve(metadata.getCatalogs()));

        // We don't want breadcrumbs on FDP page
        mav.addObject("ignoreBreadcrumbs", true);

        return mav;
    }

    /**
     * To handle POST catalog metadata request.
     *
     * @param request  Http request
     * @param response Http response
     * @param metadata catalog metadata
     * @return created message
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "Update fdp metadata")
    @RequestMapping(method = RequestMethod.PATCH, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata updateFDPMetaData(final HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestBody(required = true) FDPMetadata metadata) throws
            MetadataServiceException {

        IRI uri = VALUEFACTORY.createIRI(getRequestURL(request));

        fdpMetadataService.update(uri, metadata);
        return fdpMetadataService.retrieve(uri);
    }
}
