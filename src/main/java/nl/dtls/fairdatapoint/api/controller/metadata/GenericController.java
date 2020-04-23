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
package nl.dtls.fairdatapoint.api.controller.metadata;

import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.shape.ShapeService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static nl.dtls.fairdatapoint.util.HttpUtil.*;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.changeBaseUri;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.read;
import static nl.dtls.fairdatapoint.util.RdfUtil.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@RestController
@RequestMapping("/")
public class GenericController {

    @Value("${instance.url}")
    private String instanceUrl;

    @Autowired
    private MetadataServiceFactory metadataServiceFactory;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Autowired
    private ShapeService shapeService;

    @RequestMapping(
            value = "**/spec",
            method = RequestMethod.GET,
            produces = {"!application/json"})
    public Model getFormMetadata() {
        return shapeService.getShaclFromShapes();
    }

    @RequestMapping(
            value = "**/expanded",
            method = RequestMethod.GET,
            produces = {"!application/json"})
    public Model getMetaDataExpanded(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String uri = getRequestURL(request, instanceUrl);
        Model resultRdf = new LinkedHashModel();
        String urlPrefix = getResourceNameForDetail(uri);
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        ResourceDefinition rd = resourceDefinitionService.getByUriPrefix(urlPrefix);

        // 3. Get entity
        IRI entityUri = i(getRequestURL(request, instanceUrl));
        Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 3. Get children
        if (rd.getChild() != null) {
            for (org.eclipse.rdf4j.model.Value datasetUri : getObjectsBy(entity, entityUri, i(rd.getChild()))) {
                Model dataset = metadataService.retrieve(i(datasetUri.stringValue()));
                resultRdf.addAll(dataset);
            }
        }

        // 4. Get parent
        while (true) {
            IRI parentUri = i(getStringObjectBy(entity, entityUri, DCTERMS.IS_PART_OF));
            if (parentUri == null) {
                break;
            }
            Model parent = metadataService.retrieve(parentUri);
            resultRdf.addAll(parent);
            entity = parent;
            entityUri = parentUri;
        }

        // 5. Create response
        return resultRdf;
    }

    @RequestMapping(
            value = "**",
            method = RequestMethod.GET,
            produces = {"!application/json"})
    public Model getMetaData(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String uri = getRequestURL(request, instanceUrl);
        Model resultRdf = new LinkedHashModel();
        String urlPrefix = getResourceNameForDetail(uri);
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI entityUri = i(getRequestURL(request, instanceUrl));
        Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 3. Create response
        return resultRdf;
    }

    @RequestMapping(
            value = "**",
            method = RequestMethod.POST,
            produces = {"!application/json"})
    public ResponseEntity<Model> storeMetaData(HttpServletRequest request,
                                               @RequestBody String reqBody,
                                               @RequestHeader(value = "Content-Type", required = false) String contentType)
            throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUriPrefix(urlPrefix);

        // 2. Generate URI
        IRI uri = generateNewIRI(request, instanceUrl);

        // 3. Parse reqDto
        RDFFormat rdfContentType = getRdfContentType(contentType);
        Model oldDto = read(reqBody, uri.stringValue(), rdfContentType);
        Model reqDto = changeBaseUri(oldDto, uri.stringValue(), rd.getShaclTargetClasses());
        String child = rd.getChild();
        if (child != null) {
            reqDto.remove(null, i(child), null);
        }

        // 4. Store metadata
        Model metadata = metadataService.store(reqDto, uri, rd);

        // 5. Create response
        return ResponseEntity
                .created(URI.create(uri.stringValue()))
                .body(metadata);
    }

    @RequestMapping(
            value = "**",
            method = RequestMethod.PUT,
            produces = {"!application/json"})
    public ResponseEntity<Model> updateMetaData(HttpServletRequest request,
                                                @RequestBody String reqBody,
                                                @RequestHeader(value = "Content-Type", required = false) String contentType)
            throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUriPrefix(urlPrefix);

        // 2. Extract URI
        IRI uri = i(getRequestURL(request, instanceUrl));

        // 3. Parse reqDto
        RDFFormat rdfContentType = getRdfContentType(contentType);
        Model reqDto = read(reqBody, uri.stringValue(), rdfContentType);
        String child = rd.getChild();
        if (child != null) {
            org.eclipse.rdf4j.model.Value childEntity = getObjectBy(reqDto, null, i(child));
            if (childEntity != null) {
                reqDto.remove(i(childEntity.stringValue()), null, null);
            }
        }

        // 4. Store metadata
        Model metadata = metadataService.update(reqDto, uri, rd);

        // 5. Create response
        return ResponseEntity
                .ok(metadata);
    }

    @RequestMapping(value = "**", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMetadata(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUriPrefix(urlPrefix);

        // 2. Skip if Repository (we don't support delete for repository)
        if (rd.getName().equals("Repository")) {
            return ResponseEntity.notFound().build();
        }

        // 3. Extract URI
        IRI uri = i(getRequestURL(request, instanceUrl));

        // 4. Store metadata
        metadataService.delete(uri, rd);

        // 5. Create response
        return ResponseEntity.noContent().build();
    }

    private String getResourceNameForList(String url) {
        instanceUrl = removeLastSlash(instanceUrl);
        url = url.replace(instanceUrl, "");

        String[] parts = url.split("/");
        if (parts.length != 2) {
            throw new ValidationException("Unsupported URL");
        }
        return parts[1];
    }

    private String getResourceNameForDetail(String url) {
        instanceUrl = removeLastSlash(instanceUrl);
        url = url.replace(instanceUrl, "");

        // If URL is a repository -> return empty string
        if (url.equals("")) {
            return "";
        }

        String[] parts = url.split("/");
        if (parts.length != 3) {
            throw new ValidationException("Unsupported URL");
        }
        return parts[1];
    }

}
