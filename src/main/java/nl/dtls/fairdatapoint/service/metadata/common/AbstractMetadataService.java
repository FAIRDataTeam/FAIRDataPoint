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
import nl.dtls.fairdatapoint.service.metadata.validator.MetadataValidator;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    protected MetadataRepository metadataRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    protected MetadataEnhancer metadataEnhancer;

    @Autowired
    protected MetadataValidator metadataValidator;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Override
    public Model retrieve(IRI uri) throws MetadataServiceException, ResourceNotFoundException {
        try {
            // 1. Get metadata
            List<Statement> statements = metadataRepository.find(uri);
            if (statements.isEmpty()) {
                throw new ResourceNotFoundException(format("No metadata found for the uri '%s'", uri));
            }

            // 2. Convert to model
            Model metadata = new LinkedHashModel();
            metadata.addAll(statements);
            return metadata;
        } catch (MetadataRepositoryException ex) {
            throw new MetadataServiceException(ex.getMessage());
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
    public Model store(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        try {
            metadataValidator.validate(metadata, uri, resourceDefinition);
            metadataEnhancer.enhance(metadata, uri, resourceDefinition);
            metadataRepository.save(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            addPermissions(uri);
            return metadata;
        } catch (MetadataRepositoryException e) {
            throw new MetadataServiceException(e.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasPermission(#uri.stringValue(), 'nl.dtls.fairdatapoint.entity.metadata.Metadata', 'WRITE') " +
            "or hasRole('ADMIN')")
    public Model update(Model metadata, IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        try {
            metadataValidator.validate(metadata, uri, rd);
            Model oldMetadata = retrieve(uri);
            metadataEnhancer.enhance(metadata, uri, rd, oldMetadata);
            metadataRepository.remove(uri);
            metadataRepository.save(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, rd);
            return metadata;
        } catch (MetadataRepositoryException | MetadataServiceException e) {
            throw (new MetadataServiceException(e.getMessage()));
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        try {
            Model metadata = retrieve(uri);

            // Delete all children
            for (ResourceDefinitionChild child : rd.getChildren()) {
                String childRdUuid = child.getResourceDefinitionUuid();
                ResourceDefinition rdChild = resourceDefinitionCache.getByUuid(childRdUuid);
                if (rdChild != null) {
                    List<IRI> children = getChildren(metadata, i(child.getRelationUri()));
                    for (IRI childUri : children) {
                        delete(childUri, rdChild);
                    }
                }
            }

            // Remove reference at parent
            ResourceDefinition rdParent = resourceDefinitionCache.getParentByUuid(rd.getUuid());
            if (rdParent != null) {
                IRI parentUri = getParent(metadata);
                Model parentMetadata = retrieve(parentUri);
                for (ResourceDefinitionChild rdChild : rdParent.getChildren()) {
                    if (rdChild.getResourceDefinitionUuid().equals(rd.getUuid())) {
                        parentMetadata.remove(null, i(rdChild.getRelationUri()), uri);
                        update(parentMetadata, parentUri, rdParent);
                    }
                }
            }

            // Delete itself
            metadataRepository.remove(uri);

        } catch (MetadataRepositoryException | MetadataServiceException e) {
            throw (new MetadataServiceException(e.getMessage()));
        }
    }

    protected void updateParent(Model metadata, IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        IRI parent = MetadataGetter.getParent(metadata);
        if (parent != null) {
            ResourceDefinition rdParent = resourceDefinitionCache.getParentByUuid(rd.getUuid());
            if (rdParent != null) {
                try {
                    List<Statement> statements = new ArrayList<>();
                    for (ResourceDefinitionChild rdChild : rdParent.getChildren()) {
                        if (rdChild.getResourceDefinitionUuid().equals(rd.getUuid())) {
                            statements.add(s(parent, i(rdChild.getRelationUri()), uri));
                        }
                    }
                    metadataRepository.removeStatement(parent, FDP.METADATAMODIFIED, null, parent);
                    statements.add(s(parent, FDP.METADATAMODIFIED, l(OffsetDateTime.now())));
                    metadataRepository.save(statements, parent);
                } catch (MetadataRepositoryException e) {
                    throw new MetadataServiceException("Problem with updating parent timestamp");
                }
                Model parentMetadata = retrieve(parent);
                updateParent(parentMetadata, parent, rdParent);
            }
        }
    }

    private void addPermissions(IRI uri) {
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            return;
        }
        User user = oUser.get();
        memberService.createOwner(uri.stringValue(), Metadata.class, user.getUuid());
    }
}
