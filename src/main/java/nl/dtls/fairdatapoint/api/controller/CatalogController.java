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
package nl.dtls.fairdatapoint.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
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
import java.util.ArrayList;
import java.util.UUID;

import static nl.dtls.fairdatapoint.util.ThrowingFunction.suppress;

@RestController
@Api(description = "FDP metadata")
@RequestMapping("/fdp/catalog")
public class CatalogController extends MetadataController {

    /**
     * Get catalog metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the catalog in one of the acceptable formats (RDF Turtle, JSON-LD, RDF
     * XML and RDF N3)
     * @throws IllegalStateException
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"text/turtle",
            "application/ld+json", "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public CatalogMetadata getCatalogMetaData(@PathVariable final String id,
                                              HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get CATALOG metadata, request url : {}", request.getRequestURL());
        String uri = getRequesedURL(request);
        CatalogMetadata metadata = fairMetaDataService
                .retrieveCatalogMetadata(VALUEFACTORY.createIRI(uri));
        return metadata;
    }

    @ApiIgnore
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlCatalogMetadata(HttpServletRequest request)
            throws MetadataServiceException, ResourceNotFoundException, MetadataException {

        ModelAndView mav = new ModelAndView("pages/catalog");
        String uri = getRequesedURL(request);
        mav.addObject("contextPath", request.getContextPath());

        // Retrieve Catalog metadata
        CatalogMetadata metadata = fairMetaDataService.retrieveCatalogMetadata(VALUEFACTORY.createIRI(uri));
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata, RDFFormat.JSONLD,
                MetadataUtils.SCHEMA_DOT_ORG_MODEL));

        // Retrieve parent for breadcrumbs
        FDPMetadata repository = fairMetaDataService.retrieveFDPMetadata(metadata.getParentURI());
        mav.addObject("repository", repository);

        // Retrieve Datasets details
        mav.addObject("datasets", retrieveMetadata(metadata.getDatasets(),
                suppress(fairMetaDataService::retrieveDatasetMetadata)));

        return mav;
    }

    /**
     * To handle POST catalog metadata request.
     *
     * @param request  Http request
     * @param response Http response
     * @param metadata catalog metadata
     * @return created message
     * @throws nl.dtl.fairmetadata4j.io.MetadataParserException
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "POST catalog metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogMetadata storeCatalogMetaData(final HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody(required = true) CatalogMetadata metadata)
            throws MetadataServiceException, MetadataException {
        String requestedURL = getRequesedURL(request);
        String fURI = requestedURL.replace("/catalog", "");

        UUID uid = UUID.randomUUID();
        LOGGER.info("Request to store catalog metatdata with ID {}", uid.toString());
        IRI uri = VALUEFACTORY.createIRI(requestedURL + "/" + uid.toString());
        metadata.setUri(uri);
        IRI fdpURI = VALUEFACTORY.createIRI(fURI);

        // Set parent uri
        metadata.setParentURI(fdpURI);

        // Ignore children links
        metadata.setDatasets(new ArrayList());

        fairMetaDataService.storeCatalogMetadata(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return fairMetaDataService.retrieveCatalogMetadata(uri);
    }

}
