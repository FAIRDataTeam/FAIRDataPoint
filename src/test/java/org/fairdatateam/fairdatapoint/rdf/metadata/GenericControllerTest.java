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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.fairdatateam.fairdatapoint.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles(Profiles.TESTING)
@SpringBootTest
public class GenericControllerTest {

    @Autowired
    GenericController genericController;

    @Autowired
    String persistentUrl;

    @Test
    public void getChildResourceUrisReturnsSorted(
    ) throws MetadataRdfRepositoryException, MetadataServiceException {
        final String urlPrefix = "catalog";
        final String childPrefix = "dataset";
        final IRI entityUri = i(persistentUrl + "/catalog/catalog-1");
        final IRI relationUri = DCAT.HAS_DATASET;
        final List<IRI> actualUris = genericController.getChildResourceUris(
                urlPrefix, childPrefix, entityUri, relationUri);
        // expectations based on TestRdfMetadataFixtures
        final List<IRI> expectedUris = List.of(
                i(persistentUrl + "/dataset/dataset-1"),
                i(persistentUrl + "/dataset/dataset-2"));
        assertEquals(expectedUris, actualUris);
    }
}
