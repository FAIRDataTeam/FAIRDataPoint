/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.utils;


import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.openrdf.rio.RDFFormat;
import org.springframework.http.MediaType;

/**
 * Handles http headers
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-07
 * @version 0.1
 */
public class HttpHeadersUtils {
    
    private final static org.apache.logging.log4j.Logger LOGGER 
            = LogManager.getLogger(HttpHeadersUtils.class);
    
    public final static String[] SUPPORTED_HEADERS = { "text/turtle", 
        "application/ld+json", "application/rdf+xml", "text/n3"};
    
    /**
     * Set response header for the internal server errors
     * 
     * @param response  Http response
     * @param ex    Server exception
     * @return returns null (as a response body)
     */
    public static String set500ResponseHeaders(HttpServletResponse 
            response, Exception ex) {
        String errorMessage = ("Internal server error; Error message : " 
                + ex.getMessage());              
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    errorMessage);
        } catch (IOException ex1) {
            LOGGER.warn("Error setting error message for internal server "
                    + "error; The error : " + ex1.getMessage());
        }
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return null;
    }
    
    /**
     * Set response header for the successful call
     * 
     * @param responseBody ResponseBody
     * @param response  Http response
     * @param requesetedContentType Requeseted ContentType(i.e Accept header)
     */
    public static void set200ResponseHeaders(String responseBody, 
            HttpServletResponse response, RDFFormat requesetedContentType) {   
        if (responseBody == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);            
        }
        else {
            response.setStatus(HttpServletResponse.SC_OK);                
            response.setContentType(requesetedContentType.
                    getDefaultMIMEType()); 
        }
    }
    
    public static RDFFormat getRequestedAcceptHeader(String contentType) {        
        RDFFormat requesetedContentType = null; 
        if (contentType == null || contentType.isEmpty()) {
            requesetedContentType = RDFFormat.TURTLE;
        }
        else if (contentType.contentEquals(
                RDFFormat.TURTLE.getDefaultMIMEType()) ||         
                contentType.contains(MediaType.ALL_VALUE)) {
            requesetedContentType = RDFFormat.TURTLE;
        }
        else if (contentType.contentEquals(
                RDFFormat.JSONLD.getDefaultMIMEType())) {
            requesetedContentType = RDFFormat.JSONLD;
        }
        else if (contentType.contentEquals(
                RDFFormat.N3.getDefaultMIMEType())) {
            requesetedContentType = RDFFormat.N3;
        }
        else if (contentType.contentEquals(
                RDFFormat.RDFXML.getDefaultMIMEType())) {
            requesetedContentType = RDFFormat.RDFXML;
        }
        return requesetedContentType;
    }
    
}
