/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;

/**
 * Handles loggers
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-22
 * @version 0.1
 */
public class LoggerUtils {
    
    /**
     * Log the request.
     * 
     * Log message pattern [Time\t IP\t requestMethod\t requestedURL]
     * @param logger    Class logger
     * @param request   Client request
     * @param response  Server response
     */
    public static void logRequest(Logger logger, HttpServletRequest request,
            HttpServletResponse response) {
        ThreadContext.put("requestMethod", request.getMethod());
        ThreadContext.put("requestURI", request.getRequestURI());
        ThreadContext.put("requestProtocol", request.getProtocol());
        ThreadContext.put("responseStatus", String.valueOf(
                response.getStatus()));
        String contentLength = response.getHeader(HttpHeaders.CONTENT_LENGTH);
        ThreadContext.put("contentSize", contentLength);
        logger.log(Level.getLevel("API-REQUEST"), "");
    }
    
}
