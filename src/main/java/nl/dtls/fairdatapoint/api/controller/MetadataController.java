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
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata.io.CatalogMetadataParser;
import nl.dtl.fairmetadata.io.DatasetMetadataParser;
import nl.dtl.fairmetadata.io.DistributionMetadataParser;
import nl.dtl.fairmetadata.io.MetadataParserException;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtl.fairmetadata.utils.MetadataParserUtils;
import nl.dtl.fairmetadata.utils.MetadataUtils;
import nl.dtl.fairmetadata.utils.RDFUtils;
import nl.dtls.fairdatapoint.api.utils.controller.HttpHeadersUtils;
import nl.dtls.fairdatapoint.api.utils.controller.LoggerUtils;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.service.FairMetadataServiceExceptionErrorCode;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @return On success return FDP metadata
     */
    @ApiOperation(value = "FDP metadata")
    @RequestMapping(method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    public String getFDAMetaData(final HttpServletRequest request,
            HttpServletResponse response) {
        String responseBody;
        LOGGER.info("Request to get FDP metadata");
        LOGGER.info("GET : " + request.getRequestURL());
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requestedContentType = HttpHeadersUtils.
                getRequestedAcceptHeader(contentType);
        String fdpURI = getRequesedURL(request);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            FDPMetadata metadata = fairMetaDataService.
                    retrieveFDPMetaData(fdpURI);
            if(metadata == null) {
                responseBody = HttpHeadersUtils.set404ResponseHeaders(
                        response);
            }
            else {
               responseBody = MetadataUtils.getString(metadata, 
                       requestedContentType);
                HttpHeadersUtils.set200ResponseHeaders(responseBody, response,
                    requestedContentType); 
            }           
        } catch (Exception ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
        }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    /**
     * To hander POST catalog metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param catalogMetaData Content for catalog metadata
     * @param catalogID Unique catalog ID
     * @return On success return FDP metadata
     */
    @ApiOperation(value = "POST catalog metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"})
    public String storeCatalogMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = true) String catalogMetaData,
            @RequestParam("catalogID") String catalogID) {        
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        RDFFormat format = HttpHeadersUtils.getContentType(contentType);
        String requestURL = getRequesedURL(request);
        URI fdpURI = new URIImpl(requestURL);
        URI catalogURI = new URIImpl(requestURL + "/" + catalogID);
        String responseBody;
        LOGGER.info("Request to store catalog metatdata with ID = ", catalogID);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            CatalogMetadataParser parser = 
                    MetadataParserUtils.getCatalogParser();
            CatalogMetadata metadata = parser.parse(catalogMetaData, catalogID, 
                    catalogURI, fdpURI, format);
            metadata.setIssued(RDFUtils.getCurrentTime());
            fairMetaDataService.storeCatalogMetaData(metadata);
            responseBody = HttpHeadersUtils.set201ResponseHeaders(response);
        } catch(FairMetadataServiceException ex) {
            if(ex.getErrorCode() == 
                    FairMetadataServiceExceptionErrorCode.RESOURCE_EXIST) {
                responseBody = HttpHeadersUtils.set409ResponseHeaders(
                    response, ex);            
            } else {
                responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
            }
        
        } catch (DatatypeConfigurationException | MalformedURLException ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
        } catch (MetadataParserException ex) {
            responseBody = HttpHeadersUtils.set400ResponseHeaders(
                    response, ex);
        }
        return responseBody;
    }

    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/{catalogID}", method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    public String getCatalogMetaData(
            @PathVariable final String catalogID, HttpServletRequest request,
            HttpServletResponse response) {
        LOGGER.info("Request to get CATALOG metadata {}", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody;
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HttpHeadersUtils.
                getRequestedAcceptHeader(contentType);
        String catalogURI = getRequesedURL(request);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            CatalogMetadata metadata = fairMetaDataService.
                    retrieveCatalogMetaData(catalogURI);            
            if(metadata == null) {
                responseBody = HttpHeadersUtils.set404ResponseHeaders(
                        response);
            }
            else {
               responseBody = MetadataUtils.getString(metadata, 
                       requesetedContentType);
                HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                        requesetedContentType); 
            }
        } catch (Exception ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
        }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    /**
     * To hander POST dataset metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param datasetMetaData dataset metadata in RDF
     * @param catalogID Unique catalog ID
     * @param datasetID Unique dataset ID
     * @return On success return FDP metadata
     */
    @ApiOperation(value = "POST dataset metadata")
    @RequestMapping(value = "/{catalogID}", method = RequestMethod.POST,
            consumes = {"text/turtle"})
    public String storeDatasetMetaData(final HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable final String catalogID,
            @RequestBody(required = true) String datasetMetaData,
            @RequestParam("datasetID") String datasetID) {
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        RDFFormat format = HttpHeadersUtils.getContentType(contentType);
        String requestURL = getRequesedURL(request);
        URI catalogURI = new URIImpl(requestURL);
        URI datasetURI = new URIImpl(requestURL +  "/" + datasetID);
        String responseBody = null;        
        LOGGER.info("Request to store dataset metatdata with ID = ", datasetID);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            DatasetMetadataParser parser = 
                    MetadataParserUtils.getDatasetParser();
            DatasetMetadata metadata = parser.parse(datasetMetaData, datasetID, 
                    datasetURI, catalogURI, format);
            metadata.setIssued(RDFUtils.getCurrentTime());
            fairMetaDataService.storeDatasetMetaData(metadata);
            responseBody = HttpHeadersUtils.set201ResponseHeaders(response);
        } catch(FairMetadataServiceException ex) {
            if(ex.getErrorCode() == 
                    FairMetadataServiceExceptionErrorCode.RESOURCE_EXIST) {
                responseBody = HttpHeadersUtils.set409ResponseHeaders(
                    response, ex);            
            } else {
                responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
            }        
        } catch (DatatypeConfigurationException | MalformedURLException ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
        } catch (MetadataParserException ex) {
            responseBody = HttpHeadersUtils.set400ResponseHeaders(
                    response, ex);
        }
        return responseBody;
    }

    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}",
            method = RequestMethod.GET,
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    public String getDatasetMetaData(@PathVariable final String catalogID,
            @PathVariable final String datasetID, HttpServletRequest request,
            HttpServletResponse response) {
        LOGGER.info("Request to get DATASET metadata {}", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody;
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HttpHeadersUtils.
                getRequestedAcceptHeader(contentType);
        String datasetURI = getRequesedURL(request);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            DatasetMetadata metadata = fairMetaDataService.
                    retrieveDatasetMetaData(datasetURI);
            if(metadata == null) {
                responseBody = HttpHeadersUtils.set404ResponseHeaders(
                        response);
            }
            else {
               responseBody = MetadataUtils.getString(metadata, 
                       requesetedContentType);
                HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                        requesetedContentType); 
            }
        } catch (Exception ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
        }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    /**
     * To hander POST distribution metadata request.
     *
     * @param request Http request
     * @param response Http response
     * @param catalogID Unique catalog ID
     * @param datasetID Unique dataset ID
     * @param distributionMetaData
     * @param distributionID Unique distribution ID
     * @return On success return FDP metadata
     */
    @ApiOperation(value = "POST distribution metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}",
            method = RequestMethod.POST, consumes = {"text/turtle"})
    public String storeDatasetDistribution(final HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable final String catalogID,
            @PathVariable final String datasetID,
            @RequestBody(required = true) String distributionMetaData,
            @RequestParam("distributionID") String distributionID) {
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        RDFFormat format = HttpHeadersUtils.getContentType(contentType);
        String requestURL = getRequesedURL(request);
        URI datasetURI = new URIImpl(requestURL);
        URI distributionURI = new URIImpl(requestURL +  "/" + distributionID);
        String responseBody = null;        
        LOGGER.info("Request to store distribution metatdata with ID = ", 
                distributionID);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            DistributionMetadataParser parser = MetadataParserUtils.
                    getDistributionParser();
            DistributionMetadata metadata = parser.parse(distributionMetaData, 
                    distributionID, distributionURI, datasetURI, format);
            metadata.setIssued(RDFUtils.getCurrentTime());
            fairMetaDataService.storeDistributionMetaData(metadata);
            responseBody = HttpHeadersUtils.set201ResponseHeaders(response);
        } catch(FairMetadataServiceException ex) {
            if(ex.getErrorCode() == 
                    FairMetadataServiceExceptionErrorCode.RESOURCE_EXIST) {
                responseBody = HttpHeadersUtils.set409ResponseHeaders(
                    response, ex);            
            } else {
                responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
            }
        
        } catch (DatatypeConfigurationException | MalformedURLException ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                    response, ex);
        } catch (MetadataParserException ex) {
            responseBody = HttpHeadersUtils.set400ResponseHeaders(
                    response, ex);
        }
        return responseBody;
    }

    @ApiOperation(value = "Dataset distribution metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}/{distributionID}",
            produces = {"text/turtle",
                "application/ld+json", "application/rdf+xml", "text/n3"},
            method = RequestMethod.GET)
    public String getDatasetDistribution(@PathVariable final String catalogID,
            @PathVariable final String datasetID,
            @PathVariable final String distributionID,
            HttpServletRequest request,
            HttpServletResponse response) {

        LOGGER.info("Request to get dataset's distribution {}", distributionID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody = null;
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HttpHeadersUtils.
                getRequestedAcceptHeader(acceptHeader);
        String distributionURI = getRequesedURL(request);
        try {
            if (!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            DistributionMetadata metadata = fairMetaDataService.
                    retrieveDistributionMetaData(distributionURI);
            if(metadata == null) {
                responseBody = HttpHeadersUtils.set404ResponseHeaders(
                        response);
            }
            else {
               responseBody = MetadataUtils.getString(metadata, 
                       requesetedContentType);
                HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                        requesetedContentType); 
            }
        } catch (Exception ex) {
            HttpHeadersUtils.set500ResponseHeaders(response, ex);
        }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }

    private String getRequesedURL(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private void createFDPMetaData(HttpServletRequest request) throws
            MalformedURLException, DatatypeConfigurationException,
            FairMetadataServiceException {     
        LOGGER.info("Creating simple FDP metadata");        
        FDPMetadata metadata = new FDPMetadata();          
        String fdpUrl = getRequesedURL(request).split("/fdp")[0];
        fdpUrl = fdpUrl + "/fdp"; 
        metadata.setUri(new URIImpl(fdpUrl));
        String host = new URL(fdpUrl).getAuthority();
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
        metadata.setIssued(RDFUtils.getCurrentTime());
        metadata.setModified(RDFUtils.getCurrentTime());
        metadata.setSwaggerDoc(new URIImpl(fdpUrl + "/swagger-ui.html"));
        fairMetaDataService.storeFDPMetaData(metadata);
        isFDPMetaDataAvailable = true;
    }

}
