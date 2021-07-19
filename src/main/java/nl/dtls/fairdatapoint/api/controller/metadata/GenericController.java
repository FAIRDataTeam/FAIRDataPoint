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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.enhance.MetadataEnhancer;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import nl.dtls.fairdatapoint.service.metadata.state.MetadataStateService;
import nl.dtls.fairdatapoint.service.openapi.OpenApiService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.shape.ShapeService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedList;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static nl.dtls.fairdatapoint.util.HttpUtil.*;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.changeBaseUri;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.read;
import static nl.dtls.fairdatapoint.util.RdfUtil.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Tag(name = "Metadata")
@RestController
@RequestMapping("/")
public class GenericController {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private MetadataServiceFactory metadataServiceFactory;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Autowired
    private ShapeService shapeService;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    private MetadataEnhancer metadataEnhancer;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Operation(hidden = true)
    @GetMapping(path = "**/spec", produces = {"!application/json"})
    public Model getFormMetadata() {
        return shapeService.getShaclFromShapes();
    }

    @Operation(hidden = true, deprecated = true)
    @GetMapping(path = "**/expanded", produces = {"!application/json"})
    public Model getMetaDataExpanded(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String uri = getRequestURL(request, persistentUrl);
        Model resultRdf = new LinkedHashModel();
        String urlPrefix = getResourceNameForDetail(uri);
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI entityUri = i(getRequestURL(request, persistentUrl));
        Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 3. Check if it is draft
        Metadata state = metadataStateService.get(entityUri);
        Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (state.getState().equals(MetadataState.DRAFT) && oCurrentUser.isEmpty()) {
            throw new ForbiddenException("You are not allow to view this record in state DRAFT");
        }

        // 4. Enhance
        metadataEnhancer.enhanceWithResourceDefinition(entityUri, rd, resultRdf);

        // 5. Get parent
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

    @Operation(hidden = true)
    @GetMapping(path = "**", produces = {"!application/json"})
    public Model getMetaData(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String uri = getRequestURL(request, persistentUrl);
        Model resultRdf = new LinkedHashModel();
        String urlPrefix = getResourceNameForDetail(uri);
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 3. Get entity
        IRI entityUri = i(getRequestURL(request, persistentUrl));
        Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 4. Check if it is DRAFT
        Metadata state = metadataStateService.get(entityUri);
        Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (state.getState().equals(MetadataState.DRAFT) && oCurrentUser.isEmpty()) {
            throw new ForbiddenException("You are not allow to view this record in state DRAFT");
        }

        // 5. Filter children
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            IRI relationUri = i(rdChild.getRelationUri());
            for (org.eclipse.rdf4j.model.Value childUri : getObjectsBy(entity, entityUri, relationUri)) {
                Metadata childState = metadataStateService.get(i(childUri.stringValue()));
                if (!(childState.getState().equals(MetadataState.PUBLISHED) || oCurrentUser.isPresent())) {
                    resultRdf.remove(entityUri, relationUri, childUri);
                }
            }
        }

        // 6. Add links
        metadataEnhancer.enhanceWithLinks(entityUri, entity, rd, persistentUrl, resultRdf);
        metadataEnhancer.enhanceWithResourceDefinition(entityUri, rd, resultRdf);

        // 7. Create response
        return resultRdf;
    }

    @Operation(hidden = true)
    @PostMapping(path = "**", produces = {"!application/json"})
    public ResponseEntity<Model> storeMetaData(HttpServletRequest request,
                                               @RequestBody String reqBody,
                                               @RequestHeader(value = "Content-Type", required = false) String contentType)
            throws MetadataServiceException {
        // 1. Check if user is authenticated
        //     - it can't be in SecurityConfig because the authentication is done based on content-type
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            throw new ForbiddenException("You have to be login at first");
        }

        // 2. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request, persistentUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 3. Generate URI
        IRI uri = generateNewIRI(request, persistentUrl);

        // 4. Parse reqDto
        RDFFormat rdfContentType = getRdfContentType(contentType);
        Model oldDto = read(reqBody, uri.stringValue(), rdfContentType);
        Model reqDto = changeBaseUri(oldDto, uri.stringValue(), resourceDefinitionService.getTargetClassUris(rd));
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            reqDto.remove(null, i(rdChild.getRelationUri()), null);
        }

        // 5. Store metadata
        Model metadata = metadataService.store(reqDto, uri, rd);

        // 6. Create response
        return ResponseEntity
                .created(URI.create(uri.stringValue()))
                .body(metadata);
    }

    @Operation(hidden = true)
    @PutMapping(path = "**", produces = {"!application/json"})
    public ResponseEntity<Model> updateMetaData(HttpServletRequest request,
                                                @RequestBody String reqBody,
                                                @RequestHeader(value = "Content-Type", required = false) String contentType)
            throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request, persistentUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 2. Extract URI
        IRI uri = i(getRequestURL(request, persistentUrl));

        // 3. Parse reqDto
        RDFFormat rdfContentType = getRdfContentType(contentType);
        Model reqDto = read(reqBody, uri.stringValue(), rdfContentType);
        for (ResourceDefinitionChild child : rd.getChildren()) {
            org.eclipse.rdf4j.model.Value childEntity = getObjectBy(reqDto, null, i(child.getRelationUri()));
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

    @Operation(hidden = true)
    @DeleteMapping(path = "**")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMetadata(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request, persistentUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 2. Skip if Repository (we don't support delete for repository)
        if (rd.getName().equals("Repository")) {
            return ResponseEntity.notFound().build();
        }

        // 3. Extract URI
        IRI uri = i(getRequestURL(request, persistentUrl));

        // 4. Store metadata
        metadataService.delete(uri, rd);

        // 5. Create response
        return ResponseEntity.noContent().build();
    }

    @Operation(hidden = true)
    @GetMapping(path = "**/page/{childPrefix}", produces = {"!application/json"})
    public ResponseEntity<Model> getMetaDataChildren(
            @PathVariable final String childPrefix,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            HttpServletRequest request
    ) throws MetadataServiceException, MetadataRepositoryException {
        // 1. Init
        String requestUrl = getRequestURL(request, persistentUrl);
        Model resultRdf = new LinkedHashModel();
        String urlPrefix = getResourceNameForChild(requestUrl);
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI entityUri = getEntityIriForPagination(requestUrl);
        Model entity = metadataService.retrieve(entityUri);

        // 3. Check if it is draft
        Metadata state = metadataStateService.get(entityUri);
        Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (state.getState().equals(MetadataState.DRAFT) && oCurrentUser.isEmpty()) {
            throw new ForbiddenException("You are not allow to view this record in state DRAFT");
        }

        // 4. Get Children
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);
        ResourceDefinition currentChildRd = resourceDefinitionService.getByUrlPrefix(childPrefix);
        MetadataService childMetadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(childPrefix);

        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            if (rdChild.getResourceDefinitionUuid().equals(currentChildRd.getUuid())) {
                IRI relationUri = i(rdChild.getRelationUri());

                // 4.1 Get all titles for sort
                var titles = metadataRepository.findChildTitles(entityUri, relationUri);

                // 4.2 Get all children sorted
                var children = getObjectsBy(entity, entityUri, relationUri)
                        .stream()
                        .filter((childUri) -> getResourceNameForChild(childUri.toString()).equals(childPrefix))
                        .filter((childUri) -> {
                            if (oCurrentUser.isPresent()) return true;
                            Metadata childState = metadataStateService.get(i(childUri.stringValue()));
                            return childState.getState().equals(MetadataState.PUBLISHED);
                        })
                        .sorted((v1, v2) -> {
                            var title1 = titles.get(v1.toString());
                            var title2 = titles.get(v2.toString());
                            return title1.compareTo(title2);
                        })
                        .collect(toList());

                // 4.3 Retrieve children metadata only for requested page
                var childrenCount = children.size();
                children.stream().skip(page * size).limit(size)
                        .map((childUri) -> retrieveChildModel(childMetadataService, childUri))
                        .flatMap(Optional::stream)
                        .forEach(resultRdf::addAll);

                // 4.4 Set Link headers and send response
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Link", createLinkHeader(requestUrl, childrenCount, page, size));
                return ResponseEntity.ok().headers(responseHeaders).body(resultRdf);
            }
        }

        // Send empty response in case nothing was found
        return ResponseEntity.ok(resultRdf);
    }

    private String getResourceNameForList(String url) {
        url = url.replace(persistentUrl, "");

        String[] parts = url.split("/");
        if (parts.length != 2) {
            throw new ValidationException("Unsupported URL");
        }
        return parts[1];
    }

    private String getResourceNameForDetail(String url) {
        url = url.replace(persistentUrl, "");

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

    private String getResourceNameForChild(String url) {
        url = url.replace(persistentUrl, "");
        String[] parts = url.split("/");

        if (parts.length < 2) {
            throw new ValidationException("Unsupported URL");
        }

        // If URL is a repository -> return empty string
        if (parts[1].equals("page")) {
            return "";
        }

        return parts[1];
    }

    private IRI getEntityIriForPagination(String url) {
        String[] parts = url.split("/");

        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length - 2; i++) {
            sb.append("/");
            sb.append(parts[i]);
        }
        return i(sb.toString());
    }

    private String createLinkHeader(String requestUrl, int childrenCount, int page, int size) {
        var links = new LinkedList<String>();
        var lastPage = (int) Math.ceil((float) childrenCount / size) - 1;

        links.add(createLink(requestUrl, 0, size, "first"));
        links.add(createLink(requestUrl, lastPage, size, "last"));

        if (page > 0 && page <= lastPage) {
            links.add(createLink(requestUrl, page - 1, size, "prev"));
        }

        if (page < lastPage && page >= 0) {
            links.add(createLink(requestUrl, page + 1, size, "next"));
        }

        return String.join(", ", links);
    }

    private Optional<Model> retrieveChildModel(MetadataService childMetadataService, Value childUri) {
        try {
            Model childModel = childMetadataService.retrieve(i(childUri.stringValue()));
            return Optional.of(childModel);
        } catch (MetadataServiceException e) {
            return Optional.empty();
        }
    }

    private String createLink(String requestUrl, int page, int size, String rel) {
        return "<" + requestUrl + "/?page=" + page + "&size=" + size + ">; rel=\"" + rel + "\"";
    }
}
