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

import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.RepositoryMetadataChangeDTO;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.utils.AuthHelper;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepositoryMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private MetadataService<FDPMetadata, RepositoryMetadataChangeDTO> repositoryMetadataService;

    @Test
    public void storeAndRetrieve() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        assertNotNull(repositoryMetadataService.retrieve(repository.getUri()));
    }

    @Test
    public void storeWithNoTitle() {
        assertThrows(Exception.class, () -> {
            // GIVEN:
            FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
            repository.setTitle(null);

            // WHEN:
            repositoryMetadataService.store(repository);

            // THEN:
            // Expect exception
        });
    }

    @Test
    public void storeWithNoID() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        repository.setIdentifier(null);

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getIdentifier());
    }

    @Test
    public void storeWithNoRepoID() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        repository.setRepostoryIdentifier(null);

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getRepostoryIdentifier());
    }

    @Test
    public void storeWithNoPublisher() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        repository.setPublisher(null);

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getPublisher());
    }

    @Test
    public void storeWithNoLanguage() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        repository.setLanguage(null);

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getLanguage());
    }

    @Test
    public void storeWithNoLicense() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        repository.setLicense(null);

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getLicense());
    }

    @Test
    public void update() throws Exception {
        // GIVEN: Authenticate due to perform changes
        authHelper.authenticateAsAdmin();

        // AND: Prepare data
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();
        repositoryMetadataService.store(repository);

        // WHEN:
        Literal title = l("New FDP title");
        repository.setTitle(title);
        repositoryMetadataService.update(repository.getUri(), repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertEquals(title, result.getTitle());
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
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getSpecification());
    }

    @Test
    public void metrics() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertFalse(result.getMetrics().isEmpty());
    }

    @Test
    public void accessRights() throws Exception {
        // GIVEN:
        FDPMetadata repository = testMetadataFixtures.repositoryMetadata();

        // WHEN:
        repositoryMetadataService.store(repository);

        // THEN:
        FDPMetadata result = repositoryMetadataService.retrieve(repository.getUri());
        assertNotNull(result.getAccessRights().getDescription());
    }

}
