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
package nl.dtls.fairdatapoint.service.metadata.common;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.metadata.MetadataGetter;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.enhance.MetadataEnhancer;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.state.MetadataStateService;
import nl.dtls.fairdatapoint.service.metadata.validator.MetadataValidator;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getChildren;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getParent;
import static nl.dtls.fairdatapoint.util.ThrowingFunction.suppress;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

@Slf4j
public abstract class AbstractMetadataService implements MetadataService {

    @Autowired
    @Qualifier("genericMetadataRepository")
    private MetadataRepository metadataRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MetadataEnhancer metadataEnhancer;

    @Autowired
    private MetadataValidator metadataValidator;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Override
    public Model retrieve(IRI uri) throws MetadataServiceException, ResourceNotFoundException {
        try {
            // 1. Get metadata
            final List<Statement> statements = metadataRepository.find(uri);
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
        catch (MetadataRepositoryException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    @Override
    public List<Model> retrieve(List<IRI> uris) {
        return uris
                .stream()
                .map(suppress(this::retrieve))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Model store(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        try {
            metadataValidator.validate(metadata, uri, resourceDefinition);
            metadataEnhancer.enhance(metadata, uri, resourceDefinition);
            metadataRepository.save(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            addPermissions(uri);
            addState(uri);
            return metadata;
        }
        catch (MetadataRepositoryException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    @Override
    @PreAuthorize("""
            hasPermission(#uri.stringValue(),
            'nl.dtls.fairdatapoint.entity.metadata.Metadata', 'WRITE')
            or hasRole('ADMIN')
            """)
    public Model update(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        try {
            metadataValidator.validate(metadata, uri, resourceDefinition);
            final Model oldMetadata = retrieve(uri);
            metadataEnhancer.enhance(metadata, uri, resourceDefinition, oldMetadata);
            metadataRepository.remove(uri);
            metadataRepository.save(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            return metadata;
        }
        catch (MetadataRepositoryException | MetadataServiceException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        try {
            final Model metadata = retrieve(uri);

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
                final Model parentMetadata = retrieve(parentUri);
                for (ResourceDefinitionChild rdChild : rdParent.getChildren()) {
                    if (rdChild.getResourceDefinitionUuid().equals(rd.getUuid())) {
                        parentMetadata.remove(null, i(rdChild.getRelationUri()), uri);
                        update(parentMetadata, parentUri, rdParent);
                    }
                }
            }

            // Delete itself
            metadataRepository.remove(uri);
        }
        catch (MetadataRepositoryException | MetadataServiceException exception) {
            throw new MetadataServiceException(exception.getMessage());
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
                    metadataRepository.removeStatement(parent, FDP.METADATAMODIFIED, null, parent);
                    statements.add(s(parent, FDP.METADATAMODIFIED, l(OffsetDateTime.now())));
                    metadataRepository.save(statements, parent);
                }
                catch (MetadataRepositoryException exception) {
                    throw new MetadataServiceException("Problem with updating parent timestamp");
                }
                final Model parentMetadata = retrieve(parent);
                updateParent(parentMetadata, parent, rdParent);
            }
        }
    }

    private void addPermissions(IRI uri) {
        final Optional<User> user = currentUserService.getCurrentUser();
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
