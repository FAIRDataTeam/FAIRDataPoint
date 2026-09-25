/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.rdf.metadata;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.model.Statement;
import org.fairdatateam.fairdatapoint.common.error.ForbiddenException;
import org.fairdatateam.fairdatapoint.common.error.ValidationException;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionChild;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionService;
import org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchemaService;
import org.fairdatateam.fairdatapoint.search.SearchFilterCache;
import org.fairdatateam.fairdatapoint.security.CurrentUserProvider;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.rio.RDFFormat;
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
import static org.fairdatateam.fairdatapoint.common.util.HttpUtil.*;
import static org.fairdatateam.fairdatapoint.rdf.RdfIOUtil.changeBaseUri;
import static org.fairdatateam.fairdatapoint.rdf.RdfIOUtil.read;
import static org.fairdatateam.fairdatapoint.rdf.RdfUtil.*;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;

/**
 * This is the main controller that handles RDF metadata resources
 */
@Tag(name = "Metadata")
@RestController
@RequestMapping("/")
// constructor autowiring with the help of lombok
@RequiredArgsConstructor
public class GenericController {

    private static final String MSG_ERROR_DRAFT_FORBIDDEN = "You are not allowed to view this record in state DRAFT";

    // lombok is configured to copy the qualifier into the generated constructor, see lombok.config
    @Qualifier("persistentUrl")
    private final String persistentUrl;

    private final CurrentUserProvider currentUserProvider;

    private final GenericMetadataRdfRepository metadataRepository;

    private final MetadataEnhancer metadataEnhancer;

    private final MetadataSchemaService metadataSchemaService;

    private final MetadataServiceFactory metadataServiceFactory;

    private final MetadataStateService metadataStateService;

    private final ResourceDefinitionService resourceDefinitionService;

    private final SearchFilterCache searchFilterCache;

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
        abortIfUserCannotAccessResource(entityUri);

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
        abortIfUserCannotAccessResource(entityUri);

        // 5. Filter children
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            final IRI relationUri = i(rdChild.getRelationUri());
            for (org.eclipse.rdf4j.model.Value childUri : getObjectsBy(entity, entityUri, relationUri)) {
                if (!userCanAccessResource(childUri)) {
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
        final Optional<User> oUser = currentUserProvider.getCurrentUser();
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
        final Model metadata = metadataService.update(reqDto, uri, rd, true);

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
    ) throws MetadataServiceException, MetadataRdfRepositoryException {
        // Initialize new RDF graph
        final Model resultRdf = new LinkedHashModel();

        // Note that urlPrefix and childPrefix actually represent resource types (or LDP container names).
        // The recordId is basically the resource id.
        // For example, the catalog (urlPrefix) with given uuid (recordId) contains dataset (childPrefix) resources.
        // todo: should rename for clarity, but that is a tough job because these terms are also used in db fields etc.
        final String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");

        // Get the metadata service for the specified child resource type
        final MetadataService childMetadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(childPrefix);

        // Get the IRI for the specified resource
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);

        // Check resource access
        abortIfUserCannotAccessResource(entityUri);

        // Get the resource definitions for the specified resource type and child resource type
        final ResourceDefinition resourceDefinition = resourceDefinitionService.getByUrlPrefix(urlPrefix);
        final ResourceDefinition childResourceDefinition = resourceDefinitionService.getByUrlPrefix(childPrefix);

        // A ResourceDefinitionChild defines the RDF-predicate and RDF-object (another ResourceDefinition) of the
        // membership relation defined in an LDP direct container.
        for (ResourceDefinitionChild resourceDefinitionChild : resourceDefinition.getChildren()) {
            // A resource may have multiple types of children, so we only select the resource type specified in the uri
            if (resourceDefinitionChild.getResourceDefinitionUuid().equals(childResourceDefinition.getUuid())) {
                // Get the RDF-predicate
                final IRI relationUri = i(resourceDefinitionChild.getRelationUri());

                // Get child resources
                final List<Value> children = getChildResources(urlPrefix, childPrefix, entityUri, relationUri);

                // Apply paging to limit the result size
                final List<Value> selectedChildren = children.stream().skip((long) page * size).limit(size).toList();

                // Add the RDF statements for each of the selected child resources to the result graph
                for (Value childUri : selectedChildren) {
                    // see AbstractMetadataService.retrieve
                    resultRdf.addAll(childMetadataService.retrieve(i(childUri)));
                }

                // Set HTTP Link headers and return response
                final HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(
                        "Link",
                        createPagingLinkHeader(entityUri.stringValue(), childPrefix, children.size(), page, size)
                );
                return ResponseEntity.ok().headers(responseHeaders).body(resultRdf);
            }
        }

        // Send empty response in case nothing was found
        return ResponseEntity.ok(resultRdf);
    }

    /**
     * Returns a list of child resource IRIs sorted by title
     */
    private List<Value> getChildResources(
            String urlPrefix, String childPrefix, IRI entityUri, IRI relationUri
    ) throws MetadataRdfRepositoryException, MetadataServiceException {
        // Get the metadata service for the specified parent resource type
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // Get the RDF graph for the parent resource
        final Model entity = metadataService.retrieve(entityUri);

        // Get the titles of the child resources contained in the current resource (entityUri) using SPARQL.
        // For example, the titles of the datasets that are contained in the specified catalog.
        // These child resources are identified by the RDF-predicate (relationUri) defined in the
        // ResourceDefinitionChild, i.e., dcat:dataset in our example.
        final Map<String, String> titles = metadataRepository.findChildTitles(entityUri, relationUri);

        // Get the RDF-object values (children) for the specified RDF-subject (entityUri) and
        // RDF-predicate (relationUri), filtered by access and sorted by title. For example, the full list of
        // URIs (childUri) of all the dataset resources that are part of our catalog.
        return entity.filter(entityUri, relationUri, null)
                .stream()
                .map(Statement::getObject)
                .filter(childUri -> getResourceNameForChild(childUri.toString()).equals(childPrefix))
                .filter(this::userCanAccessResource)
                .sorted((value1, value2) -> {
                    final String title1 = titles.get(value1.toString());
                    final String title2 = titles.get(value2.toString());
                    return title1.compareTo(title2);
                })
                .toList();
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

    private String createPagingLinkHeader(String entityUrl, String childPrefix, int childrenCount, int page, int size) {
        final List<String> links = new LinkedList<>();
        final int lastPage = (int) Math.ceil((float) childrenCount / size) - 1;

        links.add(createPagingLink(entityUrl, childPrefix, 0, size, "first"));
        links.add(createPagingLink(entityUrl, childPrefix, lastPage, size, "last"));

        if (page > 0 && page <= lastPage) {
            links.add(createPagingLink(entityUrl, childPrefix, page - 1, size, "prev"));
        }

        if (page < lastPage && page >= 0) {
            links.add(createPagingLink(entityUrl, childPrefix, page + 1, size, "next"));
        }

        return String.join(", ", links);
    }

    private String createPagingLink(String entityUrl, String childPrefix, int page, int size, String rel) {
        return format("<%s/page/%s?page=%d&size=%d>; rel=\"%s\"", entityUrl, childPrefix, page, size, rel);
    }

    /**
     * Checks if the specified resource is visible for the current user.
     * DRAFT resources are only visible for authenticated users, PUBLISHED resources are always visible.
     */
    private boolean userCanAccessResource(Value metadataUri) {
        final boolean userIsAuthenticated = currentUserProvider.getCurrentUser().isPresent();
        final MetadataState publicationState = metadataStateService.get(i(metadataUri)).getState();
        return userIsAuthenticated || publicationState.equals(MetadataState.PUBLISHED);
    }

    /**
     * Raises an exception if the request user is not allowed to see the specified resource.
     * This is handled by the ExceptionControllerAdvice class, which then returns HTTP status 403 FORBIDDEN.
     */
    private void abortIfUserCannotAccessResource(IRI resourceUri) {
        if (!userCanAccessResource(resourceUri)) {
            throw new ForbiddenException(MSG_ERROR_DRAFT_FORBIDDEN);
        }
    }
}
