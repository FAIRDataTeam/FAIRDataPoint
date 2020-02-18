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

import nl.dtls.fairmetadata4j.model.*;
import nl.dtls.fairdatapoint.api.dto.metadata.*;
import nl.dtls.fairdatapoint.database.common.migration.Migration;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data.MetadataFixtures;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MetadataMigration implements Migration {

    @Autowired
    protected MetadataFixtures metadataFixtures;

    @Autowired
    protected MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    protected MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    protected MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    protected MetadataService<DistributionMetadata, DistributionMetadataChangeDTO> distributionMetadataService;

    @Autowired
    private MetadataService<DataRecordMetadata, DataRecordMetadataChangeDTO> dataRecordMetadataService;

    @Autowired
    protected UserFixtures userFixtures;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Value("${instance.url}")
    private String instanceUrl;

    public void runMigration() {
        try {
            // 1. Auth user
            String albertUuid = userFixtures.albert().getUuid();
            Authentication auth = mongoAuthenticationService.getAuthentication(albertUuid);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 2. Load metadata fixtures
            importDefaultFixtures(instanceUrl);
        } catch (MetadataServiceException e) {
            e.printStackTrace();
        }
    }

    public void importDefaultFixtures(String repositoryUrl) throws MetadataServiceException {
        FDPMetadata repository = metadataFixtures.repositoryMetadata(repositoryUrl);
        repositoryMetadataService.store(repository);

        CatalogMetadata catalog1 = metadataFixtures.catalog1(repositoryUrl, repository);
        catalogMetadataService.store(catalog1);

        CatalogMetadata catalog2 = metadataFixtures.catalog2(repositoryUrl, repository);
        catalogMetadataService.store(catalog2);

        DatasetMetadata dataset1 = metadataFixtures.dataset1(repositoryUrl, catalog1);
        datasetMetadataService.store(dataset1);

        DatasetMetadata dataset2 = metadataFixtures.dataset2(repositoryUrl, catalog1);
        datasetMetadataService.store(dataset2);

        DistributionMetadata distribution1 = metadataFixtures.distribution1(repositoryUrl, dataset1);
        distributionMetadataService.store(distribution1);

        DistributionMetadata distribution2 = metadataFixtures.distribution2(repositoryUrl, dataset1);
        distributionMetadataService.store(distribution2);

        DataRecordMetadata datarecord1 = metadataFixtures.datarecord1(repositoryUrl, dataset1);
        dataRecordMetadataService.store(datarecord1);
    }

}
