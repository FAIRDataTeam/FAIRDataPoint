/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.utils;


import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.openrdf.rio.RDFFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

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
        response.setHeader(HttpHeaders.CONTENT_TYPE, 
                MediaType.TEXT_PLAIN_VALUE);
        return null;
    }
    
    public static void set200ResponseHeaders(String responseBody, 
            HttpServletResponse response, RDFFormat requesetedContentType) {   
        if (responseBody == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setHeader(HttpHeaders.CONTENT_TYPE, 
                    MediaType.TEXT_PLAIN_VALUE);            
        }
        else {
            response.setStatus(HttpServletResponse.SC_OK);                
            response.setHeader(HttpHeaders.CONTENT_TYPE, 
                    requesetedContentType.getDefaultMIMEType()); 
        }
    }
    
    public static void setMandatoryResponseHeaders(HttpServletResponse 
            response) { 
        response.setHeader(HttpHeaders.SERVER, "FAIR data point (JAVA)");             
        response.setHeader(HttpHeaders.ALLOW, RequestMethod.GET.name());
    }
    
    public static RDFFormat requestedAcceptHeader(String contentType) {        
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
