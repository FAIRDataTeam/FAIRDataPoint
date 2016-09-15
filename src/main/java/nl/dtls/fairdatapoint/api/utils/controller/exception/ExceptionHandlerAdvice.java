/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.utils.controller.exception;

import javax.servlet.http.HttpServletResponse;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handle controller exception
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-09-13
 * @version 0.1
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    
    @ExceptionHandler(IllegalStateException.class)
    public @ResponseBody String ResourceNotFound(IllegalStateException ex, 
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return "Required resource not found, ErrorMsg : " + ex.getMessage();
    }
        
    @ExceptionHandler({MetadataControllerException.class, 
        MetadataException.class, FairMetadataServiceException.class})
    public @ResponseBody String InternalServerError(Exception ex, 
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        return "ErrorMsg : " + ex.getMessage();
    }    
    
}
