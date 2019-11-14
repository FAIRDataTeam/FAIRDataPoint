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

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.catalog.CatalogMetadataMapper;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/fdp/catalog")
public class CatalogController extends MetadataController {

    @Autowired
    private CatalogMetadataMapper catalogMetadataMapper;

    @Autowired
    private MemberService memberService;

    @Value("${instance.url}")
    private String instanceUrl;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=application/json"})
    public ResponseEntity<CatalogMetadataDTO> getCatalogMetaData(@PathVariable final String id,
                                                                 HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException {
        IRI uri = getRequestURLasIRI(request);
        CatalogMetadata metadata = catalogMetadataService.retrieve(uri);
        List<DatasetMetadata> datasets = datasetMetadataService.retrieve(metadata.getDatasets());
        FDPMetadata repository = fdpMetadataService.retrieve(metadata.getParentURI());
        String catalogId = metadata.getIdentifier().getIdentifier().getLabel();
        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(catalogId, CatalogMetadata.class);
        CatalogMetadataDTO dto = catalogMetadataMapper.toDTO(metadata, datasets, repository, oMember);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=*/*"}, produces = {"text/turtle",
            "application/ld+json", "application/rdf+xml", "text/n3"})
    @ResponseStatus(HttpStatus.OK)
    public CatalogMetadata getCatalogMetaData(@PathVariable final String id,
                                              HttpServletRequest request, HttpServletResponse response) throws
            MetadataServiceException, ResourceNotFoundException {

        LOGGER.info("Request to get CATALOG metadata, request url : {}", request.getRequestURL());
        return catalogMetadataService.retrieve(getRequestURLasIRI(request));
    }

    @RequestMapping(method = RequestMethod.POST, headers = {"Accept=*/*"}, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogMetadata storeCatalogMetaData(final HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody CatalogMetadata metadata)
            throws MetadataServiceException {

        IRI uri = generateNewIRI(request);
        LOGGER.info("Request to store catalog metadata with IRI {}", uri.toString());

        metadata.setUri(uri);
        metadata.setParentURI(VALUEFACTORY.createIRI(instanceUrl + "/fdp"));

        // Ignore children links
        metadata.setDatasets(Collections.emptyList());

        catalogMetadataService.store(metadata);
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return catalogMetadataService.retrieve(uri);
    }
}
