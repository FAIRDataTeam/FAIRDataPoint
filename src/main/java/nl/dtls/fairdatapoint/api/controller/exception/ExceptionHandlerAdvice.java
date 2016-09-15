/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.exception;

import javax.servlet.http.HttpServletResponse;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.io.MetadataParserException;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public ResponseEntity<String> handleResourceNotFound(
            IllegalStateException ex, HttpServletResponse response) { 
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.TEXT_PLAIN);
        String msg =  "Required resource not found, ErrorMsg : " + 
                ex.getMessage();
        return new ResponseEntity<>(msg, headers, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MetadataException.class)
    public ResponseEntity<String> handelBadRequest(MetadataException ex, 
            HttpServletResponse response) {  
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.TEXT_PLAIN);
        String msg =  "ErrorMsg : " + ex.getMessage();
        return new ResponseEntity<>(msg, headers, 
                HttpStatus.BAD_REQUEST);
    } 
        
    @ExceptionHandler({MetadataControllerException.class, 
        FairMetadataServiceException.class, MetadataParserException.class})
    public ResponseEntity<String> handelInternalServerError(Exception ex, 
            HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.TEXT_PLAIN);
        String msg =  "ErrorMsg : " + ex.getMessage();
        return new ResponseEntity<>(msg, headers, 
                HttpStatus.INTERNAL_SERVER_ERROR);
    }    
    
}
