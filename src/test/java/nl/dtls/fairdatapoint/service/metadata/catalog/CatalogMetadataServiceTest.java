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

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
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

public class CatalogMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    @Qualifier("genericMetadataService")
    private MetadataService genericMetadataService;

    @Autowired
    @Qualifier("catalogMetadataService")
    private MetadataService catalogMetadataService;

    @Autowired
    private ResourceDefinitionFixtures resourceDefinitionFixtures;

    private ResourceDefinition catalogRd;

    @BeforeEach
    public void setUp() throws Exception {
        catalogRd = resourceDefinitionFixtures.catalogDefinition();
    }

    @Test
    public void retrieveNonExitingMetadata() {
        assertThrows(ResourceNotFoundException.class, () -> {
            // GIVEN:
            IRI repositoryUri = getUri(testMetadataFixtures.repositoryMetadata());
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
        Model catalog = testMetadataFixtures.catalog3();
        catalogMetadataService.store(catalog, getUri(catalog), catalogRd);

        // WHEN:
        Model metadata = catalogMetadataService.retrieve(getUri(catalog));

        // THEN:
        assertNotNull(getSpecification(metadata));
    }

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog3();

        // WHEN:
        catalogMetadataService.store(catalog, getUri(catalog), catalogRd);

        // THEN:
        assertNotNull(catalogMetadataService.retrieve(getUri(catalog)));
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog3();
        setMetadataIdentifier(catalog, getUri(catalog), null);

        // WHEN:
        catalogMetadataService.store(catalog, getUri(catalog), catalogRd);

        // THEN:
        Model result = catalogMetadataService.retrieve(getUri(catalog));
        assertNotNull(getMetadataIdentifier(result));
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog3();
        setLanguage(catalog, getUri(catalog), null);

        // WHEN:
        catalogMetadataService.store(catalog, getUri(catalog), catalogRd);

        // THEN:
        Model result = catalogMetadataService.retrieve(getUri(catalog));
        assertNotNull(getLanguage(result));
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        Model catalog = testMetadataFixtures.catalog3();
        setLicence(catalog, getUri(catalog), null);

        // WHEN:
        catalogMetadataService.store(catalog, getUri(catalog), catalogRd);

        // THEN:
        Model result = catalogMetadataService.retrieve(getUri(catalog));
        assertNotNull(getLicence(result));
    }

    @Test
    public void updateParent() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        Model catalog = testMetadataFixtures.catalog3();

        // WHEN:
        catalogMetadataService.store(catalog, getUri(catalog), catalogRd);

        // THEN:
        Model updatedRepository = genericMetadataService.retrieve(getUri(repository));
        Model updatedCatalog = catalogMetadataService.retrieve(getUri(catalog));
        LocalDateTime repositoryModified = getModified(updatedRepository);
        LocalDateTime catalogModified = getModified(updatedCatalog);
        assertFalse(repositoryModified.isBefore(catalogModified), "FDP modified is not after Catalog modified");
    }

}
