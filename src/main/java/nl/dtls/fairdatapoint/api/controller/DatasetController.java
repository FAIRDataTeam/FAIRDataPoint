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
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataDTO;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.dataset.DatasetMetadataMapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Api(description = "Dataset Metadata")
@RequestMapping("/fdp/dataset")
public class DatasetController extends MetadataController {

    @Autowired
    private DatasetMetadataMapper datasetMetadataMapper;

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=application/json"})
    public ResponseEntity<DatasetMetadataDTO> getDatasetMetaData(@PathVariable final String id,
                                                                 HttpServletRequest request) throws
            MetadataServiceException, ResourceNotFoundException {
        IRI uri = getRequestURLasIRI(request);
        DatasetMetadata metadata = datasetMetadataService.retrieve(uri);
        List<DistributionMetadata> distributions = distributionMetadataService.retrieve(metadata.getDistributions());
        CatalogMetadata catalog = catalogMetadataService.retrieve(metadata.getParentURI());
        FDPMetadata repository = fdpMetadataService.retrieve(catalog.getParentURI());
        String datasetId = metadata.getIdentifier().getIdentifier().getLabel();
        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(datasetId, DatasetMetadata.class);
        DatasetMetadataDTO dto = datasetMetadataMapper.toDTO(metadata, distributions, repository, catalog, oMember);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Get dataset metadata
     *
     * @param id
     * @param request
     * @param response
     * @return Metadata about the dataset in one of the acceptable formats (RDF Turtle, JSON-LD, RDF
     * XML and RDF N3)
     * @throws MetadataServiceException
     */
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

    @ApiIgnore
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Accept=text/html"},
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHtmlDatasetMetadata(HttpServletRequest request)
            throws MetadataServiceException, ResourceNotFoundException, MetadataException {

        ModelAndView mav = new ModelAndView("pages/dataset");
        IRI uri = getRequestURLasIRI(request);
        mav.addObject("contextPath", request.getContextPath());

        // Retrieve Dataset metadata
        DatasetMetadata metadata = datasetMetadataService.retrieve(uri);
        mav.addObject("metadata", metadata);
        mav.addObject("jsonLd", MetadataUtils.getString(metadata, RDFFormat.JSONLD,
                MetadataUtils.SCHEMA_DOT_ORG_MODEL));

        // Retrieve parents for breadcrumbs
        CatalogMetadata catalog = catalogMetadataService.retrieve(metadata.getParentURI());
        FDPMetadata repository = fdpMetadataService.retrieve(catalog.getParentURI());
        mav.addObject("repository", repository);
        mav.addObject("catalog", catalog);

        // Retrieve Distributions details
        mav.addObject("distributions", distributionMetadataService.retrieve(metadata.getDistributions()));

        return mav;
    }

    /**
     * To handle POST dataset metadata request.
     *
     * @param request  Http request
     * @param response Http response
     * @param metadata Dataset metadata
     * @return created message
     * @throws MetadataServiceException
     */
    @ApiOperation(value = "POST dataset metadata")
    @RequestMapping(method = RequestMethod.POST, headers = {"Accept=*/*"}, consumes = {"text/turtle"},
            produces = {"text/turtle"})
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetMetadata storeDatasetMetaData(final HttpServletRequest request,
                                                HttpServletResponse response,
                                                @RequestBody(required = true) DatasetMetadata metadata)
            throws MetadataServiceException {

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

}
