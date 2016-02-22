/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.utils;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

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
     * @param request   HTTP request
     */
    public static void logRequest(Logger logger, HttpServletRequest request) {
        String message = ("\t" + request.getRemoteAddr() + "\t" + 
                request.getMethod() + "\t" + request.getRequestURL());
         logger.log(Level.getLevel("API-REQUEST"), message);
    }
    
}
