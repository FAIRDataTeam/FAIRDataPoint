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
package nl.dtls.fairdatapoint.api.controller.metadata.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairmetadata4j.model.DatasetMetadata;
import nl.dtls.fairmetadata4j.model.DistributionMetadata;
import nl.dtls.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.controller.metadata.MetadataController;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataDTO;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.dataset.DatasetMetadataMapper;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Api(description = "Dataset Metadata")
@RequestMapping("/dataset")
public class DatasetController extends MetadataController {

    @Autowired
    private DatasetMetadataMapper datasetMetadataMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/spec", method = RequestMethod.GET, headers = {"Accept=application/json"})
    @ResponseBody
    public Object getFormMetadata() {
        Resource resource = new ClassPathResource("form-specs/dataset-spec.json");
        try {
            return objectMapper.readValue(resource.getInputStream(), Object.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=application/json"})
    public ResponseEntity<DatasetMetadataDTO> getDatasetMetaData(@PathVariable final String id,
                                                                 HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException {
        IRI uri = getRequestURLasIRI(request);
        DatasetMetadata metadata = datasetMetadataService.retrieve(uri);
        List<DistributionMetadata> distributions = distributionMetadataService.retrieve(metadata.getDistributions());
        CatalogMetadata catalog = catalogMetadataService.retrieve(metadata.getParentURI());
        FDPMetadata repository = repositoryMetadataService.retrieve(catalog.getParentURI());
        String datasetId = metadata.getIdentifier().getIdentifier().getLabel();
        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(datasetId, DatasetMetadata.class);
        DatasetMetadataDTO dto = datasetMetadataMapper.toDTO(metadata, distributions, repository, catalog, oMember);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "Dataset metadata")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=*/*"}, produces = {"text/turtle",
            "application/ld+json", "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public DatasetMetadata getDatasetMetaData(@PathVariable final String id,
                                              HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get DATASET metadata, request url : {}", request.getRequestURL());
        return datasetMetadataService.retrieve(getRequestURLasIRI(request));
    }

    @ApiOperation(value = "POST dataset metadata")
    @RequestMapping(method = RequestMethod.POST, headers = {"Accept=*/*"}, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetMetadata storeDatasetMetaData(final HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestBody DatasetMetadata metadata) throws MetadataServiceException {

        IRI uri = generateNewIRI(request);
        LOGGER.info("Request to store dataset metadata with IRI {}", uri.toString());

        metadata.setUri(uri);
        String parentId = metadata.getParentURI().getLocalName();
        if (!memberService.checkPermission(parentId, CatalogMetadata.class, BasePermission.CREATE)) {
            throw new ForbiddenException("You are not allow to add new entry");
        }

        // Ignore children links
        metadata.setDistributions(Collections.emptyList());

        datasetMetadataService.store(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return datasetMetadataService.retrieve(uri);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = {"Accept=application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateDatasetMetaData(@PathVariable final String id, final HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestBody DatasetMetadataChangeDTO reqDto)
            throws MetadataServiceException {

        IRI uri = getRequestURLasIRI(request);
        datasetMetadataService.update(uri, DatasetMetadata.class, reqDto);
        return ResponseEntity.noContent().build();
    }

}
