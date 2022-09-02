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
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaService;
import nl.dtls.fairdatapoint.service.search.SearchFilterCache;
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

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.HttpUtil.*;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.changeBaseUri;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.read;
import static nl.dtls.fairdatapoint.util.RdfUtil.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Tag(name = "Metadata")
@RestController
@RequestMapping("/")
public class GenericController {

    private static final String MSG_ERROR_DRAFT_FORBIDDEN =
            "You are not allow to view this record in state DRAFT";

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private MetadataServiceFactory metadataServiceFactory;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Autowired
    private MetadataSchemaService metadataSchemaService;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    private MetadataEnhancer metadataEnhancer;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Autowired
    private SearchFilterCache searchFilterCache;

    @Operation(hidden = true)
    @GetMapping(path = {"/spec", "{oUrlPrefix:[^.]+}/spec"}, produces = "!application/json")
    public Model getFormMetadata(
            @PathVariable final Optional<String> oUrlPrefix
    ) {
        final String urlPrefix = oUrlPrefix.orElse("");
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);
        return metadataSchemaService.getShaclFromSchemas(rd.getMetadataSchemaUuids());
    }

    @Operation(hidden = true, deprecated = true)
    @GetMapping(
            path = {"/expanded", "{oUrlPrefix:[^.]+}/{oRecordId:[^.]+}/expanded"},
            produces = "!application/json"
    )
    public Model getMetaDataExpanded(
            @PathVariable final Optional<String> oUrlPrefix,
            @PathVariable final Optional<String> oRecordId
    ) throws MetadataServiceException {
        // 1. Init
        final Model resultRdf = new LinkedHashModel();
        final String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 3. Check if it is DRAFT
        final Metadata state = metadataStateService.get(entityUri);
        final Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (state.getState().equals(MetadataState.DRAFT) && oCurrentUser.isEmpty()) {
            throw new ForbiddenException(MSG_ERROR_DRAFT_FORBIDDEN);
        }

        // 4. Enhance
        metadataEnhancer.enhanceWithResourceDefinition(entityUri, rd, resultRdf);

        // 5. Get parent
        while (true) {
            final IRI parentUri = i(getStringObjectBy(entity, entityUri, DCTERMS.IS_PART_OF));
            if (parentUri == null) {
                break;
            }
            final Model parent = metadataService.retrieve(parentUri);
            resultRdf.addAll(parent);
            entity = parent;
            entityUri = parentUri;
        }

        // 5. Create response
        return resultRdf;
    }

    @Operation(hidden = true)
    @GetMapping(
            path = {"", "{oUrlPrefix:[^.]+}/{oRecordId:[^.]+}"},
            produces = "!application/json"
    )
    public Model getMetaData(
            @PathVariable final Optional<String> oUrlPrefix,
            @PathVariable final Optional<String> oRecordId
    ) throws MetadataServiceException {
        // 1. Init
        final Model resultRdf = new LinkedHashModel();
        final String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 3. Get entity
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        final Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 4. Check if it is DRAFT
        final Metadata state = metadataStateService.get(entityUri);
        final Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (state.getState().equals(MetadataState.DRAFT) && oCurrentUser.isEmpty()) {
            throw new ForbiddenException(MSG_ERROR_DRAFT_FORBIDDEN);
        }

        // 5. Filter children
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            final IRI relationUri = i(rdChild.getRelationUri());
            for (org.eclipse.rdf4j.model.Value childUri : getObjectsBy(entity, entityUri, relationUri)) {
                final Metadata childState = metadataStateService.get(i(childUri.stringValue()));
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
    @PostMapping(path = "{urlPrefix:[^.]+}", produces = "!application/json")
    public ResponseEntity<Model> storeMetaData(
            @PathVariable final String urlPrefix,
            @RequestBody String reqBody,
            @RequestHeader(value = "Content-Type", required = false) String contentType
    ) throws MetadataServiceException {
        // 1. Check if user is authenticated
        //     - it can't be in SecurityConfig because the authentication is done based on content-type
        final Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            throw new ForbiddenException("You have to be login at first");
        }

        // 2. Init
        // String urlPrefix = getResourceNameForList(getRequestURL(request, persistentUrl));
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 3. Generate URI
        final IRI uri = generateNewMetadataIRI(persistentUrl, urlPrefix);

        // 4. Parse reqDto
        final RDFFormat rdfContentType = getRdfContentType(contentType);
        final Model oldDto = read(reqBody, uri.stringValue(), rdfContentType);
        final Model reqDto = changeBaseUri(oldDto, uri.stringValue(), resourceDefinitionService.getTargetClassUris(rd));
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            reqDto.remove(null, i(rdChild.getRelationUri()), null);
        }

        // 5. Store metadata
        final Model metadata = metadataService.store(reqDto, uri, rd);

        // 6. Invalidate search filters cache
        searchFilterCache.clearCache();

        // 7. Create response
        return ResponseEntity
                .created(URI.create(uri.stringValue()))
                .body(metadata);
    }

    @Operation(hidden = true)
    @PutMapping(
            path = {"", "{oUrlPrefix:[^.]+}/{oRecordId:[^.]+}"},
            produces = "!application/json"
    )
    public ResponseEntity<Model> updateMetaData(
            @PathVariable final Optional<String> oUrlPrefix,
            @PathVariable final Optional<String> oRecordId,
            @RequestBody String reqBody,
            @RequestHeader(value = "Content-Type", required = false) String contentType
    ) throws MetadataServiceException {
        // 1. Init
        final String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 2. Extract URI
        final IRI uri = getMetadataIRI(persistentUrl, urlPrefix, recordId);

        // 3. Parse reqDto
        final RDFFormat rdfContentType = getRdfContentType(contentType);
        final Model reqDto = read(reqBody, uri.stringValue(), rdfContentType);
        for (ResourceDefinitionChild child : rd.getChildren()) {
            final org.eclipse.rdf4j.model.Value childEntity = getObjectBy(reqDto, null, i(child.getRelationUri()));
            if (childEntity != null) {
                reqDto.remove(i(childEntity.stringValue()), null, null);
            }
        }

        // 4. Store metadata
        final Model metadata = metadataService.update(reqDto, uri, rd);

        // 5. Invalidate search filters cache
        searchFilterCache.clearCache();

        // 6. Create response
        return ResponseEntity
                .ok(metadata);
    }

    @Operation(hidden = true)
    @DeleteMapping(path = "{urlPrefix:[^.]+}/{recordId:[^.]+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMetadata(
            @PathVariable final String urlPrefix,
            @PathVariable final String recordId
    ) throws MetadataServiceException {
        // 1. Init
        // String urlPrefix = getResourceNameForDetail(getRequestURL(request, persistentUrl));
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 2. Skip if Repository (we don't support delete for repository)
        if (rd.isRoot()) {
            return ResponseEntity.notFound().build();
        }

        // 3. Extract URI
        final IRI uri = getMetadataIRI(persistentUrl, urlPrefix, recordId);

        // 4. Store metadata
        metadataService.delete(uri, rd);

        // 5. Invalidate search filters cache
        searchFilterCache.clearCache();

        // 6. Create response
        return ResponseEntity.noContent().build();
    }

    @Operation(hidden = true)
    @GetMapping(
            path = {"page/{childPrefix}", "{oUrlPrefix:[^.]+}/{oRecordId:[^.]+}/page/{childPrefix}"},
            produces = "!application/json"
    )
    public ResponseEntity<Model> getMetaDataChildren(
            @PathVariable final Optional<String> oUrlPrefix,
            @PathVariable final Optional<String> oRecordId,
            @PathVariable final String childPrefix,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) throws MetadataServiceException, MetadataRepositoryException {
        // 1. Init
        final Model resultRdf = new LinkedHashModel();
        final String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get entity
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        final Model entity = metadataService.retrieve(entityUri);

        // 3. Check if it is draft
        final Metadata state = metadataStateService.get(entityUri);
        final Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (state.getState().equals(MetadataState.DRAFT) && oCurrentUser.isEmpty()) {
            throw new ForbiddenException(MSG_ERROR_DRAFT_FORBIDDEN);
        }

        // 4. Get Children
        final ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);
        final ResourceDefinition currentChildRd = resourceDefinitionService.getByUrlPrefix(childPrefix);
        final MetadataService childMetadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(childPrefix);

        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            if (rdChild.getResourceDefinitionUuid().equals(currentChildRd.getUuid())) {
                final IRI relationUri = i(rdChild.getRelationUri());

                // 4.1 Get all titles for sort
                final Map<String, String> titles = metadataRepository.findChildTitles(entityUri, relationUri);

                // 4.2 Get all children sorted
                final List<Value> children = getObjectsBy(entity, entityUri, relationUri)
                        .stream()
                        .filter(childUri -> getResourceNameForChild(childUri.toString()).equals(childPrefix))
                        .filter(childUri -> {
                            if (oCurrentUser.isPresent()) {
                                return true;
                            }
                            final Metadata childState = metadataStateService.get(i(childUri.stringValue()));
                            return childState.getState().equals(MetadataState.PUBLISHED);
                        })
                        .sorted((value1, value2) -> {
                            final String title1 = titles.get(value1.toString());
                            final String title2 = titles.get(value2.toString());
                            return title1.compareTo(title2);
                        })
                        .toList();

                // 4.3 Retrieve children metadata only for requested page
                final int childrenCount = children.size();
                children.stream().skip((long) page * size).limit(size)
                        .map(childUri -> retrieveChildModel(childMetadataService, childUri))
                        .flatMap(Optional::stream)
                        .forEach(resultRdf::addAll);

                // 4.4 Set Link headers and send response
                final HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(
                        "Link",
                        createLinkHeader(entityUri.stringValue(), childPrefix, childrenCount, page, size)
                );
                return ResponseEntity.ok().headers(responseHeaders).body(resultRdf);
            }
        }

        // Send empty response in case nothing was found
        return ResponseEntity.ok(resultRdf);
    }

    private String getResourceNameForChild(String url) {
        final String[] parts = url
                .replace(persistentUrl, "")
                .split("/");

        if (parts.length < 2) {
            throw new ValidationException("Unsupported URL");
        }

        // If URL is a repository -> return empty string
        if (parts[1].equals("page")) {
            return "";
        }

        return parts[1];
    }

    private String createLinkHeader(String entityUrl, String childPrefix, int childrenCount, int page, int size) {
        final List<String> links = new LinkedList<String>();
        final int lastPage = (int) Math.ceil((float) childrenCount / size) - 1;

        links.add(createLink(entityUrl, childPrefix, 0, size, "first"));
        links.add(createLink(entityUrl, childPrefix, lastPage, size, "last"));

        if (page > 0 && page <= lastPage) {
            links.add(createLink(entityUrl, childPrefix, page - 1, size, "prev"));
        }

        if (page < lastPage && page >= 0) {
            links.add(createLink(entityUrl, childPrefix, page + 1, size, "next"));
        }

        return String.join(", ", links);
    }

    private Optional<Model> retrieveChildModel(MetadataService childMetadataService, Value childUri) {
        try {
            final Model childModel = childMetadataService.retrieve(i(childUri.stringValue()));
            return Optional.of(childModel);
        }
        catch (MetadataServiceException exception) {
            return Optional.empty();
        }
    }

    private String createLink(String entityUrl, String childPrefix, int page, int size, String rel) {
        return format("<%s/page/%s?page=%d&size=%d>; rel=\"%s\"", entityUrl, childPrefix, page, size, rel);
    }
}
