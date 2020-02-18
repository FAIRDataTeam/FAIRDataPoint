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
package nl.dtls.fairdatapoint.service.metadata.catalog;

import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CatalogMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Test
    public void retrieveNonExitingMetadata() {
        assertThrows(ResourceNotFoundException.class, () -> {
            // GIVEN:
            IRI repositoryUri = testMetadataFixtures.repositoryMetadata().getUri();
            IRI catalogUri = i(format("%s/non-existing", repositoryUri));

            // WHEN:
            catalogMetadataService.retrieve(catalogUri);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void specsLink() throws Exception {
        // GIVEN:
        CatalogMetadata catalog = testMetadataFixtures.catalog3();
        catalogMetadataService.store(catalog);

        // WHEN:
        CatalogMetadata metadata = catalogMetadataService.retrieve(catalog.getUri());

        // THEN:
        assertNotNull(metadata.getSpecification());
    }

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        CatalogMetadata catalog = testMetadataFixtures.catalog3();

        // WHEN:
        catalogMetadataService.store(catalog);

        // THEN:
        assertNotNull(catalogMetadataService.retrieve(catalog.getUri()));
    }

    @Test
    public void storeWithNoParentUri() {
        assertThrows(IllegalStateException.class, () -> {
            // GIVEN:
            CatalogMetadata catalog = testMetadataFixtures.catalog3();
            catalog.setParentURI(null);

            // WHEN:
            catalogMetadataService.store(catalog);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        CatalogMetadata catalog = testMetadataFixtures.catalog3();
        catalog.setIdentifier(null);

        // WHEN:
        catalogMetadataService.store(catalog);

        // THEN:
        CatalogMetadata result = catalogMetadataService.retrieve(catalog.getUri());
        assertNotNull(result.getIdentifier());
    }

    @Test
    public void storeWithNoPublisher() throws Exception {
        // GIVEN:
        CatalogMetadata catalog = testMetadataFixtures.catalog3();
        catalog.setPublisher(null);

        // WHEN:
        catalogMetadataService.store(catalog);

        // THEN:
        CatalogMetadata result = catalogMetadataService.retrieve(catalog.getUri());
        assertNotNull(result.getPublisher());
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        CatalogMetadata catalog = testMetadataFixtures.catalog3();
        catalog.setLanguage(null);

        // WHEN:
        catalogMetadataService.store(catalog);

        // THEN:
        CatalogMetadata result = catalogMetadataService.retrieve(catalog.getUri());
        assertNotNull(result.getLanguage());
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        CatalogMetadata catalog = testMetadataFixtures.catalog3();
        catalog.setLicense(null);

        // WHEN:
        catalogMetadataService.store(catalog);

        // THEN:
        CatalogMetadata result = catalogMetadataService.retrieve(catalog.getUri());
        assertNotNull(result.getLicense());
    }

    @Test
    public void updateParent() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        CatalogMetadata catalog = testMetadataFixtures.catalog3();

        // WHEN:
        catalogMetadataService.store(catalog);

        // THEN:
        FDPMetadata updatedRepository = repositoryMetadataService.retrieve(repository.getUri());
        CatalogMetadata updatedCatalog = catalogMetadataService.retrieve(catalog.getUri());
        ZonedDateTime repositoryModified = ZonedDateTime.parse(updatedRepository.getModified().stringValue());
        ZonedDateTime catalogModified = ZonedDateTime.parse(updatedCatalog.getModified().stringValue());
        assertFalse("FDP modified is not after Catalog modified", repositoryModified.isBefore(catalogModified));
    }

}
