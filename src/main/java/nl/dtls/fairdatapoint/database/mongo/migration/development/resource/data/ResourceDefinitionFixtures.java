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
import nl.dtls.fairdatapoint.vocabulary.R3D;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceDefinitionFixtures {

    public static String REPOSITORY_DEFINITION_UUID = "77aaad6a-0136-4c6e-88b9-07ffccd0ee4c";

    public static String CATALOG_DEFINITION_UUID = "a0949e72-4466-4d53-8900-9436d1049a4b";

    public static String DATASET_DEFINITION_UUID = "2f08228e-1789-40f8-84cd-28e3288c3604";

    public static String DISTRIBUTION_DEFINITION_UUID = "02c649de-c579-43bb-b470-306abdc808c7";

    public ResourceDefinition repositoryDefinition() {
        return new ResourceDefinition(
                REPOSITORY_DEFINITION_UUID,
                "Repository",
                "",
                "http://www.re3data.org/schema/3-0#Repository",
                "https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata",
                List.of("http://www.w3.org/ns/dcat#Resource", "http://www.re3data.org/schema/3-0#Repository"),
                R3D.DATACATALOG.stringValue(),
                null,
                CATALOG_DEFINITION_UUID
        );
    }

    public ResourceDefinition catalogDefinition() {
        return new ResourceDefinition(
                "a0949e72-4466-4d53-8900-9436d1049a4b",
                "Catalog",
                "catalog",
                "http://www.w3.org/ns/dcat#Catalog",
                "https://www.purl.org/fairtools/fdp/schema/0.1/catalogMetadata",
                List.of("http://www.w3.org/ns/dcat#Resource", "http://www.w3.org/ns/dcat#Catalog"),
                DCAT.HAS_DATASET.stringValue(),
                REPOSITORY_DEFINITION_UUID,
                DATASET_DEFINITION_UUID
        );
    }

    public ResourceDefinition datasetDefinition() {
        return new ResourceDefinition(
                DATASET_DEFINITION_UUID,
                "Dataset",
                "dataset",
                "http://www.w3.org/ns/dcat#Dataset",
                "https://www.purl.org/fairtools/fdp/schema/0.1/datasetMetadata",
                List.of("http://www.w3.org/ns/dcat#Resource", "http://www.w3.org/ns/dcat#Dataset"),
                DCAT.HAS_DISTRIBUTION.stringValue(),
                CATALOG_DEFINITION_UUID,
                DISTRIBUTION_DEFINITION_UUID
        );
    }

    public ResourceDefinition distributionDefinition() {
        return new ResourceDefinition(
                DISTRIBUTION_DEFINITION_UUID,
                "Distribution",
                "distribution",
                "http://www.w3.org/ns/dcat#Distribution",
                "https://www.purl.org/fairtools/fdp/schema/0.1/distributionMetadata",
                List.of("http://www.w3.org/ns/dcat#Resource", "http://www.w3.org/ns/dcat#Distribution"),
                null,
                DATASET_DEFINITION_UUID,
                null
        );
    }

}
