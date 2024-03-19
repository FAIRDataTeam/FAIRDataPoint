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
package nl.dtls.fairdatapoint.database.rdf.migration.development.metadata;

import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateChangeDTO;
import nl.dtls.fairdatapoint.database.common.migration.Migration;
import nl.dtls.fairdatapoint.database.db.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.database.db.repository.UserAccountRepository;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data.RdfMetadataFixtures;
import nl.dtls.fairdatapoint.database.rdf.repository.RepositoryMode;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.state.MetadataStateService;
import nl.dtls.fairdatapoint.service.security.AuthenticationService;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Service
public class RdfMetadataMigration implements Migration {

    @Autowired
    private RdfMetadataFixtures rdfMetadataFixtures;

    @Autowired
    @Qualifier("catalogMetadataService")
    private MetadataService catalogMetadataService;

    @Autowired
    @Qualifier("genericMetadataService")
    private MetadataService genericMetadataService;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    @Qualifier("genericMetadataRepository")
    private GenericMetadataRepository metadataRepository;

    public void clean() {
        try {
            metadataRepository.removeAll(RepositoryMode.MAIN);
            metadataRepository.removeAll(RepositoryMode.DRAFTS);
            // TODO: delete acl?
        }
        catch (MetadataRepositoryException exc) {
            throw new RuntimeException(exc);
        }
    }

    public void runMigration() {
        try {
            // Auth user
            final UserAccount user = userAccountRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).get();
            final Authentication auth = authenticationService.getAuthentication(user.getUuid().toString());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Load metadata fixtures
            importDefaultFixtures(persistentUrl);
        }
        catch (MetadataServiceException exception) {
            exception.printStackTrace();
        }
    }

    public void importDefaultFixtures(String fdpUrl) throws MetadataServiceException {
        final ResourceDefinition fdpRd =
                resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_FDP_UUID).get();
        final ResourceDefinition catalogRd =
                resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_CATALOG_UUID).get();
        final ResourceDefinition datasetRd =
                resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_DATASET_UUID).get();
        final ResourceDefinition distributionRd =
                resourceDefinitionRepository.findByUuid(KnownUUIDs.RD_DISTRIBUTION_UUID).get();

        final Model fdpM = rdfMetadataFixtures.fdpMetadata(fdpUrl);
        final IRI fdpUri = getUri(fdpM);
        genericMetadataService.store(fdpM, i(fdpUrl), fdpRd);
        metadataStateService.modifyState(fdpUri, new MetaStateChangeDTO(MetadataState.PUBLISHED));

        final Model catalog1 = rdfMetadataFixtures.catalog1(fdpUrl, i(fdpUrl));
        final IRI catalog1Uri = getUri(catalog1);
        catalogMetadataService.store(catalog1, catalog1Uri, catalogRd);
        metadataStateService.modifyState(catalog1Uri, new MetaStateChangeDTO(MetadataState.PUBLISHED));

        final Model catalog2 = rdfMetadataFixtures.catalog2(fdpUrl, fdpUri);
        final IRI catalog2Uri = getUri(catalog2);
        catalogMetadataService.store(catalog2, catalog2Uri, catalogRd);

        final Model dataset1 = rdfMetadataFixtures.dataset1(fdpUrl, catalog1Uri);
        final IRI dataset1Uri = getUri(dataset1);
        genericMetadataService.store(dataset1, dataset1Uri, datasetRd);
        metadataStateService.modifyState(dataset1Uri, new MetaStateChangeDTO(MetadataState.PUBLISHED));

        final Model dataset2 = rdfMetadataFixtures.dataset2(fdpUrl, catalog1Uri);
        final IRI dataset2Uri = getUri(dataset2);
        genericMetadataService.store(dataset2, dataset2Uri, datasetRd);

        final Model distribution1 = rdfMetadataFixtures.distribution1(fdpUrl, dataset1Uri);
        final IRI distribution1Uri = getUri(distribution1);
        genericMetadataService.store(distribution1, distribution1Uri, distributionRd);
        metadataStateService.modifyState(distribution1Uri, new MetaStateChangeDTO(MetadataState.PUBLISHED));

        final Model distribution2 = rdfMetadataFixtures.distribution2(fdpUrl, dataset1Uri);
        final IRI distribution2Uri = getUri(distribution2);
        genericMetadataService.store(distribution2, distribution2Uri, distributionRd);
    }
}
