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
package nl.dtls.fairdatapoint.service.impl;

import com.google.common.base.Preconditions;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import nl.dtls.fairdatapoint.service.FairSearchClient;
import org.apache.http.HttpStatus;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service layer implementation for FairSearchClient
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2017-10-30
 * @version 0.1
 */
@Service("fairSearchClientImpl")
public class FairSearchClientImpl implements FairSearchClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FairSearchClientImpl.class);  
    
    @Value("${fairSearch.fdpSubmitUrl:http://example.com/fse}")
    private String fdpSubmitUrl;
    
    /**
     * This method submit FDP url to the FAIR search engine. This method returns the Http response
     * from the search. In case of exception Http Internal server error code (500) is returned.
     * 
     * @param uri   FDP base URI
     * @return Http response code
     */
    
    @Async
    @Override
    public CompletableFuture submitFdpUri(@Nonnull IRI uri) {
        Preconditions.checkState(uri != null, "FDP uri can't be null");
        return CompletableFuture.supplyAsync(() -> {
            try {
                int status = Unirest.get(fdpSubmitUrl).queryString("fdp", uri.toString()).asString()
                        .getStatus();
                LOGGER.info("FDP URL is successfully submitted to search");
                return status;
            } catch (UnirestException ex) {
                String msg = "Error submitting FDP to search. " + ex.getMessage();
                LOGGER.error(msg);
                return HttpStatus.SC_INTERNAL_SERVER_ERROR;
            }
        });
    }
    
}
