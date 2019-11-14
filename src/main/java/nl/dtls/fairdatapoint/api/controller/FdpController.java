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
package nl.dtls.fairdatapoint.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.metadata.FdpMetadataDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.repository.FdpMetadataMapper;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Api(description = "FDP metadata")
@RequestMapping("/fdp")
public class FdpController extends MetadataController {

    @Autowired
    private FdpMetadataMapper fdpMetadataMapper;

    @RequestMapping(method = RequestMethod.GET, headers = {"Accept=application/json"})
    public ResponseEntity<FdpMetadataDTO> getFdpMetadata(HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException {

        String uri = getRequestURL(request);
        FDPMetadata metadata = fdpMetadataService.retrieve(VALUEFACTORY.createIRI(uri));
        List<CatalogMetadata> catalogs = catalogMetadataService.retrieve(metadata.getCatalogs());
        FdpMetadataDTO dto = fdpMetadataMapper.toDTO(metadata, catalogs);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "FDP metadata")
    @RequestMapping(method = RequestMethod.GET, headers = {"Accept=*/*"}, produces = {"text/turtle", "application/ld" +
            "+json",
            "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata getFDPMetaData(final HttpServletRequest request,
                                      HttpServletResponse response) throws MetadataServiceException,
            ResourceNotFoundException {

        LOGGER.info("Request to get FDP metadata, request url : {}", request.getRequestURL());
        String uri = getRequestURL(request);
        return fdpMetadataService.retrieve(VALUEFACTORY.createIRI(uri));
    }

    @ApiOperation(value = "Update fdp metadata")
    @RequestMapping(method = RequestMethod.PATCH, headers = {"Accept=*/*"}, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata updateFDPMetaData(final HttpServletRequest request, @RequestBody FDPMetadata metadata)
            throws MetadataServiceException {

        IRI uri = VALUEFACTORY.createIRI(getRequestURL(request));
        fdpMetadataService.update(uri, metadata);
        return fdpMetadataService.retrieve(uri);
    }
}
