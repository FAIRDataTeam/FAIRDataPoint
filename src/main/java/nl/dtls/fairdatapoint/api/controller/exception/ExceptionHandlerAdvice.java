/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.api.controller.exception;

import javax.servlet.http.HttpServletResponse;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.io.MetadataParserException;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handle controller exception
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-09-13
 * @version 0.1
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    
    private final static Logger LOGGER
            = LogManager.getLogger(ExceptionHandlerAdvice.class.getName());
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.TEXT_PLAIN);
        String msg = ex.getMessage();
        LOGGER.error(msg);
        return new ResponseEntity<>(msg, headers, 
                HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MetadataException.class)
    public ResponseEntity<String> handelBadRequest(MetadataException ex, 
            HttpServletResponse response) {  
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.TEXT_PLAIN);
        String msg = ex.getMessage();
        LOGGER.error(msg);
        return new ResponseEntity<>(msg, headers, 
                HttpStatus.BAD_REQUEST);
    } 
        
    @ExceptionHandler({FairMetadataServiceException.class, 
        MetadataParserException.class})
    public ResponseEntity<String> handelInternalServerError(Exception ex, 
            HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();    
        headers.setContentType(MediaType.TEXT_PLAIN);
        String msg =  ex.getMessage();
        LOGGER.error(msg);
        return new ResponseEntity<>(msg, headers, 
                HttpStatus.INTERNAL_SERVER_ERROR);
    }    
    
}
