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
package nl.dtls.fairdatapoint.api.controller.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.controller.metadata.MetadataController;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.repository.RepositoryMetadataMapper;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
public class RepositoryController extends MetadataController {

    @Autowired
    private RepositoryMetadataMapper repositoryMetadataMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/spec", method = RequestMethod.GET, headers = {"Accept=application/json"})
    @ResponseBody
    public Object getFormMetadata() {
        Resource resource = new ClassPathResource("form-specs/repository-spec.json");
        try {
            return objectMapper.readValue(resource.getInputStream(), Object.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, headers = {"Accept=application/json"})
    public ResponseEntity<RepositoryMetadataDTO> getRepositoryMetadata(HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException {

        String uri = getRequestURL(request);
        FDPMetadata metadata = repositoryMetadataService.retrieve(VALUEFACTORY.createIRI(uri));
        List<CatalogMetadata> catalogs = catalogMetadataService.retrieve(metadata.getCatalogs());
        RepositoryMetadataDTO dto = repositoryMetadataMapper.toDTO(metadata, catalogs);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, headers = {"Accept=*/*"}, produces = {"text/turtle", "application/ld" +
            "+json",
            "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata getRepositoryMetaData(final HttpServletRequest request,
                                             HttpServletResponse response) throws MetadataServiceException,
            ResourceNotFoundException {

        LOGGER.info("Request to get FDP metadata, request url : {}", request.getRequestURL());
        String uri = getRequestURL(request);
        return repositoryMetadataService.retrieve(VALUEFACTORY.createIRI(uri));
    }

    @RequestMapping(method = RequestMethod.PATCH, headers = {"Accept=text/turtle"}, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.OK)
    public FDPMetadata updateRepositoryMetaData(final HttpServletRequest request, @RequestBody FDPMetadata metadata)
            throws MetadataServiceException {

        IRI uri = VALUEFACTORY.createIRI(getRequestURL(request));
        repositoryMetadataService.update(uri, metadata);
        return repositoryMetadataService.retrieve(uri);
    }

    @RequestMapping(method = RequestMethod.PUT, headers = {"Accept=application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateRepositoryMetaData(final HttpServletRequest request, HttpServletResponse response,
                                                   @RequestBody RepositoryMetadataChangeDTO reqDto) throws MetadataServiceException {

        IRI uri = getRequestURLasIRI(request);
        repositoryMetadataService.update(uri, FDPMetadata.class, reqDto);
        return ResponseEntity.noContent().build();
    }
}
