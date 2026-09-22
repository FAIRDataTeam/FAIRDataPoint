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
package org.fairdatateam.fairdatapoint.reset;

import org.fairdatateam.fairdatapoint.security.membership.Membership;
import org.fairdatateam.fairdatapoint.security.membership.MembershipPermission;
import org.fairdatateam.fairdatapoint.rdf.metadata.Metadata;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataState;
import org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchema;
import org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchemaType;
import org.fairdatateam.fairdatapoint.common.util.SemVer;
import org.fairdatateam.fairdatapoint.resource.*;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchemaShaclUtils;
import org.fairdatateam.fairdatapoint.common.util.KnownUUIDs;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.DATACITE;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.DCAT3;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDP;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.R3D;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.fairdatateam.fairdatapoint.common.util.SpringResourceReader.loadClassResource;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.*;

public class FactoryDefaults {

    public static final String PASSWORD_HASH =
            "$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW";
    public static final String LIPSUM_TEXT = "Duis pellentesque, nunc a fringilla varius, magna dui porta quam, nec "
            + "ultricies augue turpis sed velit. Donec id consectetur ligula. Suspendisse pharetra egestas "
            + "massa, vel varius leo viverra at. Donec scelerisque id ipsum id semper. Maecenas facilisis augue"
            + " vel justo molestie aliquet. Maecenas sed mattis lacus, sed viverra risus. Donec iaculis quis "
            + "lacus vitae scelerisque. Nullam fermentum lectus nisi, id vulputate nisi congue nec. Morbi "
            + "fermentum justo at justo bibendum, at tempus ipsum tempor. Donec facilisis nibh sed lectus "
            + "blandit venenatis. Cras ullamcorper, justo vitae feugiat commodo, orci metus suscipit purus, "
            + "quis sagittis turpis ante eget ex. Pellentesque malesuada a metus eu pulvinar. Morbi rutrum "
            + "euismod eros at varius. Duis finibus dapibus ex, a hendrerit mauris efficitur at.";
    public static final String DEFAULT_FDP_TITLE = "My FAIR Data Point";
    public static final String DEFAULT_PUBLISHER = "Default Publisher";
    public static final String SUFFIX_IDENTIFIER = "#identifier";
    public static final String SUFFIX_ACCESS_RIGHTS = "#accessRights";
    public static final String SUFFIX_PUBLISHER = "#publisher";
    public static final String FDP_APP_URL = "https://purl.org/fairdatapoint/app";

    public static final SemVer SEMVER_V1 = new SemVer("1.0.0");

    // == USERS
    // Changes: Migration_0001_Init
    public static final User USER_ALBERT = User.builder()
            .uuid(UUID.fromString(KnownUUIDs.USER_ALBERT_UUID))
            .firstName("Albert")
            .lastName("Einstein")
            .email("albert.einstein@example.com")
            .passwordHash(PASSWORD_HASH)
            .role(UserRole.ADMIN)
            .build();

    public static final User USER_NIKOLA = User.builder()
            .uuid(UUID.fromString(KnownUUIDs.USER_NIKOLA_UUID))
            .firstName("Nikola")
            .lastName("Tesla")
            .email("nikola.tesla@example.com")
            .passwordHash(PASSWORD_HASH)
            .role(UserRole.USER)
            .build();

    // == MEMBERSHIPS
    // Changes: Migration_0001_Init, Migration_0004_ResourceDefinition
    public static final int MASK_W = 2;
    public static final int MASK_C = 4;
    public static final int MASK_D = 8;
    public static final int MASK_A = 16;

    // == RESOURCE DEFINITIONS
    // Changes: Migration_0002_CustomMetamodel, Migration_0004_ResourceDefinition, Migration_0010_ComplyFDPO
    public static final String FDP_TITLE = "FAIR Data Point";
    public static final String FDP_PREFIX = "";
    public static final String CATALOG_TITLE = "Catalog";
    public static final String CATALOG_PREFIX = "catalog";
    public static final String DATASET_TITLE = "Dataset";
    public static final String DATASET_PREFIX = "dataset";
    public static final String DISTRIBUTION_TITLE = "Distribution";
    public static final String DISTRIBUTION_PREFIX = "distribution";
    public static final String DATASERVICE_TITLE = "Data Service";
    public static final String DATASERVICE_PREFIX = "data-service";
    public static final String METADATASERVICE_TITLE = "Metadata Service";
    public static final String METADATASERVICE_PREFIX = "metadata-service";

    public static final ResourceDefinition RESOURCE_DEFINITION_FDP = ResourceDefinition.builder()
            .uuid(KnownUUIDs.RD_FDP_UUID)
            .name(FDP_TITLE)
            .urlPrefix(FDP_PREFIX)
            .metadataSchemaUuids(List.of(KnownUUIDs.SCHEMA_FDP_UUID))
            .children(List.of(
                    ResourceDefinitionChild.builder()
                            .resourceDefinitionUuid(KnownUUIDs.RD_CATALOG_UUID)
                            .relationUri(FDP.METADATACATALOG.stringValue())
                            .listView(
                                    ResourceDefinitionChildListView.builder()
                                            .title("Catalogs")
                                            .tagsUri(DCAT.THEME_TAXONOMY.stringValue())
                                            .metadata(List.of())
                                            .build()
                            )
                            .build()
            ))
            .externalLinks(List.of())
            .build();

    public static final ResourceDefinition RESOURCE_DEFINITION_CATALOG = ResourceDefinition.builder()
            .uuid(KnownUUIDs.RD_CATALOG_UUID)
            .name(CATALOG_TITLE)
            .urlPrefix(CATALOG_PREFIX)
            .metadataSchemaUuids(List.of(KnownUUIDs.SCHEMA_CATALOG_UUID))
            .children(List.of(
                    ResourceDefinitionChild.builder()
                            .resourceDefinitionUuid(KnownUUIDs.RD_DATASET_UUID)
                            .relationUri(DCAT.HAS_DATASET.stringValue())
                            .listView(
                                    ResourceDefinitionChildListView.builder()
                                            .title("Datasets")
                                            .tagsUri(DCAT.THEME.stringValue())
                                            .metadata(List.of())
                                            .build()
                            )
                            .build()
            ))
            .externalLinks(List.of())
            .build();

    public static final ResourceDefinition RESOURCE_DEFINITION_DATASET = ResourceDefinition.builder()
            .uuid(KnownUUIDs.RD_DATASET_UUID)
            .name(DATASET_TITLE)
            .urlPrefix(DATASET_PREFIX)
            .metadataSchemaUuids(List.of(KnownUUIDs.SCHEMA_DATASET_UUID))
            .children(List.of(
                    ResourceDefinitionChild.builder()
                            .resourceDefinitionUuid(KnownUUIDs.RD_DISTRIBUTION_UUID)
                            .relationUri(DCAT.HAS_DISTRIBUTION.stringValue())
                            .listView(
                                    ResourceDefinitionChildListView.builder()
                                            .title("Distributions")
                                            .tagsUri(null)
                                            .metadata(List.of(
                                                    new ResourceDefinitionChildListViewMetadata(
                                                            "Media Type",
                                                            DCAT.MEDIA_TYPE.stringValue()
                                                    )
                                            ))
                                            .build()
                            )
                            .build()
            ))
            .externalLinks(List.of())
            .build();

    public static final ResourceDefinition RESOURCE_DEFINITION_DISTRIBUTION = ResourceDefinition.builder()
            .uuid(KnownUUIDs.RD_DISTRIBUTION_UUID)
            .name(DISTRIBUTION_TITLE)
            .urlPrefix(DISTRIBUTION_PREFIX)
            .metadataSchemaUuids(List.of(KnownUUIDs.SCHEMA_DISTRIBUTION_UUID))
            .children(List.of())
            .externalLinks(List.of(
                    new ResourceDefinitionLink(
                            "Access online",
                            DCAT.ACCESS_URL.stringValue()
                    ),
                    new ResourceDefinitionLink(
                            "Download",
                            DCAT.DOWNLOAD_URL.stringValue()
                    )
            ))
            .build();

    // == SHAPES
    // == Changes: Migration_0003_ShapeDefinition, Migration_0005_UpdateShapeDefinition,
    //             Migration_0006_ShapesSharing, Migration_0010_ComplyFDPO
    public static MetadataSchema schemaResource() throws Exception {
        final String definition = loadClassResource("shape-resource.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_RESOURCE_UUID)
                .name("Resource")
                .type(MetadataSchemaType.INTERNAL)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_RESOURCE_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(Collections.emptyList())
                .abstractSchema(true)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static MetadataSchema schemaFDP() throws Exception {
        final String definition = loadClassResource("shape-fdp.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_FDP_UUID)
                .name(FDP_TITLE)
                .type(MetadataSchemaType.INTERNAL)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_FDP_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(List.of(KnownUUIDs.SCHEMA_METADATASERVICE_UUID))
                .abstractSchema(false)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .suggestedResourceName(FDP_TITLE)
                .suggestedUrlPrefix(FDP_PREFIX)
                .build();
    }

    public static MetadataSchema schemaDataService() throws Exception {
        final String definition = loadClassResource("shape-data-service.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_DATASERVICE_UUID)
                .name(DATASERVICE_TITLE)
                .type(MetadataSchemaType.INTERNAL)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_DATASERVICE_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(List.of(KnownUUIDs.SCHEMA_RESOURCE_UUID))
                .abstractSchema(false)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .suggestedResourceName(DATASERVICE_TITLE)
                .suggestedUrlPrefix(DATASERVICE_PREFIX)
                .build();
    }

    public static MetadataSchema schemaMetadataService() throws Exception {
        final String definition = loadClassResource("shape-metadata-service.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_METADATASERVICE_UUID)
                .name(METADATASERVICE_TITLE)
                .type(MetadataSchemaType.INTERNAL)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_METADATASERVICE_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(List.of(KnownUUIDs.SCHEMA_DATASERVICE_UUID))
                .abstractSchema(false)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .suggestedResourceName(METADATASERVICE_TITLE)
                .suggestedUrlPrefix(METADATASERVICE_PREFIX)
                .build();
    }

    public static MetadataSchema schemaCatalog() throws Exception {
        final String definition = loadClassResource("shape-catalog.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_CATALOG_UUID)
                .name(CATALOG_TITLE)
                .type(MetadataSchemaType.INTERNAL)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_CATALOG_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(List.of(KnownUUIDs.SCHEMA_RESOURCE_UUID))
                .abstractSchema(false)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .suggestedResourceName(CATALOG_TITLE)
                .suggestedUrlPrefix(CATALOG_PREFIX)
                .build();
    }

    public static MetadataSchema schemaDataset() throws Exception {
        final String definition = loadClassResource("shape-dataset.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_DATASET_UUID)
                .name(DATASET_TITLE)
                .type(MetadataSchemaType.CUSTOM)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_DATASET_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(List.of(KnownUUIDs.SCHEMA_RESOURCE_UUID))
                .abstractSchema(false)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .suggestedResourceName(DATASET_TITLE)
                .suggestedUrlPrefix(DATASET_PREFIX)
                .build();
    }

    public static MetadataSchema schemaDistribution() throws Exception {
        final String definition = loadClassResource("shape-distribution.ttl", FactoryDefaults.class);
        return MetadataSchema.builder()
                .uuid(KnownUUIDs.SCHEMA_DISTRIBUTION_UUID)
                .name(DISTRIBUTION_TITLE)
                .type(MetadataSchemaType.CUSTOM)
                .published(false)
                .latest(true)
                .version(SEMVER_V1)
                .versionString(SEMVER_V1.toString())
                .versionUuid(KnownUUIDs.SCHEMA_V1_DISTRIBUTION_UUID)
                .previousVersionUuid(null)
                .origin(FDP_APP_URL)
                .importedFrom(FDP_APP_URL)
                .extendSchemas(List.of(KnownUUIDs.SCHEMA_RESOURCE_UUID))
                .abstractSchema(false)
                .definition(definition)
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(definition))
                .suggestedResourceName(DISTRIBUTION_TITLE)
                .suggestedUrlPrefix(DISTRIBUTION_PREFIX)
                .build();
    }

    // Repository RDF statements

    public static List<Statement> repositoryStatements(String persistentUrl, IRI license,
                                                       IRI language, String accessRightsDescription) {
        final List<Statement> s = new ArrayList<>();
        final IRI baseUrl = i(persistentUrl);
        FactoryDefaults.add(s, RDF.TYPE, R3D.REPOSITORY, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, i("http://www.w3.org/ns/dcat#Resource"), baseUrl);
        FactoryDefaults.add(s, DCTERMS.TITLE, l(DEFAULT_FDP_TITLE), baseUrl);
        FactoryDefaults.add(s, RDFS.LABEL, l(DEFAULT_FDP_TITLE), baseUrl);
        FactoryDefaults.add(s, DCAT3.VERSION, l(1.0f), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAISSUED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAMODIFIED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LICENSE, license, baseUrl);
        FactoryDefaults.add(s, DCTERMS.DESCRIPTION, l(LIPSUM_TEXT), baseUrl);
        FactoryDefaults.add(s, DCTERMS.CONFORMS_TO,
                i("https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata"), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LANGUAGE, language, baseUrl);
        // Identifier
        final IRI identifierIri = i(persistentUrl + SUFFIX_IDENTIFIER);
        FactoryDefaults.add(s, FDP.METADATAIDENTIFIER, identifierIri, baseUrl);
        FactoryDefaults.add(s, identifierIri, RDF.TYPE, DATACITE.IDENTIFIER, baseUrl);
        FactoryDefaults.add(s, identifierIri, DCTERMS.IDENTIFIER, l(persistentUrl), baseUrl);
        // Repository Identifier
        FactoryDefaults.add(s, R3D.REPOSITORYIDENTIFIER, identifierIri, baseUrl);
        // Access Rights
        final IRI arIri = i(persistentUrl + SUFFIX_ACCESS_RIGHTS);
        FactoryDefaults.add(s, DCTERMS.ACCESS_RIGHTS, arIri, baseUrl);
        FactoryDefaults.add(s, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT, baseUrl);
        FactoryDefaults.add(s, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription), baseUrl);
        // Publisher
        final IRI publisherIri = i(persistentUrl + SUFFIX_PUBLISHER);
        FactoryDefaults.add(s, DCTERMS.PUBLISHER, publisherIri, baseUrl);
        FactoryDefaults.add(s, publisherIri, RDF.TYPE, FOAF.AGENT, baseUrl);
        FactoryDefaults.add(s, publisherIri, FOAF.NAME, l(DEFAULT_PUBLISHER), baseUrl);
        return s;
    }

    public static List<Statement> fdpStatements(String persistentUrl, IRI license,
                                                IRI language, String accessRightsDescription) {
        final List<Statement> s = new ArrayList<>();
        final IRI baseUrl = i(persistentUrl);
        FactoryDefaults.add(s, RDF.TYPE, FDP.FAIRDATAPOINT, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, FDP.METADATASERVICE, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, DCAT.DATA_SERVICE, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, DCAT.RESOURCE, baseUrl);
        FactoryDefaults.add(s, DCTERMS.TITLE, l(DEFAULT_FDP_TITLE), baseUrl);
        FactoryDefaults.add(s, RDFS.LABEL, l(DEFAULT_FDP_TITLE), baseUrl);
        FactoryDefaults.add(s, DCAT3.VERSION, l(1.0f), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAISSUED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAMODIFIED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LICENSE, license, baseUrl);
        FactoryDefaults.add(s, DCTERMS.DESCRIPTION, l(LIPSUM_TEXT), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LANGUAGE, language, baseUrl);
        // Identifier
        final IRI identifierIri = i(persistentUrl + SUFFIX_IDENTIFIER);
        FactoryDefaults.add(s, FDP.METADATAIDENTIFIER, identifierIri, baseUrl);
        FactoryDefaults.add(s, identifierIri, RDF.TYPE, DATACITE.IDENTIFIER, baseUrl);
        FactoryDefaults.add(s, identifierIri, DCTERMS.IDENTIFIER, l(persistentUrl), baseUrl);
        // Access Rights
        final IRI arIri = i(persistentUrl + SUFFIX_ACCESS_RIGHTS);
        FactoryDefaults.add(s, DCTERMS.ACCESS_RIGHTS, arIri, baseUrl);
        FactoryDefaults.add(s, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT, baseUrl);
        FactoryDefaults.add(s, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription), baseUrl);
        // Publisher
        final IRI publisherIri = i(persistentUrl + SUFFIX_PUBLISHER);
        FactoryDefaults.add(s, DCTERMS.PUBLISHER, publisherIri, baseUrl);
        FactoryDefaults.add(s, publisherIri, RDF.TYPE, FOAF.AGENT, baseUrl);
        FactoryDefaults.add(s, publisherIri, FOAF.NAME, l(DEFAULT_PUBLISHER), baseUrl);
        return s;
    }

    public static Metadata metadataRepository(String persistentUrl) {
        return Metadata.builder()
                .uri(persistentUrl)
                .state(MetadataState.PUBLISHED)
                .build();
    }

    // Methods rather than constants: a Membership carries MembershipPermission instances that are
    // each attached to exactly one membership (see Membership.addPermission), so a single shared
    // instance could not be saved more than once. Every call builds fresh entities instead, with
    // permissions attached through Membership.addPermission so that the back-reference the
    // membership_permission foreign key needs is set on both sides.
    public static Membership membershipOwner() {
        return membership(
                KnownUUIDs.MEMBERSHIP_OWNER_UUID,
                "Owner",
                List.of(
                        KnownUUIDs.RD_CATALOG_UUID,
                        KnownUUIDs.RD_DATASET_UUID,
                        KnownUUIDs.RD_DISTRIBUTION_UUID
                ),
                new MembershipPermission(MASK_W, 'W'),
                new MembershipPermission(MASK_C, 'C'),
                new MembershipPermission(MASK_D, 'D'),
                new MembershipPermission(MASK_A, 'A')
        );
    }

    public static Membership membershipDataProvider() {
        return membership(
                KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID,
                "Data Provider",
                List.of(KnownUUIDs.RD_CATALOG_UUID),
                new MembershipPermission(MASK_C, 'C')
        );
    }

    private static Membership membership(final String uuid, final String name,
                                         final List<String> allowedEntities,
                                         final MembershipPermission... permissions) {
        final Membership membership = Membership.builder()
                .uuid(UUID.fromString(uuid))
                .name(name)
                .allowedEntities(new LinkedHashSet<>(allowedEntities))
                .build();
        for (final MembershipPermission permission : permissions) {
            membership.addPermission(permission);
        }
        return membership;
    }

    private static void add(List<Statement> statements, IRI predicate,
                            org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(base, predicate, object, base));
    }

    private static void add(List<Statement> statements, IRI subject, IRI predicate,
                            org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(subject, predicate, object, base));
    }
}
