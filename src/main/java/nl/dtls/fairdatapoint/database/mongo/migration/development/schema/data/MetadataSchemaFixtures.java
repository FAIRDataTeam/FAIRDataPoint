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
package nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data;

import lombok.SneakyThrows;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Service;

import java.util.Set;

import static nl.dtls.fairdatapoint.util.ResourceReader.loadClassResource;

@Service
public class MetadataSchemaFixtures {

    @SneakyThrows
    public MetadataSchema resourceSchema() {
        String definition = loadClassResource("shape-resource.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_RESOURCE_UUID,
                "Resource",
                false,
                MetadataSchemaType.INTERNAL,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Resource")
        );
    }

    @SneakyThrows
    public MetadataSchema fdpSchema() {
        String definition = loadClassResource("shape-fdp.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_FDP_UUID,
                "FAIR Data Point",
                false,
                MetadataSchemaType.INTERNAL,
                definition,
                Set.of("https://w3id.org/fdp/fdp-o#FAIRDataPoint")
        );
    }

    @SneakyThrows
    public MetadataSchema dataServiceSchema() {
        String definition = loadClassResource("shape-data-service.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_DATASERVICE_UUID,
                "Data Service",
                false,
                MetadataSchemaType.INTERNAL,
                definition,
                Set.of("http://www.w3.org/ns/dcat#DataService")
        );
    }

    @SneakyThrows
    public MetadataSchema metadataServiceSchema() {
        String definition = loadClassResource("shape-metadata-service.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_METADATASERVICE_UUID,
                "Metadata Service",
                false,
                MetadataSchemaType.INTERNAL,
                definition,
                Set.of("https://w3id.org/fdp/fdp-o#MetadataService")
        );
    }

    @SneakyThrows
    public MetadataSchema catalogSchema() {
        String definition = loadClassResource("shape-catalog.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_CATALOG_UUID,
                "Catalog",
                false,
                MetadataSchemaType.INTERNAL,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Catalog")
        );
    }

    @SneakyThrows
    public MetadataSchema datasetSchema() {
        String definition = loadClassResource("shape-dataset.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_DATASET_UUID,
                "Dataset",
                false,
                MetadataSchemaType.CUSTOM,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Dataset")
        );
    }

    @SneakyThrows
    public MetadataSchema distributionSchema() {
        String definition = loadClassResource("shape-distribution.ttl", getClass());
        return new MetadataSchema(
                null,
                KnownUUIDs.SCHEMA_DISTRIBUTION_UUID,
                "Distribution",
                false,
                MetadataSchemaType.CUSTOM,
                definition,
                Set.of("http://www.w3.org/ns/dcat#Distribution")
        );
    }

    @SneakyThrows
    public MetadataSchema customSchema() {
        String definition = loadClassResource("shape-custom.ttl", getClass());
        return new MetadataSchema(
                null,
                "ceba9984-9838-4be2-a2a7-12213016fd96",
                "Custom Shape",
                false,
                MetadataSchemaType.CUSTOM,
                definition,
                Set.of("http://example.org/Dog")
        );
    }

    @SneakyThrows
    public MetadataSchema customSchemaEdited(){
        String definition = loadClassResource("shape-custom-edited.ttl", getClass());
        return new MetadataSchema(
                null,
                customSchema().getUuid(),
                customSchema().getName(),
                false,
                customSchema().getType(),
                definition,
                Set.of("http://example.org/Dog")
        );
    }

}
