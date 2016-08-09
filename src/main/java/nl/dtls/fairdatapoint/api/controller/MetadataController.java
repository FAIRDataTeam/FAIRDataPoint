/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtls.fairdatapoint.api.controller.utils.HttpHeadersUtils;
import nl.dtls.fairdatapoint.api.controller.utils.LoggerUtils;
import nl.dtls.fairdatapoint.service.CatalogMetadata;
import nl.dtls.fairdatapoint.service.CatalogMetadataExeception;
import nl.dtls.fairdatapoint.service.FDPMetaData;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    
    private boolean isFDPMetaDataAvailable = true;

    /**
     * To hander GET fdp metadata request.
     * (Note:) The first value in the produces annotation is used as a fallback
     * value, for the request with the accept header value (* / *),
     * manually setting the contentType of the response is not working.
     * 
     * @param request   Http request
     * @param response  Http response   
     * @return  On success return FDP metadata
     */
    @ApiOperation(value = "FDP metadata")
    @RequestMapping(method = RequestMethod.GET,
            produces = { "text/turtle", 
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
        try { 
            if(!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            responseBody = fairMetaDataService.retrieveFDPMetaData(
                    requestedContentType);
            HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                    requestedContentType);        
            } catch (FairMetadataServiceException | MalformedURLException | 
                    DatatypeConfigurationException ex) {            
                responseBody = HttpHeadersUtils.set500ResponseHeaders(
                        response, ex);
            }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }
    
    /**
     * To hander POST catalog metadata request.
     * 
     * @param request   Http request
     * @param response  Http response   
     * @param catalogMetaData Content for catalog metadata  
     * @param catalogID Unique catalog ID  
     * @return  On success return FDP metadata
     */
    @ApiOperation(value = "POST catalog metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"})    
    public String storeCatalogMetaData(final HttpServletRequest request,
                    HttpServletResponse response, 
                    @RequestBody(required = true) String catalogMetaData,
                    @RequestParam("catalogID") String catalogID) {
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        RDFFormat format = HttpHeadersUtils.getContentType(contentType);
        String fdpURI = getRequesedURL(request);
        String responseBody;
        try {
            if(!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            CatalogMetadata cMetadata = new CatalogMetadata(catalogMetaData,
                    catalogID, fdpURI, format);
            fairMetaDataService.storeCatalogMetaData(cMetadata);
            responseBody = HttpHeadersUtils.set201ResponseHeaders(response);
        } catch (DatatypeConfigurationException | 
                FairMetadataServiceException | MalformedURLException ex) {
            responseBody = HttpHeadersUtils.set500ResponseHeaders(
                        response, ex);
        } catch (CatalogMetadataExeception ex) {
            responseBody = HttpHeadersUtils.set400ResponseHeaders(
                        response, ex);
        }
        return responseBody;
    }
        
    @ApiOperation(value = "Catalog metadata")
    @RequestMapping(value = "/{catalogID}", method = RequestMethod.GET,
            produces = { "text/turtle", 
        "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    public String getCatalogMetaData(
            @PathVariable final String catalogID, HttpServletRequest request,
                    HttpServletResponse response) {
        LOGGER.info("Request to get CATALOG metadata {}", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody;
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HttpHeadersUtils.getRequestedAcceptHeader(contentType);   
        try { 
            if(!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            responseBody = fairMetaDataService.                        
                    retrieveCatalogMetaData(catalogID, requesetedContentType);
                HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                        requesetedContentType);
            } catch (FairMetadataServiceException  | MalformedURLException | 
                    DatatypeConfigurationException ex) {
                responseBody = HttpHeadersUtils.set500ResponseHeaders(
                        response, ex);
            }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }
    
    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}", 
            method = RequestMethod.GET,
            produces = { "text/turtle", 
        "application/ld+json", "application/rdf+xml", "text/n3"}
    )
    public String getDatasetMetaData(@PathVariable final String catalogID,
            @PathVariable final String datasetID, HttpServletRequest request,
                    HttpServletResponse response) {  
        LOGGER.info("Request to get DATASET metadata {}", catalogID);
        LOGGER.info("GET : " + request.getRequestURL());
        String responseBody;
        String contentType = request.getHeader(HttpHeaders.ACCEPT);
        RDFFormat requesetedContentType = HttpHeadersUtils.getRequestedAcceptHeader(contentType);    
        try {  
            if(!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            responseBody = fairMetaDataService.retrieveDatasetMetaData(
                    catalogID, datasetID, requesetedContentType);                
            HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                    requesetedContentType);
            } catch (FairMetadataServiceException | MalformedURLException | 
                    DatatypeConfigurationException ex) {                
                responseBody = HttpHeadersUtils.set500ResponseHeaders(
                        response, ex);
            }
        LoggerUtils.logRequest(LOGGER, request, response);
        return responseBody;
    }    
    
    @ApiOperation(value = "Dataset distribution metadata")
    @RequestMapping(value = "/{catalogID}/{datasetID}/{distributionID}",
            produces = { "text/turtle", 
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
        try {
            if(!isFDPMetaDataAvailable) {
                createFDPMetaData(request);
            }
            responseBody = fairMetaDataService.retrieveDatasetDistribution(                       
                    catalogID, datasetID, distributionID, 
                    requesetedContentType);                
            HttpHeadersUtils.set200ResponseHeaders(responseBody, response, 
                    requesetedContentType);            
        } catch (FairMetadataServiceException | MalformedURLException | 
                    DatatypeConfigurationException ex) {                
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
        String requestURL = getRequesedURL(request);
        String fdpUrl = requestURL.split("/fdp")[0];
        fdpUrl = fdpUrl + "/fdp";
        FDPMetaData fdpMetaData = new FDPMetaData(fdpUrl);
        LOGGER.info("Creating simple FDP metadata");
        fairMetaDataService.storeFDPMetaData(fdpMetaData);
        isFDPMetaDataAvailable = true;
    }
    
}
