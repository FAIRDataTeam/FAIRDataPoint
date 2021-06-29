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

import nl.dtls.fairdatapoint.database.mongo.migration.development.shape.data.ShapeFixtures;
import nl.dtls.fairdatapoint.entity.resource.*;
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

    public static String ONTOLOGY_DEFINITION_UUID = "4bc19f45-845d-48d6-ade7-ac2664563f60";

    public ResourceDefinition repositoryDefinition() {
        return new ResourceDefinition(
                REPOSITORY_DEFINITION_UUID,
                "Repository",
                "",
                List.of(ShapeFixtures.RESOURCE_SHAPE_UUID, ShapeFixtures.REPOSITORY_SHAPE_UUID),
                List.of(new ResourceDefinitionChild(
                        CATALOG_DEFINITION_UUID,
                        R3D.DATACATALOG.stringValue(),
                        new ResourceDefinitionChildListView(
                                "Catalogs",
                                "http://www.w3.org/ns/dcat#themeTaxonomy",
                                List.of()
                        )
                )),
                List.of()
        );
    }

    public ResourceDefinition catalogDefinition() {
        return new ResourceDefinition(
                CATALOG_DEFINITION_UUID,
                "Catalog",
                "catalog",
                List.of(ShapeFixtures.RESOURCE_SHAPE_UUID, ShapeFixtures.CATALOG_SHAPE_UUID),
                List.of(new ResourceDefinitionChild(
                        DATASET_DEFINITION_UUID,
                        DCAT.HAS_DATASET.stringValue(),
                        new ResourceDefinitionChildListView(
                                "Datasets",
                                "http://www.w3.org/ns/dcat#theme",
                                List.of()
                        )
                )),
                List.of()
        );
    }

    public ResourceDefinition datasetDefinition() {
        return new ResourceDefinition(
                DATASET_DEFINITION_UUID,
                "Dataset",
                "dataset",
                List.of(ShapeFixtures.RESOURCE_SHAPE_UUID, ShapeFixtures.DATASET_SHAPE_UUID),
                List.of(new ResourceDefinitionChild(
                        DISTRIBUTION_DEFINITION_UUID,
                        DCAT.HAS_DISTRIBUTION.stringValue(),
                        new ResourceDefinitionChildListView("Distributions", null, List.of(
                                new ResourceDefinitionChildListViewMetadata(
                                        "Media Type",
                                        "http://www.w3.org/ns/dcat#mediaType"
                                )
                        ))
                )),
                List.of()
        );
    }

    public ResourceDefinition distributionDefinition() {
        return new ResourceDefinition(
                DISTRIBUTION_DEFINITION_UUID,
                "Distribution",
                "distribution",
                List.of(ShapeFixtures.RESOURCE_SHAPE_UUID, ShapeFixtures.DISTRIBUTION_SHAPE_UUID),
                List.of(),
                List.of(
                        new ResourceDefinitionLink("Access online", "http://www.w3.org/ns/dcat#accessURL"),
                        new ResourceDefinitionLink("Download", "http://www.w3.org/ns/dcat#downloadURL")
                )
        );
    }

    public ResourceDefinition ontologyDefinition() {
        return new ResourceDefinition(
                ONTOLOGY_DEFINITION_UUID,
                "Ontology",
                "ontology",
                List.of(ShapeFixtures.RESOURCE_SHAPE_UUID),
                List.of(),
                List.of()
        );
    }

}
