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
package nl.dtls.fairdatapoint.database.rdf.migration;

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.membership.data.MembershipFixtures;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
@DependsOn("mongobee")
public class MetadataFixtures {

    @Autowired
    protected MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    protected MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    protected MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    protected MetadataService<DistributionMetadata, DistributionMetadataChangeDTO> distributionMetadataService;

    @Autowired
    protected MetadataFactory metadataFactory;

    @Autowired
    protected UserFixtures userFixtures;

    @Autowired
    protected MembershipFixtures membershipFixtures;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Value("${instance.url}")
    private String instanceUrl;

    @PostConstruct
    public void init() {
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
        FDPMetadata repository = repositoryMetadata(repositoryUrl);
        repositoryMetadataService.store(repository);

        CatalogMetadata catalog1 = catalog1(repositoryUrl, repository);
        catalogMetadataService.store(catalog1);

        CatalogMetadata catalog2 = catalog2(repositoryUrl, repository);
        catalogMetadataService.store(catalog2);

        DatasetMetadata dataset1 = dataset1(repositoryUrl, catalog1);
        datasetMetadataService.store(dataset1);

        DatasetMetadata dataset2 = dataset2(repositoryUrl, catalog1);
        datasetMetadataService.store(dataset2);

        DistributionMetadata distribution1 = distribution1(repositoryUrl, dataset1);
        distributionMetadataService.store(distribution1);

        DistributionMetadata distribution2 = distribution2(repositoryUrl, dataset1);
        distributionMetadataService.store(distribution2);
    }


    public FDPMetadata repositoryMetadata(String repositoryUrl) {
        return metadataFactory.createFDPMetadata(
                "My FAIR Data Point",
                "Duis pellentesque, nunc a fringilla varius, magna dui porta quam, nec ultricies augue turpis sed " +
                        "velit. Donec id consectetur ligula. Suspendisse pharetra egestas massa, vel varius leo " +
                        "viverra at. Donec scelerisque id ipsum id semper. Maecenas facilisis augue vel justo " +
                        "molestie aliquet. Maecenas sed mattis lacus, sed viverra risus. Donec iaculis quis lacus " +
                        "vitae scelerisque. Nullam fermentum lectus nisi, id vulputate nisi congue nec. Morbi " +
                        "fermentum justo at justo bibendum, at tempus ipsum tempor. Donec facilisis nibh sed lectus " +
                        "blandit venenatis. Cras ullamcorper, justo vitae feugiat commodo, orci metus suscipit purus," +
                        " quis sagittis turpis ante eget ex. Pellentesque malesuada a metus eu pulvinar. Morbi rutrum" +
                        " euismod eros at varius. Duis finibus dapibus ex, a hendrerit mauris efficitur at.",
                repositoryUrl
        );
    }

    public CatalogMetadata catalog1(String repositoryUrl, FDPMetadata repository) {
        return metadataFactory.createCatalogMetadata(
                "Bio Catalog",
                "Nam eget lorem rhoncus, porta odio at, pretium tortor. Morbi dapibus urna magna, at mollis neque " +
                        "sagittis et. Praesent fringilla, justo malesuada gravida cursus, nibh augue semper enim, et " +
                        "efficitur augue justo id odio. Donec id malesuada leo, vel molestie sem. Sed vitae libero a " +
                        "tortor vestibulum ullamcorper vitae ac turpis. Proin posuere nisl sit amet mollis auctor. In" +
                        " vehicula fringilla lorem, a tristique ligula. Vivamus fringilla leo molestie pellentesque " +
                        "vehicula. Nam aliquet condimentum varius. In hac habitasse platea dictumst. Maecenas " +
                        "elementum neque ac ex ultricies auctor. Vestibulum aliquet porttitor enim eu pellentesque. " +
                        "Aenean dapibus tellus ipsum.",
                "catalog-1",
                Arrays.asList("https://www.wikidata.org/wiki/Q27318", "https://purl.org/example#theme"),
                repositoryUrl,
                repository
        );
    }

    public CatalogMetadata catalog2(String repositoryUrl, FDPMetadata repository) {
        return metadataFactory.createCatalogMetadata(
                "Tech Catalog",
                "Nam eget lorem rhoncus, porta odio at, pretium tortor. Morbi dapibus urna magna, at mollis neque " +
                        "sagittis et. Praesent fringilla, justo malesuada gravida cursus, nibh augue semper enim, et " +
                        "efficitur augue justo id odio. Donec id malesuada leo, vel molestie sem. Sed vitae libero a " +
                        "tortor vestibulum ullamcorper vitae ac turpis. Proin posuere nisl sit amet mollis auctor. In" +
                        " vehicula fringilla lorem, a tristique ligula. Vivamus fringilla leo molestie pellentesque " +
                        "vehicula. Nam aliquet condimentum varius. In hac habitasse platea dictumst. Maecenas " +
                        "elementum neque ac ex ultricies auctor. Vestibulum aliquet porttitor enim eu pellentesque. " +
                        "Aenean dapibus tellus ipsum.",
                "catalog-2",
                Arrays.asList("https://www.wikidata.org/wiki/Q27318", "https://purl.org/example#theme"),
                repositoryUrl,
                repository
        );
    }

    public DatasetMetadata dataset1(String repositoryUrl, CatalogMetadata catalog) {
        return metadataFactory.createDatasetMetadata(
                "Cat Dataset",
                "Sed hendrerit accumsan velit, ut eleifend lorem rhoncus a. Curabitur auctor euismod risus lobortis " +
                        "viverra. Donec finibus ultricies venenatis. Suspendisse non pulvinar augue, vel dictum erat." +
                        " Praesent placerat ultrices tempor. Pellentesque posuere sapien eu rutrum efficitur. Quisque" +
                        " ac risus malesuada, tempus diam at, elementum urna. Suspendisse quis posuere leo.",
                "dataset-1",
                Arrays.asList("https://www.wikidata.org/wiki/Q27318", "https://purl.org/example:theme"),
                Arrays.asList("Text Mining", "Natural Language Processing"),
                repositoryUrl,
                catalog
        );
    }

    public DatasetMetadata dataset2(String repositoryUrl, CatalogMetadata catalog) {
        return metadataFactory.createDatasetMetadata(
                "Dog Dataset",
                "Sed hendrerit accumsan velit, ut eleifend lorem rhoncus a. Curabitur auctor euismod risus lobortis " +
                        "viverra. Donec finibus ultricies venenatis. Suspendisse non pulvinar augue, vel dictum erat." +
                        " Praesent placerat ultrices tempor. Pellentesque posuere sapien eu rutrum efficitur. Quisque" +
                        " ac risus malesuada, tempus diam at, elementum urna. Suspendisse quis posuere leo.",
                "dataset-2",
                Arrays.asList("https://www.wikidata.org/wiki/Q27318", "https://purl.org/example:theme"),
                Arrays.asList("Text Mining", "Natural Language Processing"),
                repositoryUrl,
                catalog
        );
    }

    public DistributionMetadata distribution1(String repositoryUrl, DatasetMetadata dataset) {
        return metadataFactory.createDistributionMetadata(
                "Downloadable Distribution",
                "Maecenas et mollis purus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere " +
                        "cubilia Curae; Pellentesque pulvinar augue at ultricies placerat. Vestibulum faucibus sem " +
                        "vel massa egestas consectetur at et nisi. Nullam consectetur, mi et lacinia commodo, arcu " +
                        "eros tempus risus, nec porta justo metus in orci. Pellentesque mattis tortor a ultrices " +
                        "pharetra. Phasellus tristique urna orci, ut vulputate tortor accumsan sit amet. Nulla sed " +
                        "nunc varius, finibus sapien eget, venenatis tortor. Nam gravida diam ut sapien sodales, ut " +
                        "sodales tellus feugiat. Duis auctor rutrum dictum. Phasellus facilisis, nibh at tempus " +
                        "efficitur, odio sem molestie lectus, at bibendum metus orci in nibh. Mauris facilisis est " +
                        "nibh, vitae iaculis risus lacinia at. Aliquam in lectus est.",
                "distribution-1",
                "http://example.com",
                null,
                "text/plain",
                repositoryUrl,
                dataset
        );
    }

    public DistributionMetadata distribution2(String repositoryUrl, DatasetMetadata dataset) {
        return metadataFactory.createDistributionMetadata(
                "Accessible Distribution",
                "Maecenas et mollis purus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere " +
                        "cubilia Curae; Pellentesque pulvinar augue at ultricies placerat. Vestibulum faucibus sem " +
                        "vel massa egestas consectetur at et nisi. Nullam consectetur, mi et lacinia commodo, arcu " +
                        "eros tempus risus, nec porta justo metus in orci. Pellentesque mattis tortor a ultrices " +
                        "pharetra. Phasellus tristique urna orci, ut vulputate tortor accumsan sit amet. Nulla sed " +
                        "nunc varius, finibus sapien eget, venenatis tortor. Nam gravida diam ut sapien sodales, ut " +
                        "sodales tellus feugiat. Duis auctor rutrum dictum. Phasellus facilisis, nibh at tempus " +
                        "efficitur, odio sem molestie lectus, at bibendum metus orci in nibh. Mauris facilisis est " +
                        "nibh, vitae iaculis risus lacinia at. Aliquam in lectus est.",
                "distribution-2",
                null,
                "http://example.com",
                "text/plain",
                repositoryUrl,
                dataset
        );
    }
}
