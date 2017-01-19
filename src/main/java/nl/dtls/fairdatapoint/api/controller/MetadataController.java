/**
 * The MIT License
 * Copyright © 2016 DTL
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


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.io.MetadataParserException;
import nl.dtl.fairmetadata.model.Agent;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtl.fairmetadata.model.Identifier;
import nl.dtl.fairmetadata.utils.vocabulary.DataCite;
import nl.dtls.fairdatapoint.api.controller.utils.LoggerUtils;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

@RestController
@Api(description = "FDP metadata")
@RequestMapping(value = "/")
public class MetadataController {

    private final static Logger LOGGER
            = LogManager.getLogger(MetadataController.class);
    @Autowired
    private FairMetaDataService fairMetaDataService;

    private boolean isFDPMetaDataAvailable = false;

    /**
     * To handle GET FDP metadata request. (Note:) The first value in the
     * produces annotation is used as a fallback value, for the request with the
     * accept header value (* / *), manually setting the contentType of the
     * response is not working.
     *
     * @param request Http request
     * @param response Http response
     * @return Metadata about the FDP in one of the acceptable 
     * formats (RDF Turtle, JSON-LD, RDF XML and RDF N3)
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata.io.MetadataException
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
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(uri);
        LoggerUtils.logRequest(LOGGER, request, response);  
        if (metadata == null) {
            String msg = ("No fdp metadata found for the uri : "+ uri);
            throw(new ResourceNotFoundException(msg));
        }
        return metadata;
    }

    /**
     * Get catalog metadata
     *
     * @param catalogID
     * @param request
     * @param response
     * @return Metadata about the catalog in one of the acceptable 
     * formats (RDF Turtle, JSON-LD, RDF XML and RDF N3)
     *
     * @throws IllegalStateException
     * @throws FairMetadataServiceException
     */
    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/catalog/{catalogID}", method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public CatalogMetadata getCatalogMetaData(
            @PathVariable final String catalogID, HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException, 
            ResourceNotFoundException {
        LOGGER.info("Request to get CATALOG metadata with ID ", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        CatalogMetadata metadata = fairMetaDataService.
                retrieveCatalogMetaData(uri);
        LoggerUtils.logRequest(LOGGER, request, response);
        if (metadata == null) {
            String msg = ("No catalog metadata found for the uri : "+ uri);
            throw(new ResourceNotFoundException(msg));
        }
        return metadata;
    }

    /**
     * Get dataset metadata
     *
     * @param datasetID
     * @param request
     * @param response
     * @return Metadata about the dataset in one of the acceptable formats 
     * (RDF Turtle, JSON-LD, RDF XML and RDF N3)
     *
     * @throws FairMetadataServiceException
     */
    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/dataset/{datasetID}",
            method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public DatasetMetadata getDatasetMetaData(
            @PathVariable final String datasetID, HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException, 
            ResourceNotFoundException {
        LOGGER.info("Request to get DATASET metadata with ID ", datasetID);
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        DatasetMetadata metadata = fairMetaDataService.
                retrieveDatasetMetaData(uri);
        LoggerUtils.logRequest(LOGGER, request, response);
        if (metadata == null) {
            String msg = ("No dataset metadata found for the uri : "+ uri);
            throw(new ResourceNotFoundException(msg));
        }
        return metadata;
    }

    /**
     * Get distribution metadata
     *
     * @param distributionID
     * @param request
     * @param response
     * @return Metadata about the dataset distribution in one of the acceptable formats 
     * (RDF Turtle, JSON-LD, RDF XML and RDF N3)
     *
     * @throws FairMetadataServiceException
     */
    @ApiOperation(value = "Dataset distribution metadata")
    @RequestMapping(value = "/distribution/{distributionID}",
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"},
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public DistributionMetadata getDistribution(
            @PathVariable final String distributionID,
            HttpServletRequest request,
            HttpServletResponse response) throws FairMetadataServiceException, 
            ResourceNotFoundException {
        LOGGER.info("Request to get dataset's distribution wih ID ",
                distributionID);
        LOGGER.info("GET : " + request.getRequestURL());
        String uri = getRequesedURL(request);
        DistributionMetadata metadata = fairMetaDataService.
                retrieveDistributionMetaData(uri);
        LoggerUtils.logRequest(LOGGER, request, response);
        if (metadata == null) {
            String msg = ("No distribution metadata found for the uri : "+ uri);
            throw(new ResourceNotFoundException(msg));
        }
        return metadata;
    }
    
    /**
     * To handle POST catalog metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata catalog metadata
     * @return created message
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
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
        String requestedURL = getRequesedURL(request);
        fairMetaDataService.updateFDPMetaData(requestedURL, metadata);
        return "Metadata is updated";
    }

    /**
     * To handle POST catalog metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata catalog metadata
     * @param catalogID Unique catalog ID
     * @return created message
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST catalog metadata")
    @RequestMapping(value = "/catalog", 
            method = RequestMethod.POST, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeCatalogMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) CatalogMetadata metadata,
            @RequestParam("catalogID") String catalogID) throws
            FairMetadataServiceException, MetadataException {
        LOGGER.info("Request to store catalog metatdata with ID ", catalogID);
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }        
        String requestedURL = getRequesedURL(request);
        ValueFactory f = SimpleValueFactory.getInstance();
        IRI uri = f.createIRI(requestedURL + "/" + catalogID);
        metadata.setUri(uri);
        if(metadata.getParentURI() == null){
            String fURI = requestedURL.replace("/catalog", "");            
            LOGGER.info("No fdp uri is provied in the post body. "
                    + "Default fdp uri is used <%s>", fURI);
            IRI fdpURI = f.createIRI(fURI);
            metadata.setParentURI(fdpURI);
        }
        fairMetaDataService.storeCatalogMetaData(metadata);
        return "Metadata is stored";
    }

    /**
     * To handle POST dataset metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata  Dataset metadata
     * @param datasetID Unique dataset ID
     * @return created message
     *
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST dataset metadata")
    @RequestMapping(value = "/dataset", method = RequestMethod.POST,
            consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeDatasetMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) DatasetMetadata metadata,
            @RequestParam("datasetID") String datasetID)
            throws FairMetadataServiceException, MetadataException {        
        LOGGER.info("Request to store dataset metatdata with ID ", datasetID);
        String requestedURL = getRequesedURL(request);
        ValueFactory f = SimpleValueFactory.getInstance();
        IRI uri = f.createIRI(requestedURL + "/" + datasetID);
        metadata.setUri(uri);
        fairMetaDataService.storeDatasetMetaData(metadata);
        return "Metadata is stored";
    }

    /**
     * To handle POST distribution metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param metadata distribution metadata
     * @param distributionID Unique distribution ID
     * @return created message
     *
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST distribution metadata")
    @RequestMapping(value = "/distribution",
            method = RequestMethod.POST, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeDistribution(final HttpServletRequest request,
            HttpServletResponse response, 
            @RequestBody(required = true) DistributionMetadata metadata,
            @RequestParam("distributionID") String distributionID)
            throws FairMetadataServiceException, MetadataException {

        LOGGER.info("Request to store distribution metatdata with ID ",
                distributionID);
        String requestedURL = getRequesedURL(request);
        ValueFactory f = SimpleValueFactory.getInstance();
        IRI uri = f.createIRI(requestedURL + "/" + distributionID);
        metadata.setUri(uri);
        fairMetaDataService.storeDistributionMetaData(metadata);
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
            ValueFactory f = SimpleValueFactory.getInstance();
            metadata.setUri(f.createIRI(fdpUrl));
            metadata.setTitle(f.createLiteral(("FDP of " + host),
                    XMLSchema.STRING));
            metadata.setDescription(f.createLiteral(("FDP of " + host),
                    XMLSchema.STRING));
            metadata.setLanguage(f.createIRI(
                    "http://id.loc.gov/vocabulary/iso639-1/en"));
            metadata.setLicense(f.createIRI(
                    "http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
            metadata.setVersion(f.createLiteral("1.0", XMLSchema.FLOAT));
            metadata.setSwaggerDoc(f.createIRI(fdpUrl + "/swagger-ui.html"));            
            metadata.setInstitutionCountry(f.createIRI(
                    "http://lexvo.org/id/iso3166/NL"));
            Identifier id = new Identifier();
            id.setUri(f.createIRI(fdpUrl + "/metadataID"));
            id.setIdentifier(f.createLiteral("fdp-metadataID", 
                    XMLSchema.STRING)); 
            id.setType(DataCite.RESOURCE_IDENTIFIER);
            metadata.setIdentifier(id);
            Agent publisher = new Agent();
            publisher.setUri(f.createIRI("http://dtls.nl"));
            publisher.setType(FOAF.ORGANIZATION);
            publisher.setName(f.createLiteral("DTLS", XMLSchema.STRING));
            metadata.setPublisher(publisher);
            metadata.setInstitution(publisher);
            Identifier repoId = new Identifier();
            repoId.setUri(f.createIRI(fdpUrl + "/repoID"));
            repoId.setIdentifier(f.createLiteral("fdp-repoID", 
                    XMLSchema.STRING)); 
            repoId.setType(DataCite.RESOURCE_IDENTIFIER);
            metadata.setRepostoryIdentifier(repoId);
            fairMetaDataService.storeFDPMetaData(metadata);
            isFDPMetaDataAvailable = true;
        } catch (MalformedURLException | MetadataException | 
                FairMetadataServiceException ex) {
            throw new MetadataParserException (
                    "Error creating generic FDP meatdata " + ex.getMessage());
        }

    }
}
