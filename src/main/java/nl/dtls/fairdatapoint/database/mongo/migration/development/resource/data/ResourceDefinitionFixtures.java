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

import nl.dtls.fairdatapoint.entity.resource.*;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceDefinitionFixtures {

    public static final String ONTOLOGY_DEFINITION_UUID = "4bc19f45-845d-48d6-ade7-ac2664563f60";

    public ResourceDefinition fdpDefinition() {
        return new ResourceDefinition(
                KnownUUIDs.RD_FDP_UUID,
                "FAIR Data Point",
                "",
                List.of(KnownUUIDs.SCHEMA_FDP_UUID),
                List.of(new ResourceDefinitionChild(
                        KnownUUIDs.RD_CATALOG_UUID,
                        FDP.METADATACATALOG.stringValue(),
                        new ResourceDefinitionChildListView(
                                "Catalogs",
                                DCAT.THEME_TAXONOMY.stringValue(),
                                List.of()
                        )
                )),
                List.of()
        );
    }

    public ResourceDefinition catalogDefinition() {
        return new ResourceDefinition(
                KnownUUIDs.RD_CATALOG_UUID,
                "Catalog",
                "catalog",
                List.of(KnownUUIDs.SCHEMA_CATALOG_UUID),
                List.of(new ResourceDefinitionChild(
                        KnownUUIDs.RD_DATASET_UUID,
                        DCAT.HAS_DATASET.stringValue(),
                        new ResourceDefinitionChildListView(
                                "Datasets",
                                DCAT.THEME.stringValue(),
                                List.of()
                        )
                )),
                List.of()
        );
    }

    public ResourceDefinition datasetDefinition() {
        return new ResourceDefinition(
                KnownUUIDs.RD_DATASET_UUID,
                "Dataset",
                "dataset",
                List.of(KnownUUIDs.SCHEMA_DATASET_UUID),
                List.of(new ResourceDefinitionChild(
                        KnownUUIDs.RD_DISTRIBUTION_UUID,
                        DCAT.HAS_DISTRIBUTION.stringValue(),
                        new ResourceDefinitionChildListView("Distributions", null, List.of(
                                new ResourceDefinitionChildListViewMetadata(
                                        "Media Type",
                                        DCAT.MEDIA_TYPE.stringValue()
                                )
                        ))
                )),
                List.of()
        );
    }

    public ResourceDefinition distributionDefinition() {
        return new ResourceDefinition(
                KnownUUIDs.RD_DISTRIBUTION_UUID,
                "Distribution",
                "distribution",
                List.of(KnownUUIDs.SCHEMA_DISTRIBUTION_UUID),
                List.of(),
                List.of(
                        new ResourceDefinitionLink("Access online", DCAT.ACCESS_URL.stringValue()),
                        new ResourceDefinitionLink("Download", DCAT.DOWNLOAD_URL.stringValue())
                )
        );
    }

    public ResourceDefinition ontologyDefinition() {
        return new ResourceDefinition(
                ONTOLOGY_DEFINITION_UUID,
                "Data Service",
                "data-service",
                List.of(KnownUUIDs.SCHEMA_DATASERVICE_UUID),
                List.of(),
                List.of()
        );
    }

}
