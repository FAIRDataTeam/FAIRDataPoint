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
import nl.dtls.fairdatapoint.database.rdf.repository.common.GenericMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadatametrics.FairMetadataMetricsService;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import nl.dtls.fairmetadata4j.accessor.MetadataSetter;
import nl.dtls.fairmetadata4j.model.Agent;
import nl.dtls.fairmetadata4j.vocabulary.FDP;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.util.ThrowingFunction.suppress;
import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.getParent;
import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.*;
import static nl.dtls.fairmetadata4j.util.RDFUtil.containsObject;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.*;

@Slf4j
public abstract class AbstractMetadataService implements MetadataService {

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Autowired
    @Qualifier("language")
    private IRI language;

    @Autowired
    @Qualifier("license")
    private IRI license;

    @Autowired
    @Qualifier("publisher")
    private Agent publisher;

    @Autowired
    @Qualifier("creator")
    private Agent creator;

    @Autowired
    private FairMetadataMetricsService fmMetricsService;

    @Autowired
    private PIDSystem pidSystem;

    @Autowired
    @Qualifier("genericMetadataRepository")
    protected GenericMetadataRepository metadataRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    protected ResourceDefinitionRepository resourceDefinitionRepository;

    @Override
    public Model retrieve(@Nonnull IRI uri) throws MetadataServiceException, ResourceNotFoundException {
        Model model = new LinkedHashModel();
        List<Statement> statements = retrieveStatements(uri);
        model.addAll(statements);
        return model;
    }

    @Override
    public List<Model> retrieve(List<IRI> uris) {
        return uris
                .stream()
                .map(suppress(this::retrieve))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void store(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        try {
            checkParent(metadata, uri, resourceDefinition);
            enhance(metadata, uri, resourceDefinition);
            metadataRepository.storeStatements(new ArrayList<>(metadata), uri);
            updateParent(metadata, uri, resourceDefinition);
            addPermissions(uri);
        } catch (MetadataRepositoryException e) {
            log.error("Error storing distribution metadata");
            throw new MetadataServiceException(e.getMessage());
        }
    }

    @PreAuthorize("hasPermission(#uri.getLocalName(), 'nl.dtls.fairdatapoint.entity.metadata.Metadata', 'WRITE') " +
            "or hasRole('ADMIN')")
    public void update(Model model, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        try {
            metadataRepository.removeResource(uri);
            store(model, uri, resourceDefinition);
        } catch (MetadataRepositoryException | MetadataServiceException e) {
            log.error("Error updating metadata");
            throw (new MetadataServiceException(e.getMessage()));
        }
    }

    protected void checkParent(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        IRI parent = getParent(metadata);

        // 1. Check if parent exists
        Preconditions.checkState(parent != null, "No parent uri");

        // 2. Check correctness of parent type
        try {
            ResourceDefinition parentDefinition = resourceDefinition.getParent();
            boolean result = false;
            if (parentDefinition != null) {
                result = metadataRepository.isStatementExist(parent, RDF.TYPE, i(parentDefinition.getRdfType()));
            }
            Preconditions.checkState(result, "Parent is not of correct type");
        } catch (MetadataRepositoryException e) {
            throw new MetadataServiceException(e.getMessage());
        }

    }

    @Override
    public void enhance(Model metadata, IRI uri, ResourceDefinition resourceDefinition) {
        addDefaultValues(metadata, uri, resourceDefinition);
        setSpecification(metadata, uri, resourceDefinition);
        setTimestamps(metadata, uri);
    }

    private List<Statement> retrieveStatements(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        try {
            Preconditions.checkNotNull(uri, "Resource uri not be null.");
            List<Statement> statements = metadataRepository.retrieveResource(uri);
            if (statements.isEmpty()) {
                String msg = ("No metadata found for the uri : " + uri);
                throw new ResourceNotFoundException(msg);
            }
            return statements;
        } catch (MetadataRepositoryException ex) {
            log.error("Error retrieving the metadata");
            throw new MetadataServiceException(ex.getMessage());
        }
    }

    protected void addDefaultValues(Model metadata, IRI uri, ResourceDefinition resourceDefinition) {
        // Add RDF Type
        setRdfTypes(metadata, uri, i(resourceDefinition.getRdfType()), i("http://www.w3.org/ns/dcat#Resource"));

        // Add PID
        IRI pidIri = pidSystem.getURI(uri);
        setPid(metadata, uri, pidIri, l(pidSystem.getId(pidIri)));

        // Add default publisher
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.PUBLISHER.stringValue()) && publisher != null) {
            setPublisher(metadata, uri, publisher);
        }

        // Add default creator
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.CREATOR.stringValue()) && creator != null) {
            setCreator(metadata, uri, creator);
        }

        // Add default language
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.LANGUAGE.stringValue()) && language != null) {
            setLanguage(metadata, uri, language);
        }

        // Add default license
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.LICENSE.stringValue()) && license != null) {
            setLicence(metadata, uri, license);
        }

        // Add access rights
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.ACCESS_RIGHTS.stringValue())) {
            IRI arIri = i(uri.stringValue() + "#accessRights");
            setAccessRights(metadata, uri, arIri, accessRightsDescription);
        }

        // Add FAIR metrics
        setMetrics(metadata, uri, fmMetricsService.getMetrics(uri));
    }

    private void setSpecification(Model metadata, IRI uri, ResourceDefinition rd) {
        MetadataSetter.setSpecification(metadata, uri, i(rd.getSpecs()));
    }

    private void setTimestamps(Model metadata, IRI uri) {
        setIssued(metadata, uri, l(LocalDateTime.now()));
        setModified(metadata, uri, l(LocalDateTime.now()));
    }

    protected void updateParent(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        Optional<IRI> oParent = MetadataUtil.getParent(metadata, uri);
        if (oParent.isPresent()) {
            IRI parent = oParent.get();
            ResourceDefinition parentResourceDefinition = resourceDefinition.getParent();
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
