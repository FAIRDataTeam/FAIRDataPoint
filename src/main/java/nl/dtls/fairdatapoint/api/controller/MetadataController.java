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
import nl.dtl.fairmetadata.io.CatalogMetadataParser;
import nl.dtl.fairmetadata.io.DatasetMetadataParser;
import nl.dtl.fairmetadata.io.DistributionMetadataParser;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.io.MetadataParserException;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtl.fairmetadata.utils.MetadataParserUtils;
import nl.dtl.fairmetadata.utils.MetadataUtils;
import nl.dtls.fairdatapoint.api.controller.utils.LoggerUtils;
import nl.dtls.fairdatapoint.api.controller.exception.MetadataControllerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
     * To hander GET fdp metadata request. (Note:) The first value in the
     * produces annotation is used as a fallback value, for the request with the
     * accept header value (* / *), manually setting the contentType of the
     * response is not working.
     *
     * @param request Http request
     * @param response Http response
     * @return Metadata as a RDF string
     *
     * @throws MetadataControllerException
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
            HttpServletResponse response) throws IllegalStateException,
            MetadataControllerException,
            FairMetadataServiceException, MetadataException {

        LOGGER.info("Request to get FDP metadata");
        LOGGER.info("GET : " + request.getRequestURL());
        
        String uri = getRequesedURL(request);
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(uri);
        LoggerUtils.logRequest(LOGGER, request, response);
        return metadata;
    }

    /**
     * Get catalog metadata
     *
     * @param catalogID
     * @param request
     * @param response
     * @return Metadata as a RDF string
     *
     * @throws IllegalStateException
     * @throws MetadataControllerException
     * @throws FairMetadataServiceException
     * @throws MetadataException
     */
    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/{catalogID}", method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public String getCatalogMetaData(
            @PathVariable final String catalogID, HttpServletRequest request,
            HttpServletResponse response) throws IllegalStateException,
            MetadataControllerException, FairMetadataServiceException,
            MetadataException {

        LOGGER.info("Request to get CATALOG metadata with ID ", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        RDFFormat format = getRequestedAcceptHeader(request.getHeader(
                HttpHeaders.ACCEPT));
        String uri = getRequesedURL(request);
        CatalogMetadata metadata = fairMetaDataService.
                retrieveCatalogMetaData(uri);
        String responseBody = MetadataUtils.getString(metadata,
                format);
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    /**
     * Get dataset metadata
     *
     * @param catalogID
     * @param datasetID
     * @param request
     * @param response
     * @return Metadata as a RDF string
     *
     * @throws IllegalStateException
     * @throws MetadataControllerException
     * @throws FairMetadataServiceException
     * @throws MetadataException
     */
    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}",
            method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    @ResponseStatus(HttpStatus.OK)
    public String getDatasetMetaData(@PathVariable final String catalogID,
            @PathVariable final String datasetID, HttpServletRequest request,
            HttpServletResponse response) throws IllegalStateException,
            MetadataControllerException, FairMetadataServiceException,
            MetadataException {

        LOGGER.info("Request to get DATASET metadata with ID ", datasetID);
        LOGGER.info("GET : " + request.getRequestURL());
        RDFFormat format = getRequestedAcceptHeader(request.getHeader(
                HttpHeaders.ACCEPT));
        String uri = getRequesedURL(request);
        DatasetMetadata metadata = fairMetaDataService.
                retrieveDatasetMetaData(uri);
        String responseBody = MetadataUtils.getString(metadata,
                format);
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    /**
     * Get distribution metadata
     *
     * @param catalogID
     * @param datasetID
     * @param distributionID
     * @param request
     * @param response
     * @return Metadata as a RDF string
     *
     * @throws IllegalStateException
     * @throws MetadataControllerException
     * @throws FairMetadataServiceException
     * @throws MetadataException
     */
    @ApiOperation(value = "Dataset distribution metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}/{distributionID}",
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"},
            method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public String getDistribution(@PathVariable final String catalogID,
            @PathVariable final String datasetID,
            @PathVariable final String distributionID,
            HttpServletRequest request,
            HttpServletResponse response) throws IllegalStateException,
            MetadataControllerException, FairMetadataServiceException,
            MetadataException {

        LOGGER.info("Request to get dataset's distribution wih ID ",
                distributionID);
        LOGGER.info("GET : " + request.getRequestURL());
        RDFFormat format = getRequestedAcceptHeader(request.getHeader(
                HttpHeaders.ACCEPT));
        String uri = getRequesedURL(request);
        DistributionMetadata metadata = fairMetaDataService.
                retrieveDistributionMetaData(uri);

        String responseBody = MetadataUtils.getString(metadata,
                format);
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    /**
     * To hander POST catalog metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param catalogMetaData catalog metadata RDF as a string
     * @param catalogID Unique catalog ID
     * @return created message
     *
     * @throws MetadataControllerException
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST catalog metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeCatalogMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) String catalogMetaData,
            @RequestParam("catalogID") String catalogID) throws
            IllegalStateException, MetadataControllerException,
            MetadataParserException, FairMetadataServiceException, 
            MetadataException {

        LOGGER.info("Request to store catalog metatdata with ID ", catalogID);
        RDFFormat format = getContentType(request.getHeader(
                HttpHeaders.CONTENT_TYPE));
        String requestedURL = getRequesedURL(request);
        URI fdpURI = new URIImpl(requestedURL);
        URI catalogURI = new URIImpl(requestedURL + "/" + catalogID);
        if (!isFDPMetaDataAvailable) {
            storeDefaultFDPMetadata(request);
        }
        CatalogMetadataParser parser
                = MetadataParserUtils.getCatalogParser();
        CatalogMetadata metadata = parser.parse(catalogMetaData, catalogID,
                catalogURI, fdpURI, format);
        fairMetaDataService.storeCatalogMetaData(metadata);
        return "Metadata is stored";
    }

    /**
     * To hander POST dataset metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param datasetMetaData dataset metadata RDF as a string
     * @param catalogID Unique catalog ID
     * @param datasetID Unique dataset ID
     * @return created message
     *
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST dataset metadata")
    @RequestMapping(value = "/{catalogID}", method = RequestMethod.POST,
            consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeDatasetMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable final String catalogID,
            @RequestBody(required = true) String datasetMetaData,
            @RequestParam("datasetID") String datasetID)
            throws IllegalStateException, MetadataParserException,
            FairMetadataServiceException, MetadataException {

        LOGGER.info("Request to store dataset metatdata with ID ", datasetID);
        RDFFormat format = getContentType(request.getHeader(
                HttpHeaders.CONTENT_TYPE));
        String requestedURL = getRequesedURL(request);
        URI catalogURI = new URIImpl(requestedURL);
        URI datasetURI = new URIImpl(requestedURL + "/" + datasetID);
        DatasetMetadataParser parser
                = MetadataParserUtils.getDatasetParser();
        DatasetMetadata metadata = parser.parse(datasetMetaData, datasetID,
                datasetURI, catalogURI, format);
        fairMetaDataService.storeDatasetMetaData(metadata);
        return "Metadata is stored";
    }

    /**
     * To hander POST distribution metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param catalogID Unique catalog ID
     * @param datasetID Unique dataset ID
     * @param distributionMetaData distribution metadata RDF as a string
     * @param distributionID Unique distribution ID
     * @return created message
     *
     * @throws nl.dtl.fairmetadata.io.MetadataParserException
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @ApiOperation(value = "POST distribution metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}",
            method = RequestMethod.POST, consumes = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public String storeDistribution(final HttpServletRequest request,
            HttpServletResponse response, @PathVariable final String catalogID,
            @PathVariable final String datasetID,
            @RequestBody(required = true) String distributionMetaData,
            @RequestParam("distributionID") String distributionID)
            throws IllegalStateException, MetadataParserException,
            FairMetadataServiceException, MetadataException {

        LOGGER.info("Request to store distribution metatdata with ID ",
                distributionID);
        RDFFormat format = getContentType(request.getHeader(
                HttpHeaders.CONTENT_TYPE));
        String requestedURL = getRequesedURL(request);
        URI datasetURI = new URIImpl(requestedURL);
        URI distributionURI = new URIImpl(requestedURL + "/" + distributionID);
        DistributionMetadataParser parser = MetadataParserUtils.
                getDistributionParser();
        DistributionMetadata metadata = parser.parse(distributionMetaData,
                distributionID, distributionURI, datasetURI, format);
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
     * @throws MetadataControllerException
     */
    private void storeDefaultFDPMetadata(HttpServletRequest request) throws
            MetadataControllerException {
        LOGGER.info("Creating generic FDP metadata");
        try {
            String fdpUrl = getRequesedURL(request);
            String host = new URL(fdpUrl).getAuthority();
            FDPMetadata metadata = new FDPMetadata();
            metadata.setUri(new URIImpl(fdpUrl));
            metadata.setIdentifier(new LiteralImpl(DigestUtils.md5Hex(fdpUrl),
                    XMLSchema.STRING));
            metadata.setTitle(new LiteralImpl(("FDP of " + host),
                    XMLSchema.STRING));
            metadata.setDescription(new LiteralImpl(("FDP of " + host),
                    XMLSchema.STRING));
            metadata.setLanguage(new URIImpl(
                    "http://id.loc.gov/vocabulary/iso639-1/en"));
            metadata.setLicense(new URIImpl(
                    "http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0"));
            metadata.setVersion(new LiteralImpl("1.0", XMLSchema.FLOAT));
            metadata.setSwaggerDoc(new URIImpl(fdpUrl + "/swagger-ui.html"));
            fairMetaDataService.storeFDPMetaData(metadata);
            isFDPMetaDataAvailable = true;
        } catch (MalformedURLException | MetadataException | 
                FairMetadataServiceException ex) {
            throw new MetadataControllerException(
                    "Error creating generic FDP meatdata " + ex.getMessage());
        }

    }

    private RDFFormat getRequestedAcceptHeader(String accept) {
        if (accept.contentEquals(
                RDFFormat.JSONLD.getDefaultMIMEType())) {
            return RDFFormat.JSONLD;
        } else if (accept.contentEquals(RDFFormat.N3.getDefaultMIMEType())) {
            return RDFFormat.N3;
        } else if (accept.contentEquals(RDFFormat.RDFXML.getDefaultMIMEType())) {
            return RDFFormat.RDFXML;
        }
        return RDFFormat.TURTLE;
    }

    private RDFFormat getContentType(String contentType) {
        if (contentType.contentEquals(RDFFormat.TURTLE.getDefaultMIMEType())) {
            return RDFFormat.TURTLE;
        }
        return null;
    }

}
