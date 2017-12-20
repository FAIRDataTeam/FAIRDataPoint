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
package nl.dtls.fairdatapoint.api.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.io.MetadataParserException;
import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.model.Identifier;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtl.fairmetadata4j.utils.vocabulary.DataCite;
import nl.dtls.fairdatapoint.api.controller.utils.LoggerUtils;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.http.HttpHeaders;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Handle fair metadata api calls
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-11-19
 * @version 0.1
 */

@RestController
@Api(description = "FDP metadata")
@RequestMapping(value = "/")
public class MetadataController {

    private final static Logger LOGGER
            = LogManager.getLogger(MetadataController.class);
    @Autowired
    private FairMetaDataService fairMetaDataService;
    private boolean isFDPMetaDataAvailable = false;
    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * To handle GET FDP metadata request. (Note:) The first value in the
     * produces annotation is used as a fallback value, for the request with the
     * accept header value (* / *), manually setting the contentType of the
     * response is not working.
     *
     * @param request Http request
     * @param response Http response
     * @return Metadata about the FDP in one of the acceptable formats (RDF
     * Turtle, JSON-LD, RDF XML and RDF N3)
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @ApiOperation(value = "FDP metadata")
    @RequestMapping(method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata getFDPMetaData(final HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException,
            ResourceNotFoundException,
            MetadataException {
        LOGGER.info("Request to get FDP metadata");
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(
                valueFactory.createIRI(uri));
        LoggerUtils.logRequest(LOGGER, request, response);
        return metadata;
    }

    @ApiIgnore
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlFdpMetadata(HttpServletRequest request) throws
            FairMetadataServiceException, ResourceNotFoundException,
            MetadataException {
        ModelAndView mav = new ModelAndView("repository");
        LOGGER.info("Request to get FDP metadata");
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(
                valueFactory.createIRI(uri));
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata,
                RDFFormat.JSONLD));
        return mav;
    }

    /**
     * Get catalog metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the catalog in one of the acceptable formats (RDF
     * Turtle, JSON-LD, RDF XML and RDF N3)
     *
     * @throws IllegalStateException
     * @throws FairMetadataServiceException
     */
    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/catalog/{id}", method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public CatalogMetadata getCatalogMetaData(
            @PathVariable final String id, HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException,
            ResourceNotFoundException {
        LOGGER.info("Request to get CATALOG metadata with ID ", id);
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        CatalogMetadata metadata = fairMetaDataService.
                retrieveCatalogMetaData(valueFactory.createIRI(uri));
        LoggerUtils.logRequest(LOGGER, request, response);
        return metadata;
    }

    @ApiIgnore
    @RequestMapping(value = "/catalog/{id}", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlCatalogMetadata(HttpServletRequest request)
            throws FairMetadataServiceException, ResourceNotFoundException,
            MetadataException {
        ModelAndView mav = new ModelAndView("catalog");
        String uri = getRequesedURL(request);
        CatalogMetadata metadata = fairMetaDataService.
                retrieveCatalogMetaData(valueFactory.createIRI(uri));
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata,
                RDFFormat.JSONLD));
        return mav;
    }

    /**
     * Get dataset metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the dataset in one of the acceptable formats (RDF
     * Turtle, JSON-LD, RDF XML and RDF N3)
     *
     * @throws FairMetadataServiceException
     */
    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/dataset/{id}",
            method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public DatasetMetadata getDatasetMetaData(
            @PathVariable final String id, HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException,
            ResourceNotFoundException {
        LOGGER.info("Request to get DATASET metadata with ID ", id);
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        DatasetMetadata metadata = fairMetaDataService.
                retrieveDatasetMetaData(valueFactory.createIRI(uri));
        LoggerUtils.logRequest(LOGGER, request, response);
        return metadata;
    }

    @ApiIgnore
    @RequestMapping(value = "/dataset/{id}", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlDatsetMetadata(HttpServletRequest request)
            throws FairMetadataServiceException, ResourceNotFoundException,
            MetadataException {
        ModelAndView mav = new ModelAndView("dataset");
        String uri = getRequesedURL(request);
        DatasetMetadata metadata = fairMetaDataService.
                retrieveDatasetMetaData(valueFactory.createIRI(uri));
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata,
                RDFFormat.JSONLD));
        return mav;
    }

    /**
     * Get distribution metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the dataset distribution in one of the acceptable
     * formats (RDF Turtle, JSON-LD, RDF XML and RDF N3)
     *
     * @throws FairMetadataServiceException
     */
    @ApiOperation(value = "Dataset distribution metadata")
    @RequestMapping(value = "/distribution/{id}",
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"},
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DistributionMetadata getDistribution(
            @PathVariable final String id,
            HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException,
            ResourceNotFoundException {
        LOGGER.info("Request to get dataset's distribution wih ID ",
                id);
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        DistributionMetadata metadata = fairMetaDataService.
                retrieveDistributionMetaData(valueFactory.createIRI(uri));
        LoggerUtils.logRequest(LOGGER, request, response);
        return metadata;
    }

    @ApiIgnore
    @RequestMapping(value = "/distribution/{id}", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlDistributionMetadata(HttpServletRequest request)
            throws FairMetadataServiceException, ResourceNotFoundException,
            MetadataException {
        ModelAndView mav = new ModelAndView("distribution");
        String uri = getRequesedURL(request);
        DistributionMetadata metadata = fairMetaDataService.
                retrieveDistributionMetaData(valueFactory.createIRI(uri));
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata,
                RDFFormat.JSONLD));
        return mav;
    }

    /**
     * To handle POST catalog metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata catalog metadata
     * @return created message
     * @throws nl.dtl.fairmetadata4j.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "Update fdp metadata")
    @RequestMapping(method = RequestMethod.PATCH, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.OK)
    public String updateFDPMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) FDPMetadata metadata) throws
            FairMetadataServiceException, MetadataException {
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }
        String uri = getRequesedURL(request);
        fairMetaDataService.updateFDPMetaData(valueFactory.createIRI(uri),
                metadata);
        return "Metadata is updated";
    }

    /**
     * To handle POST catalog metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata catalog metadata
     * @param id Unique catalog ID
     * @return created message
     * @throws nl.dtl.fairmetadata4j.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST catalog metadata")
    @RequestMapping(value = "/catalog",
            method = RequestMethod.POST, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeCatalogMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) CatalogMetadata metadata,
            @RequestParam("id") String id) throws
            FairMetadataServiceException, MetadataException {
        String trimmedId = trimmer(id);
        LOGGER.info("Request to store catalog metatdata with ID ", trimmedId);
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }
        String requestedURL = getRequesedURL(request);
        IRI uri = valueFactory.createIRI(requestedURL + "/" + trimmedId);
        metadata.setUri(uri);
        if (metadata.getParentURI() == null) {
            String fURI = requestedURL.replace("/catalog", "");
            LOGGER.info("No fdp uri is provied in the post body. "
                    + "Default fdp uri is used <%s>", fURI);
            IRI fdpURI = valueFactory.createIRI(fURI);
            metadata.setParentURI(fdpURI);
        }
        fairMetaDataService.storeCatalogMetaData(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return "Metadata is stored";
    }

    /**
     * To handle POST dataset metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata Dataset metadata
     * @param id Unique dataset ID
     * @return created message
     *
     * @throws nl.dtl.fairmetadata4j.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST dataset metadata")
    @RequestMapping(value = "/dataset", method = RequestMethod.POST,
            consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeDatasetMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) DatasetMetadata metadata,
            @RequestParam("id") String id)
            throws FairMetadataServiceException, MetadataException {
        String trimmedId = trimmer(id);
        LOGGER.info("Request to store dataset metatdata with ID ", trimmedId);
        String requestedURL = getRequesedURL(request);
        IRI uri = valueFactory.createIRI(requestedURL + "/" + trimmedId);
        metadata.setUri(uri);
        fairMetaDataService.storeDatasetMetaData(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return "Metadata is stored";
    }

    /**
     * To handle POST distribution metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata distribution metadata
     * @param id Unique distribution ID
     * @return created message
     *
     * @throws nl.dtl.fairmetadata4j.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST distribution metadata")
    @RequestMapping(value = "/distribution",
            method = RequestMethod.POST, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeDistribution(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) DistributionMetadata metadata,
            @RequestParam("id") String id)
            throws FairMetadataServiceException, MetadataException {
        String trimmedId = trimmer(id);
        LOGGER.info("Request to store distribution metatdata with ID ",
                trimmedId);
        String requestedURL = getRequesedURL(request);
        IRI uri = valueFactory.createIRI(requestedURL + "/" + trimmedId);
        metadata.setUri(uri);
        fairMetaDataService.storeDistributionMetaData(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return "Metadata is stored";
    }

    /**
     * Get requested URL
     *
     * @param request HttpServletRequest
     * @return URL as a string
     */
    private String getRequesedURL(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * Create and store generic FDP metadata
     *
     * @param request HttpServletRequest
     * @throws MetadataParserException
     */
    private void storeDefaultFDPMetadata(HttpServletRequest request)
            throws MetadataParserException {
        LOGGER.info("Creating generic FDP metadata");
        try {
            String fdpUrl = getRequesedURL(request);
            String host = new URL(fdpUrl).getAuthority();
            FDPMetadata metadata = new FDPMetadata();
            metadata.setUri(valueFactory.createIRI(fdpUrl));
            metadata.setTitle(valueFactory.createLiteral(("FDP of " + host),
                    XMLSchema.STRING));
            metadata.setDescription(valueFactory.createLiteral(
                    ("FDP of " + host), XMLSchema.STRING));
            metadata.setLanguage(valueFactory.createIRI(
                    "http://id.loc.gov/vocabulary/iso639-1/en"));
            metadata.setLicense(valueFactory.createIRI(
                    "http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
            metadata.setVersion(valueFactory.createLiteral(
                    "1.0", XMLSchema.FLOAT));
            metadata.setSwaggerDoc(valueFactory.createIRI(
                    fdpUrl + "/swagger-ui.html"));
            metadata.setInstitutionCountry(valueFactory.createIRI(
                    "http://lexvo.org/id/iso3166/NL"));
            Identifier id = new Identifier();
            id.setUri(valueFactory.createIRI(fdpUrl + "/metadataID"));
            id.setIdentifier(valueFactory.createLiteral("fdp-metadataID",
                    XMLSchema.STRING));
            id.setType(DataCite.RESOURCE_IDENTIFIER);
            metadata.setIdentifier(id);
            Agent publisher = new Agent();
            publisher.setUri(valueFactory.createIRI("http://dtls.nl"));
            publisher.setType(FOAF.ORGANIZATION);
            publisher.setName(valueFactory.createLiteral("DTLS",
                    XMLSchema.STRING));
            metadata.setPublisher(publisher);
            metadata.setInstitution(publisher);
            Identifier repoId = new Identifier();
            repoId.setUri(valueFactory.createIRI(fdpUrl + "/repoID"));
            repoId.setIdentifier(valueFactory.createLiteral("fdp-repoID",
                    XMLSchema.STRING));
            repoId.setType(DataCite.RESOURCE_IDENTIFIER);
            metadata.setRepostoryIdentifier(repoId);
            fairMetaDataService.storeFDPMetaData(metadata);
            isFDPMetaDataAvailable = true;
        } catch (MalformedURLException | MetadataException |
                FairMetadataServiceException ex) {
            throw new MetadataParserException(
                    "Error creating generic FDP meatdata " + ex.getMessage());
        }

    }

    /**
     * Trim white space at start, end and between strings
     *
     * @param str Input string
     * @return Trimmed string
     */
    private String trimmer(String str) {
        String trimmedStr = str;
        trimmedStr = trimmedStr.trim();
        trimmedStr = trimmedStr.replace(" ", "-");
        return trimmedStr;
    }
}
