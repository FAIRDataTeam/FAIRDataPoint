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
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
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
import java.util.UUID;

@RestController
@Api(description = "Distribution Metadata")
@RequestMapping("/fdp/distribution")
public class DistributionController extends MetadataController {

    /**
     * Get distribution metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the dataset distribution in one of the acceptable formats (RDF Turtle,
     * JSON-LD, RDF XML and RDF N3)
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "Dataset distribution metadata")
    @RequestMapping(value = "/{id}", produces = {"text/turtle", "application/ld+json",
            "application/rdf+xml", "text/n3"}, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DistributionMetadata getDistribution(@PathVariable final String id,
                                                HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get distribution metadata, request url : {}", request.getRequestURL());
        return distributionMetadataService.retrieve(getRequestURLasIRI(request));
    }

    @ApiIgnore
    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlDistributionMetadata(HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException, MetadataException {

        ModelAndView mav = new ModelAndView("pages/distribution");
        IRI uri = getRequestURLasIRI(request);
        mav.addObject("contextPath", request.getContextPath());

        // Retrieve Distribution metadata
        DistributionMetadata metadata = distributionMetadataService.retrieve(uri);
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata, RDFFormat.JSONLD,
                MetadataUtils.SCHEMA_DOT_ORG_MODEL));

        // Retrieve parents for breadcrumbs links
        DatasetMetadata dataset = datasetMetadataService.retrieve(metadata.getParentURI());
        CatalogMetadata catalog = catalogMetadataService.retrieve(dataset.getParentURI());
        FDPMetadata repository = fdpMetadataService.retrieve(catalog.getParentURI());
        mav.addObject("repository", repository);
        mav.addObject("catalog", catalog);
        mav.addObject("dataset", dataset);

        return mav;
    }

    /**
     * To handle POST distribution metadata request.
     *
     * @param request  Http request
     * @param response Http response
     * @param metadata distribution metadata
     * @return created message
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "POST distribution metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"}, produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public DistributionMetadata storeDistribution(final HttpServletRequest request,
                                                  HttpServletResponse response, @RequestBody(required = true)
                                                          DistributionMetadata metadata)
            throws MetadataServiceException {

        IRI uri = generateNewIRI(request);
        LOGGER.info("Request to store distribution metadata with IRI {}", uri.toString());
        metadata.setUri(uri);
        distributionMetadataService.store(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return distributionMetadataService.retrieve(uri);
    }

}
