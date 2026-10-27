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

import lombok.RequiredArgsConstructor;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.fairdatateam.fairdatapoint.common.error.ForbiddenException;
import org.fairdatateam.fairdatapoint.common.error.ResourceNotFoundException;
import org.fairdatateam.fairdatapoint.common.error.ValidationException;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDP;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionCache;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionChild;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionService;
import org.fairdatateam.fairdatapoint.security.CurrentUserProvider;
import org.fairdatateam.fairdatapoint.security.membership.MemberService;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.fairdatateam.fairdatapoint.common.util.ThrowingFunction.suppress;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.*;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getChildren;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getParent;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataSetter.setThemeTaxonomies;

@Service
@RequiredArgsConstructor
public class GenericMetadataService implements MetadataService {

    private final CurrentUserProvider currentUserProvider;

    private final MemberService memberService;

    private final MetadataEnhancer metadataEnhancer;

    private final MetadataRdfRepository metadataRdfRepository;

    private final MetadataStateService metadataStateService;

    private final MetadataValidator metadataValidator;

    private final ResourceDefinitionCache resourceDefinitionCache;

    private final ResourceDefinitionService resourceDefinitionService;

    @Override
    public Model create(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        if (!resourceDefinition.isRoot()) {
            // 1. Check permissions
            final String parentId = Optional.ofNullable(getParent(metadata))
                    .orElseThrow(() -> new ValidationException("Metadata has no parent")).stringValue();
            if (!(getMemberService().checkPermission(parentId, Metadata.class, BasePermission.CREATE)
                    || getMemberService().checkRole(UserRole.ADMIN))) {
                throw new ForbiddenException("You are not allowed to create a new entry");
            }
        }

        // 2. Store
        try {
            metadataValidator.validate(metadata, uri, resourceDefinition);
            metadataEnhancer.enhance(metadata, uri, resourceDefinition);
            metadataRdfRepository.save(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            addPermissions(uri);
            addState(uri);
            return metadata;
        }
        catch (RdfRepositoryException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    @Override
    public Model read(IRI uri) throws MetadataServiceException, ResourceNotFoundException {
        try {
            // 1. Get metadata
            final List<Statement> statements = metadataRdfRepository.find(uri);
            if (statements.isEmpty()) {
                throw new ResourceNotFoundException(
                        format("No metadata found for the uri '%s'", uri)
                );
            }

            // 2. Convert to model
            final Model metadata = new LinkedHashModel();
            metadata.addAll(statements);
            return metadata;
        }
        catch (RdfRepositoryException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    @Override
    public List<Model> read(List<IRI> uris) {
        return uris
                .stream()
                .map(suppress(this::read))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("""
            hasPermission(#uri.stringValue(),
            T(org.fairdatateam.fairdatapoint.rdf.metadata.Metadata).getName(), 'WRITE')
            or hasRole('ADMIN')
            """)
    public Model update(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition, boolean validate
    ) throws MetadataServiceException {
        try {
            if (validate) {
                metadataValidator.validate(metadata, uri, resourceDefinition);
            }
            final Model oldMetadata = read(uri);
            metadataEnhancer.enhance(metadata, uri, resourceDefinition, oldMetadata);
            metadataRdfRepository.remove(uri);
            metadataRdfRepository.save(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            return metadata;
        }
        catch (RdfRepositoryException | MetadataServiceException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        try {
            final Model metadata = read(uri);

            // Delete all children
            for (ResourceDefinitionChild child : rd.getChildren()) {
                final String childRdUuid = child.getResourceDefinitionUuid();
                final ResourceDefinition rdChild = resourceDefinitionCache.getByUuid(childRdUuid);
                if (rdChild != null) {
                    final List<IRI> children = getChildren(metadata, i(child.getRelationUri()));
                    for (IRI childUri : children) {
                        delete(childUri, rdChild);
                    }
                }
            }

            // Remove reference at parent
            final Set<ResourceDefinition> rdParents =
                    resourceDefinitionCache.getParentsByUuid(rd.getUuid());
            // select parent based on URI prefix
            for (ResourceDefinition rdParent : rdParents) {
                final IRI parentUri = getParent(metadata);
                final Model parentMetadata = read(parentUri);
                for (ResourceDefinitionChild rdChild : rdParent.getChildren()) {
                    if (rdChild.getResourceDefinitionUuid().equals(rd.getUuid())) {
                        parentMetadata.remove(null, i(rdChild.getRelationUri()), uri);
                        update(parentMetadata, parentUri, rdParent, false);
                    }
                }
            }

            // Delete itself
            metadataRdfRepository.remove(uri);
        }
        catch (RdfRepositoryException | MetadataServiceException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    /**
     * Updates the specified resource metadata with themes obtained from any datasets it contains.
     */
    public void annotateWithDatasetThemes(Model resourceMetadata, IRI resourceUri) throws RdfRepositoryException {
        // Strictly speaking there is no need to check if the resourceUri actually represents a dcat:Catalog,
        // because the underlying SPARQL query simply returns empty if it does not (assuming the RDF properly observes
        // the domain of dcat:dataset). Nevertheless, we do check if it is a catalog, to limit the number of SPARQL
        // queries and the number of corresponding cache keys.
        if (resourceMetadata.contains(resourceUri, RDF.TYPE, DCAT.CATALOG)) {
            final List<IRI> themes = metadataRdfRepository.getDatasetThemesForCatalog(resourceUri);
            // todo: themes instead of themeTaxonomies (#1033)
            setThemeTaxonomies(resourceMetadata, resourceUri, themes);
        }
    }

    protected void updateParent(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        final IRI parent = MetadataGetter.getParent(metadata);
        if (parent != null) {
            final ResourceDefinition rdParent =
                    resourceDefinitionService.getByUrl(parent.toString());
            if (rdParent != null) {
                try {
                    final List<Statement> statements = new ArrayList<>();
                    for (ResourceDefinitionChild rdChild : rdParent.getChildren()) {
                        if (rdChild.getResourceDefinitionUuid()
                                .equals(resourceDefinition.getUuid())) {
                            statements.add(s(parent, i(rdChild.getRelationUri()), uri));
                        }
                    }
                    metadataRdfRepository.removeStatement(parent, FDP.METADATAMODIFIED, null, parent);
                    statements.add(s(parent, FDP.METADATAMODIFIED, l(OffsetDateTime.now())));
                    metadataRdfRepository.save(statements, parent);
                }
                catch (RdfRepositoryException exception) {
                    throw new MetadataServiceException("Problem with updating parent timestamp");
                }
                final Model parentMetadata = read(parent);
                updateParent(parentMetadata, parent, rdParent);
            }
        }
    }

    private void addPermissions(IRI uri) {
        final Optional<User> user = currentUserProvider.getCurrentUser();
        if (user.isEmpty()) {
            return;
        }
        memberService.createOwner(uri.stringValue(), Metadata.class, user.get().getUuid());
    }

    private void addState(IRI uri) {
        metadataStateService.initState(uri);
    }

    protected MemberService getMemberService() {
        return memberService;
    }

}
