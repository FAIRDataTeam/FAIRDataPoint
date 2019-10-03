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
package nl.dtls.fairdatapoint.service.metadata;

import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.*;

public class FDPMetadataServiceTest extends BaseIntegrationTest {

    private final static ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();
    private final static String TEST_FDP_URI = "http://example.com/fdp";

    @Autowired
    private MetadataService<FDPMetadata> fdpMetadataService;

    @DirtiesContext
    @Test
    public void storeAndRetrieve() throws MetadataServiceException {
        // WHEN:
        fdpMetadataService.store(createExampleMetadata());

        // THEN:
        assertNotNull(fdpMetadataService.retrieve(exampleIRI()));
    }

    @DirtiesContext
    @Test(expected = MetadataServiceException.class)
    public void storeWithNoTitle() throws Exception {
        // WHEN:
        FDPMetadata metadata = createExampleMetadata();
        metadata.setTitle(null);
        fdpMetadataService.store(metadata);

        // THEN:
        // Expect exception
    }

    @DirtiesContext
    @Test
    public void storeWithNoID() throws MetadataServiceException {
        // WHEN:
        FDPMetadata metadata = createExampleMetadata();
        metadata.setIdentifier(null);
        fdpMetadataService.store(metadata);

        // THEN:
        FDPMetadata mdata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getIdentifier());
    }

    @DirtiesContext
    @Test
    public void storeWithNoRepoID() throws MetadataServiceException {
        // WHEN:
        FDPMetadata metadata = createExampleMetadata();
        metadata.setRepostoryIdentifier(null);
        fdpMetadataService.store(metadata);

        // THEN:
        FDPMetadata mdata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getRepostoryIdentifier());
    }

    @DirtiesContext
    @Test
    public void storeWithNoPublisher() throws MetadataServiceException {
        // WHEN:
        FDPMetadata metadata = createExampleMetadata();
        metadata.setPublisher(null);
        fdpMetadataService.store(metadata);

        // THEN:
        FDPMetadata mdata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getPublisher());
    }

    @DirtiesContext
    @Test
    public void storeWithNoLanguage() throws MetadataServiceException {
        // WHEN:
        FDPMetadata metadata = createExampleMetadata();
        metadata.setLanguage(null);
        fdpMetadataService.store(metadata);

        // THEN:
        FDPMetadata mdata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getLanguage());
    }

    @DirtiesContext
    @Test
    public void storeWithNoLicense() throws MetadataServiceException {
        // WHEN:
        FDPMetadata metadata = createExampleMetadata();
        metadata.setLicense(null);
        fdpMetadataService.store(metadata);

        // THEN:
        FDPMetadata mdata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(mdata.getLicense());
    }

    @DirtiesContext
    @Test
    public void update() throws MetadataServiceException {
        // GIVEN:
        FDPMetadata metadata = createExampleMetadata();
        fdpMetadataService.store(metadata);

        // WHEN:
        Literal title = VALUE_FACTORY.createLiteral("New FDP title");
        metadata.setTitle(title);
        fdpMetadataService.update(exampleIRI(), metadata);

        // THEN:
        FDPMetadata mdata = fdpMetadataService.retrieve(metadata.getUri());
        assertEquals(title, mdata.getTitle());
    }

    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void nullFDPURI() throws Exception {
        // WHEN:
        fdpMetadataService.retrieve((IRI) null);

        // THEN:
        // Expect exception
    }

    @DirtiesContext
    @Test
    public void specsLink() throws Exception {
        // WHEN:
        fdpMetadataService.store(createExampleMetadata());

        // THEN:
        FDPMetadata metadata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(metadata.getSpecification());
    }

    @DirtiesContext
    @Test
    public void metrics() throws Exception {
        // WHEN:
        fdpMetadataService.store(createExampleMetadata());

        // THEN:
        FDPMetadata metadata = fdpMetadataService.retrieve(exampleIRI());
        assertFalse(metadata.getMetrics().isEmpty());
    }

    @DirtiesContext
    @Test
    public void accessRights() throws Exception {
        // WHEN:
        fdpMetadataService.store(createExampleMetadata());

        // THEN:
        FDPMetadata metadata = fdpMetadataService.retrieve(exampleIRI());
        assertNotNull(metadata.getAccessRights().getDescription());
    }

    private static FDPMetadata createExampleMetadata() {
        return ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
    }

    private static IRI exampleIRI() {
        return VALUE_FACTORY.createIRI(TEST_FDP_URI);
    }
}
