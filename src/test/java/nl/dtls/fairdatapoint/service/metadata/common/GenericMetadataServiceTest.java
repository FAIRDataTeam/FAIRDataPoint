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

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.utils.AuthHelper;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.*;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataSetter.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.junit.jupiter.api.Assertions.*;

public class GenericMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    @Qualifier("genericMetadataService")
    private MetadataService genericMetadataService;

    @Autowired
    @Qualifier("catalogMetadataService")
    private MetadataService catalogMetadataService;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private ResourceDefinitionFixtures resourceDefinitionFixtures;

    private ResourceDefinition distributionRd;

    @BeforeEach
    public void before() {
        authHelper.authenticateAsAlbert();
        distributionRd = resourceDefinitionFixtures.distributionDefinition();
    }

    @Test
    public void retrieveNonExitingMetadata() {
        assertThrows(ResourceNotFoundException.class, () -> {
            // GIVEN:
            IRI repositoryUri = getUri(testMetadataFixtures.repositoryMetadata());
            IRI datasetUri = i(format("%s/non-existing", repositoryUri));

            // WHEN:
            genericMetadataService.retrieve(datasetUri);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void existenceDatasetMetaDataSpecsLink() throws Exception {
        // GIVEN:
        Model distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), distributionRd);

        // THEN:
        Model metadata = genericMetadataService.retrieve(getUri(distribution));
        assertNotNull(getSpecification(metadata));
    }

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        Model distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), distributionRd);

        // THEN:
        assertNotNull(genericMetadataService.retrieve(getUri(distribution)));
    }

    @Test
    public void storeWithNoParentURI() {
        assertThrows(MetadataServiceException.class, () -> {
            // GIVEN:
            Model distribution = testMetadataFixtures.c1_d1_distribution1();
            setParent(distribution, getUri(distribution), null);

            // WHEN:
            genericMetadataService.store(distribution, getUri(distribution), distributionRd);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithWrongParentURI() {
        assertThrows(ValidationException.class, () -> {
            // GIVEN:
            Model repository = testMetadataFixtures.repositoryMetadata();
            Model distribution = testMetadataFixtures.c1_d1_distribution1();
            setParent(distribution, getUri(distribution), getUri(repository));

            // WHEN:
            genericMetadataService.store(distribution, getUri(distribution), distributionRd);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        Model distribution = testMetadataFixtures.c1_d1_distribution1();
        setMetadataIdentifier(distribution, getUri(distribution), null);

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), distributionRd);

        // THEN:
        Model mdata = genericMetadataService.retrieve(getUri(distribution));
        assertNotNull(getMetadataIdentifier(mdata));
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        Model distribution = testMetadataFixtures.c1_d1_distribution1();
        setLicence(distribution, getUri(distribution), null);

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), distributionRd);

        // THEN:
        Model mdata = genericMetadataService.retrieve(getUri(distribution));
        assertNotNull(getLicence(mdata));
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        Model distribution = testMetadataFixtures.c1_d1_distribution1();
        setLanguage(distribution, getUri(distribution), null);

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), distributionRd);

        // THEN:
        Model mdata = genericMetadataService.retrieve(getUri(distribution));
        assertNotNull(getLanguage(mdata));
    }

    @Test
    public void updateParent() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        Model catalog = testMetadataFixtures.catalog1();
        Model dataset = testMetadataFixtures.c1_dataset1();
        Model distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), distributionRd);

        // THEN:
        Model updatedRepository = genericMetadataService.retrieve(getUri(repository));
        Model updatedCatalog = catalogMetadataService.retrieve(getUri(catalog));
        Model updatedDataset = genericMetadataService.retrieve(getUri(dataset));
        Model storedDistribution = genericMetadataService.retrieve(getUri(distribution));
        LocalDateTime repositoryModified = getModified(updatedRepository);
        LocalDateTime catalogModified = getModified(updatedCatalog);
        LocalDateTime datasetModified = getModified(updatedDataset);
        LocalDateTime distributionModified = getModified(storedDistribution);
        assertFalse(datasetModified.isBefore(distributionModified), "Dataset modified is not after Distribution " +
                "modified");
        assertFalse(catalogModified.isBefore(distributionModified), "Catalog modified is not after Dataset modified");
        assertFalse(repositoryModified.isBefore(distributionModified), "FDP modified is not after Dataset modified");
    }

}
