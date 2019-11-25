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
package nl.dtls.fairdatapoint.api.controller.metadata.datarecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtls.fairdatapoint.api.controller.metadata.MetadataController;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(description = "Data Record Metadata")
@RequestMapping("/fdp/datarecord")
public class DataRecordController extends MetadataController {

    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=*/*"},
            produces = {"text/turtle", "application/ld+json", "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public DataRecordMetadata getDataRecordMetaData(@PathVariable final String id,
                                                    HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get DATARECORD metadata,request url : {}", request.getRequestURL());
        return dataRecordMetadataService.retrieve(getRequestURLasIRI(request));
    }

    @ApiOperation(value = "POST datarecord metadata")
    @RequestMapping(method = RequestMethod.POST, consumes = {"text/turtle"}, headers = {"Accept=*/*"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public DataRecordMetadata storeDataRecord(final HttpServletRequest request,
                                              HttpServletResponse response,
                                              @RequestBody DataRecordMetadata metadata)
            throws MetadataServiceException {

        IRI uri = generateNewIRI(request);
        LOGGER.info("Request to store datarecord metatdata with IRI {}", uri.toString());
        metadata.setUri(uri);
        dataRecordMetadataService.store(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return dataRecordMetadataService.retrieve(uri);
    }

}
