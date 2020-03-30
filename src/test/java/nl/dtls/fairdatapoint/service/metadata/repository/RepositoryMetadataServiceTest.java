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
package nl.dtls.fairdatapoint.service.metadata.repository;

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.utils.AuthHelper;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import nl.dtls.fairdatapoint.utils.TestResourceDefinitionFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.*;
import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.*;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.l;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepositoryMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    @Qualifier("repositoryMetadataService")
    private MetadataService repositoryMetadataService;

    @Autowired
    private TestResourceDefinitionFixtures testResourceDefinitionFixtures;

    private ResourceDefinition repositoryRd;

    @BeforeEach
    public void before() {
        authHelper.authenticateAsAlbert();
        repositoryRd = testResourceDefinitionFixtures.repositoryDefinition();
    }


    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        assertNotNull(repositoryMetadataService.retrieve(getUri(repository)));
    }

    @Test
    public void storeWithNoTitle() {
        assertThrows(Exception.class, () -> {
            // GIVEN:
            Model repository = testMetadataFixtures.repositoryMetadata();
            setTitle(repository, getUri(repository), null);

            // WHEN:
            repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        setIdentifier(repository, getUri(repository), null);

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getIdentifier(result));
    }

    @Test
    public void storeWithNoRepoID() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        setRepositoryIdentifier(repository, getUri(repository), null);

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getRepositoryIdentifier(result));
    }

    @Test
    public void storeWithNoPublisher() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        setPublisher(repository, getUri(repository), null);

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getPublisher(result));
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        setLanguage(repository, getUri(repository), null);

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getLanguage(result));
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();
        setLicence(repository, getUri(repository), null);

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getLicence(result));
    }

    @Test
    public void update() throws Exception {
        // GIVEN: Authenticate due to perform changes
        authHelper.authenticateAsAdmin();

        // AND: Prepare data
        Model repository = testMetadataFixtures.repositoryMetadata();
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // WHEN:
        Literal title = l("New FDP title");
        setTitle(repository, getUri(repository), title);
        repositoryMetadataService.update(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertEquals(title, getTitle(result));
    }

    @Test
    public void nullFDPURI() {
        assertThrows(NullPointerException.class, () -> {
            // WHEN:
            repositoryMetadataService.retrieve((IRI) null);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void specsLink() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getSpecification(result));
    }

    @Test
    public void metrics() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertFalse(getMetrics(result).isEmpty());
    }

    @Test
    public void accessRights() throws Exception {
        // GIVEN:
        Model repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository, getUri(repository), repositoryRd);

        // THEN:
        Model result = repositoryMetadataService.retrieve(getUri(repository));
        assertNotNull(getAccessRights(result).getDescription());
    }

}
