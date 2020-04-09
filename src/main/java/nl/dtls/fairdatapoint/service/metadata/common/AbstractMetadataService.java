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

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.enhance.MetadataEnhancer;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.validator.MetadataValidator;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import nl.dtls.fairmetadata4j.accessor.MetadataGetter;
import nl.dtls.fairmetadata4j.vocabulary.FDP;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.util.ThrowingFunction.suppress;
import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.getChildren;
import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.getParent;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.*;

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
    protected ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    protected ResourceDefinitionService resourceDefinitionService;

    @Autowired
    protected MetadataEnhancer metadataEnhancer;

    @Autowired
    protected MetadataValidator metadataValidator;

    @Override
    public Model retrieve(IRI uri) throws MetadataServiceException, ResourceNotFoundException {
        Model metadata = new LinkedHashModel();
        try {
            Preconditions.checkNotNull(uri, "Resource uri not be null.");
            List<Statement> statements = metadataRepository.retrieveResource(uri);
            if (statements.isEmpty()) {
                String msg = ("No metadata found for the uri : " + uri);
                throw new ResourceNotFoundException(msg);
            }
            metadata.addAll(statements);
            return metadata;
        } catch (MetadataRepositoryException ex) {
            log.error("Error retrieving the metadata");
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
            metadataRepository.storeStatements(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            addPermissions(uri);
            return metadata;
        } catch (MetadataRepositoryException e) {
            log.error("Error storing distribution metadata");
            throw new MetadataServiceException(e.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasPermission(#uri.getLocalName(), 'nl.dtls.fairdatapoint.entity.metadata.Metadata', 'WRITE') " +
            "or hasRole('ADMIN')")
    public Model update(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        try {
            metadataValidator.validate(metadata, uri, resourceDefinition);
            Model oldMetadata = retrieve(uri);
            metadataEnhancer.enhance(metadata, uri, resourceDefinition, oldMetadata);
            metadataRepository.removeResource(uri);
            metadataRepository.storeStatements(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            return metadata;
        } catch (MetadataRepositoryException | MetadataServiceException e) {
            log.error("Error updating metadata");
            throw (new MetadataServiceException(e.getMessage()));
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        try {
            Model metadata = retrieve(uri);
            String childPredicate = resourceDefinition.getChild();

            // Delete all children
            if (childPredicate != null) {
                String childRdUuid = resourceDefinition.getChildResourceDefinitionUuid();
                ResourceDefinition childRd = resourceDefinitionService.getByUuid(childRdUuid);
                List<IRI> children = getChildren(metadata, i(childPredicate));
                for (IRI child : children) {
                    delete(child, childRd);
                }
            }

            // Remove reference at parent
            String parentRdUuid = resourceDefinition.getParentResourceDefinitionUuid();
            if (parentRdUuid != null) {
                ResourceDefinition parentRd = resourceDefinitionService.getByUuid(parentRdUuid);
                IRI parentUri = getParent(metadata);
                Model parent = retrieve(parentUri);
                parent.remove(null, i(parentRd.getChild()), uri);
                update(parent, parentUri, parentRd);
            }

            // Delete itself
            metadataRepository.removeResource(uri);

        } catch (MetadataRepositoryException | MetadataServiceException e) {
            log.error("Error updating metadata");
            throw (new MetadataServiceException(e.getMessage()));
        }
    }

    protected void updateParent(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        IRI parent = MetadataGetter.getParent(metadata);
        if (parent != null) {
            String parentRdUuid = resourceDefinition.getParentResourceDefinitionUuid();
            ResourceDefinition parentResourceDefinition = resourceDefinitionService.getByUuid(parentRdUuid);
            try {
                List<Statement> statements = new ArrayList<>();
                if (parentResourceDefinition.getChild() != null) {
                    statements.add(s(parent, i(parentResourceDefinition.getChild()), uri));
                }
                metadataRepository.removeStatement(parent, FDP.METADATAMODIFIED, null);
                statements.add(s(parent, FDP.METADATAMODIFIED, l(LocalDateTime.now())));
                metadataRepository.storeStatements(statements, parent);
            } catch (MetadataRepositoryException e) {
                throw new MetadataServiceException("Problem with updating parent timestamp");
            }
            Model parentMetadata = retrieve(parent);
            updateParent(parentMetadata, parent, parentResourceDefinition);
        }
    }

    private void addPermissions(IRI uri) {
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            return;
        }
        User user = oUser.get();
        String entityId = uri.getLocalName();
        memberService.createOwner(entityId, Metadata.class, user.getUuid());
    }
}
