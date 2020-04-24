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

import nl.dtls.fairdatapoint.database.common.migration.Migration;
import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data.MetadataFixtures;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
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
public class MetadataMigration implements Migration {

    @Autowired
    protected MetadataFixtures metadataFixtures;

    @Autowired
    @Qualifier("catalogMetadataService")
    protected MetadataService catalogMetadataService;

    @Autowired
    @Qualifier("genericMetadataService")
    protected MetadataService genericMetadataService;

    @Autowired
    protected UserFixtures userFixtures;

    @Autowired
    protected ResourceDefinitionFixtures resourceDefinitionFixtures;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    public void runMigration() {
        try {
            // 1. Auth user
            String albertUuid = userFixtures.albert().getUuid();
            Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 2. Load metadata fixtures
            importDefaultFixtures(persistentUrl);
        } catch (MetadataServiceException e) {
            e.printStackTrace();
        }
    }

    public void importDefaultFixtures(String repositoryUrl) throws MetadataServiceException {
        ResourceDefinition repositoryRd = resourceDefinitionFixtures.repositoryDefinition();
        ResourceDefinition catalogRd = resourceDefinitionFixtures.catalogDefinition();
        ResourceDefinition datasetRd = resourceDefinitionFixtures.datasetDefinition();
        ResourceDefinition distributionRd = resourceDefinitionFixtures.distributionDefinition();

        Model repositoryM = metadataFixtures.repositoryMetadata(repositoryUrl);
        IRI repositoryUri = getUri(repositoryM);
        genericMetadataService.store(repositoryM, i(repositoryUrl), repositoryRd);

        Model catalog1 = metadataFixtures.catalog1(repositoryUrl, i(repositoryUrl));
        IRI catalog1Uri = getUri(catalog1);
        catalogMetadataService.store(catalog1, catalog1Uri, catalogRd);

        Model catalog2 = metadataFixtures.catalog2(repositoryUrl, repositoryUri);
        IRI catalog2Uri = getUri(catalog2);
        catalogMetadataService.store(catalog2, catalog2Uri, catalogRd);

        Model dataset1 = metadataFixtures.dataset1(repositoryUrl, catalog1Uri);
        IRI dataset1Uri = getUri(dataset1);
        genericMetadataService.store(dataset1, dataset1Uri, datasetRd);

        Model dataset2 = metadataFixtures.dataset2(repositoryUrl, catalog1Uri);
        IRI dataset2Uri = getUri(dataset2);
        genericMetadataService.store(dataset2, dataset2Uri, datasetRd);

        Model distribution1 = metadataFixtures.distribution1(repositoryUrl, dataset1Uri);
        IRI distribution1Uri = getUri(distribution1);
        genericMetadataService.store(distribution1, distribution1Uri, distributionRd);

        Model distribution2 = metadataFixtures.distribution2(repositoryUrl, dataset1Uri);
        IRI distribution2Uri = getUri(distribution2);
        genericMetadataService.store(distribution2, distribution2Uri, distributionRd);
    }

}
