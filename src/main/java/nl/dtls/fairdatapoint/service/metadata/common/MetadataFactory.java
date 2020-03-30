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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;

import java.time.LocalDateTime;
import java.util.List;

public interface MetadataFactory {
    Model createFDPMetadata(String title, String description, String fdpUrl);

    Model createCatalogMetadata(String title, String description, String identifier, List<String> themeTaxonomies,
                                String repositoryUrl, IRI repository);

    Model createDatasetMetadata(String title, String description, String identifier, List<String> themes,
                                List<String> keywords, String fdpUrl, IRI catalog);

    Model createDistributionMetadata(String title, String description, String identifier, String downloadUrl,
                                     String accessUrl, String mediaType, String fdpUrl, IRI dataset);

}
