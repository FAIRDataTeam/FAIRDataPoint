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

import org.fairdatateam.fairdatapoint.BaseIntegrationTest;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.ResourceDefinitionFixtures;
import org.fairdatateam.fairdatapoint.common.error.ResourceNotFoundException;
import org.fairdatateam.fairdatapoint.common.error.ValidationException;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.testfixtures.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

import static java.lang.String.format;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.*;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataSetter.*;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class GenericMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Autowired
    private GenericMetadataService genericMetadataService;

    @Autowired
    private CatalogMetadataService catalogMetadataService;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private ResourceDefinitionFixtures resourceDefinitionFixtures;

    @BeforeEach
    public void before() {
        // todo: replace authHelper by simple mock of MemberService.check* methods
        authHelper.authenticateAsAdmin();
    }

    @Test
    public void retrieveNonExistingMetadataThrowsError() {
        // GIVEN:
        IRI fdpUri = getUri(testMetadataFixtures.fdpMetadata());
        IRI metadataUri = i(format("%s/distribution/non-existing", fdpUri));

        // WHEN:
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> genericMetadataService.retrieve(metadataUri)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo(format("No metadata found for the uri '%s'", metadataUri))));
    }

    @Test
    public void storeWorks() throws Exception {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model metadata = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        genericMetadataService.store(metadata, getUri(metadata), metadataRd);

        // THEN:
        Model metadataFromDB = genericMetadataService.retrieve(getUri(metadata));
        assertNotNull(metadataFromDB);
    }

    @Test
    public void storeWithNoParentURIThrowsError() {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model metadata = testMetadataFixtures.c1_d1_distribution1();
        setParent(metadata, getUri(metadata), null);

        // WHEN:
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> genericMetadataService.store(metadata, getUri(metadata), metadataRd)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo("Metadata has no parent")));
    }

    @Test
    public void storeWithWrongParentURIThrowsError() {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model fdp = testMetadataFixtures.fdpMetadata();
        Model metadata = testMetadataFixtures.c1_d1_distribution1();
        setParent(metadata, getUri(metadata), getUri(fdp));

        // WHEN:
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> genericMetadataService.store(metadata, getUri(metadata), metadataRd)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo("Parent is not of correct type (RD: FAIR Data Point)")));
    }

    @Test
    public void storeWithNoMetadataIdentifier() throws Exception {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model metadata = testMetadataFixtures.c1_d1_distribution1();
        setMetadataIdentifier(metadata, getUri(metadata), null);

        // WHEN:
        genericMetadataService.store(metadata, getUri(metadata), metadataRd);

        // THEN:
        Model metadataFromDB = genericMetadataService.retrieve(getUri(metadata));
        assertNotNull(getMetadataIdentifier(metadataFromDB));
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model metadata = testMetadataFixtures.c1_d1_distribution1();
        setLicence(metadata, getUri(metadata), null);

        // WHEN:
        genericMetadataService.store(metadata, getUri(metadata), metadataRd);

        // THEN:
        Model metadataFromDB = genericMetadataService.retrieve(getUri(metadata));
        assertNotNull(getLicence(metadataFromDB));
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model metadata = testMetadataFixtures.c1_d1_distribution1();
        setLanguage(metadata, getUri(metadata), null);

        // WHEN:
        genericMetadataService.store(metadata, getUri(metadata), metadataRd);

        // THEN:
        Model metadataFromDB = genericMetadataService.retrieve(getUri(metadata));
        assertNotNull(getLanguage(metadataFromDB));
    }

    @Test
    public void updateParent() throws Exception {
        // GIVEN:
        ResourceDefinition metadataRd = resourceDefinitionFixtures.distributionDefinition();
        Model fdp = testMetadataFixtures.fdpMetadata();
        Model catalog = testMetadataFixtures.catalog1();
        Model dataset = testMetadataFixtures.c1_dataset1();
        Model distribution = testMetadataFixtures.c1_d1_distribution1();

        // WHEN:
        genericMetadataService.store(distribution, getUri(distribution), metadataRd);

        // THEN:
        Model updatedRepository = genericMetadataService.retrieve(getUri(fdp));
        Model updatedCatalog = catalogMetadataService.retrieve(getUri(catalog));
        Model updatedDataset = genericMetadataService.retrieve(getUri(dataset));
        Model storedDistribution = genericMetadataService.retrieve(getUri(distribution));
        OffsetDateTime repositoryModified = getModified(updatedRepository);
        OffsetDateTime catalogModified = getModified(updatedCatalog);
        OffsetDateTime datasetModified = getModified(updatedDataset);
        OffsetDateTime distributionModified = getModified(storedDistribution);
        assertFalse(datasetModified.isBefore(distributionModified), "Dataset modified is not after Distribution " +
                "modified");
        assertFalse(catalogModified.isBefore(distributionModified), "Catalog modified is not after Dataset modified");
        assertFalse(repositoryModified.isBefore(distributionModified), "FDP modified is not after Dataset modified");
    }

//    @Test
//    @DisplayName("'save' should evict cache")
//    public void saveEvictsCache() throws RdfRepositoryException {
//        // GIVEN:
//        Model catalog = testMetadataFixtures.catalog1();
//        IRI catalogUri = getUri(catalog);
//        Model dataset = testMetadataFixtures.c1_dataset1();
//
//        // AND: Compute cache
//        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // AND: Check if cache is full
//        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));
//
//        // WHEN:
//        metadataRdfRepository.save(new ArrayList<>(dataset), getUri(dataset));
//
//        // THEN:
//        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
//    }
//
//    @Test
//    @DisplayName("'removeStatement' should evict cache")
//    public void removeStatementEvictsCache() throws RdfRepositoryException {
//        // GIVEN:
//        Model catalog = testMetadataFixtures.catalog1();
//        IRI catalogUri = getUri(catalog);
//        Model dataset = testMetadataFixtures.c1_dataset1();
//
//        // AND: Compute cache
//        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // AND: Check if cache is full
//        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));
//
//        // WHEN:
//        metadataRdfRepository.removeStatement(getUri(dataset), DCTERMS.LANGUAGE, getLanguage(dataset), getUri(dataset));
//
//        // THEN:
//        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
//    }
//
//    @Test
//    @DisplayName("'remove' should evict cache")
//    public void removeEvictsCache() throws RdfRepositoryException {
//        // GIVEN:
//        Model catalog = testMetadataFixtures.catalog1();
//        IRI catalogUri = getUri(catalog);
//        Model dataset = testMetadataFixtures.c1_dataset1();
//
//        // AND: Compute cache
//        metadataRdfRepository.getDatasetThemesForCatalog(catalogUri);
//
//        // AND: Check if cache is full
//        assertThat(getCache().get(catalogUri.stringValue()), is(notNullValue()));
//
//        // WHEN:
//        metadataRdfRepository.remove(getUri(dataset));
//
//        // THEN:
//        assertThat(getCache().get(catalogUri.stringValue()), is(nullValue()));
//    }
//
//    private Cache getCache() {
//        return cacheManager.getCache(CATALOG_THEMES_CACHE);
//    }

}
