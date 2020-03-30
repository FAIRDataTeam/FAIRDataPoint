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
package nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data;

import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairmetadata4j.vocabulary.R3D;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.stereotype.Service;

@Service
public class ResourceDefinitionFixtures {

    public ResourceDefinition repositoryDefinition() {
        return new ResourceDefinition(
                "77aaad6a-0136-4c6e-88b9-07ffccd0ee4c",
                "Repository",
                "",
                "http://www.re3data.org/schema/3-0#Repository",
                "https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata",
                "...",
                R3D.DATACATALOG.stringValue(),
                null
        );
    }

    public ResourceDefinition catalogDefinition(ResourceDefinition parent) {
        return new ResourceDefinition(
                "a0949e72-4466-4d53-8900-9436d1049a4b",
                "Catalog",
                "catalog",
                "http://www.w3.org/ns/dcat#Catalog",
                "https://www.purl.org/fairtools/fdp/schema/0.1/catalogMetadata",
                "...",
                DCAT.HAS_DATASET.stringValue(),
                parent
        );
    }

    public ResourceDefinition datasetDefinition(ResourceDefinition parent) {
        return new ResourceDefinition(
                "2f08228e-1789-40f8-84cd-28e3288c3604",
                "Dataset",
                "dataset",
                "http://www.w3.org/ns/dcat#Dataset",
                "ttps://www.purl.org/fairtools/fdp/schema/0.1/datasetMetadata",
                "...",
                DCAT.HAS_DISTRIBUTION.stringValue(),
                parent
        );
    }

    public ResourceDefinition distributionDefinition(ResourceDefinition parent) {
        return new ResourceDefinition(
                "02c649de-c579-43bb-b470-306abdc808c7",
                "Distribution",
                "distribution",
                "http://www.w3.org/ns/dcat#Distribution",
                "https://www.purl.org/fairtools/fdp/schema/0.1/distributionMetadata",
                "...",
                null,
                parent
        );
    }

}
