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
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static nl.dtls.fairdatapoint.util.ResourceReader.loadClassResource;

@Service
public class MetadataSchemaFixtures {

    private static final SemVer VERSION = new SemVer("1.0.0");

    private MetadataSchema createSchemaFixture(
            String uuid,
            String name,
            String definition,
            Set<String> targetClasses,
            List<String> extendsSchemas,
            MetadataSchemaType type
    ) {
        return new MetadataSchema(
                null,
                uuid,
                VERSION.toString(),
                VERSION,
                name,
                "",
                definition,
                targetClasses,
                extendsSchemas,
                type,
                null,
                true,
                true,
                false,
                Instant.now(),
                null
        );
    }

    private MetadataSchemaDraft createSchemaDraftFixture(
            String uuid,
            String name,
            String definition,
            Set<String> targetClasses
    ) {
        return new MetadataSchemaDraft(
                null,
                uuid,
                name,
                "",
                false,
                definition,
                targetClasses,
                Collections.emptyList(),
                Instant.now(),
                Instant.now()
        );
    }

    @SneakyThrows
    public MetadataSchema resourceSchema() {
        String definition = loadClassResource("shape-resource.ttl", getClass());
        MetadataSchema schema = createSchemaFixture(
                KnownUUIDs.SCHEMA_RESOURCE_UUID,
                "Resource",
                definition,
                Set.of("http://www.w3.org/ns/dcat#Resource"),
                Collections.emptyList(),
                MetadataSchemaType.INTERNAL
        );
        schema.setAbstractSchema(true);
        return schema;
    }

    @SneakyThrows
    public MetadataSchema fdpSchema() {
        String definition = loadClassResource("shape-fdp.ttl", getClass());
        return createSchemaFixture(
                KnownUUIDs.SCHEMA_FDP_UUID,
                "FAIR Data Point",
                definition,
                Set.of("https://w3id.org/fdp/fdp-o#FAIRDataPoint"),
                Collections.singletonList(KnownUUIDs.SCHEMA_METADATASERVICE_UUID),
                MetadataSchemaType.INTERNAL
        );
    }

    @SneakyThrows
    public MetadataSchema dataServiceSchema() {
        String definition = loadClassResource("shape-data-service.ttl", getClass());
        return createSchemaFixture(
                KnownUUIDs.SCHEMA_DATASERVICE_UUID,
                "Data Service",
                definition,
                Set.of("http://www.w3.org/ns/dcat#DataService"),
                Collections.singletonList(KnownUUIDs.SCHEMA_RESOURCE_UUID),
                MetadataSchemaType.INTERNAL
        );
    }

    @SneakyThrows
    public MetadataSchema metadataServiceSchema() {
        String definition = loadClassResource("shape-metadata-service.ttl", getClass());
        return createSchemaFixture(
                KnownUUIDs.SCHEMA_METADATASERVICE_UUID,
                "Metadata Service",
                definition,
                Set.of("https://w3id.org/fdp/fdp-o#MetadataService"),
                Collections.singletonList(KnownUUIDs.SCHEMA_DATASERVICE_UUID),
                MetadataSchemaType.INTERNAL
        );
    }

    @SneakyThrows
    public MetadataSchema catalogSchema() {
        String definition = loadClassResource("shape-catalog.ttl", getClass());
        return createSchemaFixture(
                KnownUUIDs.SCHEMA_CATALOG_UUID,
                "Catalog",
                definition,
                Set.of("http://www.w3.org/ns/dcat#Catalog"),
                Collections.singletonList(KnownUUIDs.SCHEMA_RESOURCE_UUID),
                MetadataSchemaType.INTERNAL
        );
    }

    @SneakyThrows
    public MetadataSchema datasetSchema() {
        String definition = loadClassResource("shape-dataset.ttl", getClass());
        return createSchemaFixture(
                KnownUUIDs.SCHEMA_DATASET_UUID,
                "Dataset",
                definition,
                Set.of("http://www.w3.org/ns/dcat#Dataset"),
                Collections.singletonList(KnownUUIDs.SCHEMA_RESOURCE_UUID),
                MetadataSchemaType.CUSTOM
        );
    }

    @SneakyThrows
    public MetadataSchema distributionSchema() {
        String definition = loadClassResource("shape-distribution.ttl", getClass());
        return createSchemaFixture(
                KnownUUIDs.SCHEMA_DISTRIBUTION_UUID,
                "Distribution",
                definition,
                Set.of("http://www.w3.org/ns/dcat#Distribution"),
                Collections.singletonList(KnownUUIDs.SCHEMA_RESOURCE_UUID),
                MetadataSchemaType.CUSTOM
        );
    }

    @SneakyThrows
    public MetadataSchema customSchema() {
        String definition = loadClassResource("shape-custom.ttl", getClass());
        return createSchemaFixture(
                "ceba9984-9838-4be2-a2a7-12213016fd96",
                "Custom Shape",
                definition,
                Set.of("http://example.org/Dog"),
                Collections.singletonList(KnownUUIDs.SCHEMA_RESOURCE_UUID),
                MetadataSchemaType.CUSTOM
        );
    }

    @SneakyThrows
    public MetadataSchema customSchemaEdited(){
        String definition = loadClassResource("shape-custom-edited.ttl", getClass());
        return createSchemaFixture(
                customSchema().getUuid(),
                customSchema().getName(),
                definition,
                Set.of("http://example.org/Dog"),
                Collections.singletonList(KnownUUIDs.SCHEMA_RESOURCE_UUID),
                customSchema().getType()
        );
    }

    @SneakyThrows
    public MetadataSchemaDraft customSchemaDraft1() {
        String definition = loadClassResource("shape-custom.ttl", getClass());
        return createSchemaDraftFixture(
                customSchema().getUuid(),
                customSchema().getName(),
                definition,
                Set.of("http://example.org/Dog")
        );
    }

    @SneakyThrows
    public MetadataSchemaDraft customSchemaDraft2() {
        String definition = loadClassResource("shape-custom.ttl", getClass());
        return createSchemaDraftFixture(
                "ceba9984-9838-4be2-a2a7-12213016fd97",
                "Custom Shape 2",
                definition,
                Set.of("http://example.org/Dog")
        );
    }

    @SneakyThrows
    public MetadataSchema customSchema_v1(boolean latest) {
        MetadataSchema schema = customSchema();
        schema.setName("Schema v1.0.0");
        schema.setVersionString("1.0.0");
        schema.setLatest(latest);
        return schema;
    }

    @SneakyThrows
    public MetadataSchema customSchema_v2(MetadataSchema previousVersion, boolean latest) {
        MetadataSchema schema = customSchema();
        schema.setName("Schema v2.0.0");
        schema.setVersionString("2.0.0");
        schema.setLatest(latest);
        schema.setPreviousVersion(previousVersion);
        return schema;
    }

    @SneakyThrows
    public MetadataSchema customSchema_v3(MetadataSchema previousVersion, boolean latest) {
        MetadataSchema schema = customSchema();
        schema.setName("Schema v2.1.0");
        schema.setVersionString("2.1.0");
        schema.setLatest(latest);
        schema.setPreviousVersion(previousVersion);
        return schema;
    }
}
