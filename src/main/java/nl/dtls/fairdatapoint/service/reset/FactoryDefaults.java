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
package nl.dtls.fairdatapoint.service.reset;

import nl.dtls.fairdatapoint.entity.membership.Membership;
import nl.dtls.fairdatapoint.entity.membership.MembershipPermission;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import nl.dtls.fairdatapoint.entity.resource.*;
import nl.dtls.fairdatapoint.entity.shape.Shape;
import nl.dtls.fairdatapoint.entity.shape.ShapeType;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import nl.dtls.fairdatapoint.service.shape.ShapeShaclUtils;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static nl.dtls.fairdatapoint.util.ResourceReader.loadClassResource;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

public class FactoryDefaults {

    //== USERS
    // Changes: Migration_0001_Init
    public static final User USER_ALBERT = User.builder()
            .uuid(KnownUUIDs.USER_ALBERT_UUID)
            .firstName("Albert")
            .lastName("Einstein")
            .email("albert.einstein@example.com")
            .passwordHash("$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW")
            .role(UserRole.ADMIN)
            .build();

    public static final User USER_NIKOLA = User.builder()
            .uuid(KnownUUIDs.USER_NIKOLA_UUID)
            .firstName("Nikola")
            .lastName("Tesla")
            .email("nikola.tesla@example.com")
            .passwordHash("$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW")
            .role(UserRole.USER)
            .build();

    //== MEMBERSHIPS
    // Changes: Migration_0001_Init, Migration_0004_ResourceDefinition
    public static final Membership MEMBERSHIP_OWNER = Membership.builder()
            .uuid(KnownUUIDs.MEMBERSHIP_OWNER_UUID)
            .name("Owner")
            .permissions(List.of(
                    new MembershipPermission(2, 'W'),
                    new MembershipPermission(4, 'C'),
                    new MembershipPermission(8, 'D'),
                    new MembershipPermission(16, 'A')
            ))
            .allowedEntities(List.of(
                    KnownUUIDs.RD_CATALOG_UUID,
                    KnownUUIDs.RD_DATASET_UUID,
                    KnownUUIDs.RD_DISTRIBUTION_UUID
            ))
            .build();

    public static final Membership MEMBERSHIP_DATA_PROVIDER = Membership.builder()
            .uuid(KnownUUIDs.MEMBERSHIP_DATAPROVIDER_UUID)
            .name("Data Provider")
            .permissions(List.of(
                    new MembershipPermission(4, 'C')
            ))
            .allowedEntities(List.of(
                    KnownUUIDs.RD_CATALOG_UUID
            ))
            .build();

    //== RESOURCE DEFINITIONS
    // Changes: Migration_0002_CustomMetamodel, Migration_0004_ResourceDefinition, Migration_0010_ComplyFDPO
    public static final ResourceDefinition RESOURCE_DEFINITION_FDP = ResourceDefinition.builder()
            .uuid(KnownUUIDs.RD_FDP_UUID)
            .name("FAIR Data Point")
            .urlPrefix("")
            .shapeUuids(List.of(
                    KnownUUIDs.SHAPE_RESOURCE_UUID,
                    KnownUUIDs.SHAPE_DATASERVICE_UUID,
                    KnownUUIDs.SHAPE_METADATASERVICE_UUID,
                    KnownUUIDs.SHAPE_FDP_UUID
            ))
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
            .name("Catalog")
            .urlPrefix("catalog")
            .shapeUuids(List.of(
                    KnownUUIDs.SHAPE_RESOURCE_UUID,
                    KnownUUIDs.SHAPE_CATALOG_UUID
            ))
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
            .name("Dataset")
            .urlPrefix("dataset")
            .shapeUuids(List.of(
                    KnownUUIDs.SHAPE_RESOURCE_UUID,
                    KnownUUIDs.SHAPE_DATASET_UUID
            ))
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

    public static ResourceDefinition RESOURCE_DEFINITION_DISTRIBUTION = ResourceDefinition.builder()
            .uuid(KnownUUIDs.RD_DISTRIBUTION_UUID)
            .name("Distribution")
            .urlPrefix("distribution")
            .shapeUuids(List.of(
                    KnownUUIDs.SHAPE_RESOURCE_UUID,
                    KnownUUIDs.SHAPE_DISTRIBUTION_UUID
            ))
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

    //== SHAPES
    //== Changes: Migration_0003_ShapeDefinition, Migration_0005_UpdateShapeDefinition, Migration_0006_ShapesSharing, Migration_0010_ComplyFDPO
    public static Shape shapeResource() throws Exception {
        String definition = loadClassResource("shape-resource.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_RESOURCE_UUID)
                .name("Resource")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeFDP() throws Exception {
        String definition = loadClassResource("shape-fdp.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_FDP_UUID)
                .name("FAIR Data Point")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeDataService() throws Exception {
        String definition = loadClassResource("shape-data-service.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_DATASERVICE_UUID)
                .name("Data Service")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeMetadataService() throws Exception {
        String definition = loadClassResource("shape-metadata-service.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_METADATASERVICE_UUID)
                .name("Metadata Service")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeCatalog() throws Exception {
        String definition = loadClassResource("shape-catalog.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_CATALOG_UUID)
                .name("Catalog")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeDataset() throws Exception {
        String definition = loadClassResource("shape-dataset.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_DATASET_UUID)
                .name("Dataset")
                .type(ShapeType.CUSTOM)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeDistribution() throws Exception {
        String definition = loadClassResource("shape-distribution.ttl", FactoryDefaults.class);
        return Shape.builder()
                .uuid(KnownUUIDs.SHAPE_DISTRIBUTION_UUID)
                .name("Distribution")
                .type(ShapeType.CUSTOM)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    // Repository ACL
    public static Document aclRepository(String persistentUrl) {
        BasicBSONObject owner = new BasicBSONObject()
                .append("name", USER_ALBERT.getUuid())
                .append("isPrincipal", true);
        Document acl = new Document();
        acl.append("className", "nl.dtls.fairdatapoint.entity.metadata.FDPMetadata");
        acl.append("instanceId", persistentUrl);
        acl.append("owner", owner);
        acl.append("inheritPermissions", true);
        BasicBSONList permissions = new BasicBSONList();
        permissions.add(
                new Document()
                        .append("sid", owner)
                        .append("permission", 2)
                        .append("granting", true)
                        .append("auditFailure", false)
                        .append("auditSuccess", false));
        permissions.add(
                new Document()
                        .append("sid", owner)
                        .append("permission", 4)
                        .append("granting", true)
                        .append("auditFailure", false)
                        .append("auditSuccess", false));
        permissions.add(
                new Document()
                        .append("sid", owner)
                        .append("permission", 8)
                        .append("granting", true)
                        .append("auditFailure", false)
                        .append("auditSuccess", false));
        permissions.add(
                new Document()
                        .append("sid", owner)
                        .append("permission", 16)
                        .append("granting", true)
                        .append("auditFailure", false)
                        .append("auditSuccess", false));
        acl.append("permissions", permissions);
        acl.append("_class", "org.springframework.security.acls.domain.MongoAcl");
        return acl;
    }

    // Repository RDF statements
    private static final String LIPSUM_TEXT = "Duis pellentesque, nunc a fringilla varius, magna dui porta quam, nec " +
            "ultricies augue turpis sed velit. Donec id consectetur ligula. Suspendisse pharetra egestas " +
            "massa, vel varius leo viverra at. Donec scelerisque id ipsum id semper. Maecenas facilisis augue" +
            " vel justo molestie aliquet. Maecenas sed mattis lacus, sed viverra risus. Donec iaculis quis " +
            "lacus vitae scelerisque. Nullam fermentum lectus nisi, id vulputate nisi congue nec. Morbi " +
            "fermentum justo at justo bibendum, at tempus ipsum tempor. Donec facilisis nibh sed lectus " +
            "blandit venenatis. Cras ullamcorper, justo vitae feugiat commodo, orci metus suscipit purus, " +
            "quis sagittis turpis ante eget ex. Pellentesque malesuada a metus eu pulvinar. Morbi rutrum " +
            "euismod eros at varius. Duis finibus dapibus ex, a hendrerit mauris efficitur at.";

    public static List<Statement> repositoryStatements(String persistentUrl, IRI license, IRI language, String accessRightsDescription) {
        List<Statement> s = new ArrayList<>();
        IRI baseUrl = i(persistentUrl);
        FactoryDefaults.add(s, RDF.TYPE, R3D.REPOSITORY, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, i("http://www.w3.org/ns/dcat#Resource"), baseUrl);
        FactoryDefaults.add(s, DCTERMS.TITLE, l("My FAIR Data Point"), baseUrl);
        FactoryDefaults.add(s, RDFS.LABEL, l("My FAIR Data Point"), baseUrl);
        FactoryDefaults.add(s, DCTERMS.HAS_VERSION, l(1.0f), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAISSUED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAMODIFIED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LICENSE, license, baseUrl);
        FactoryDefaults.add(s, DCTERMS.DESCRIPTION, l(LIPSUM_TEXT), baseUrl);
        FactoryDefaults.add(s, DCTERMS.CONFORMS_TO, i("https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata"), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LANGUAGE, language, baseUrl);
        // Identifier
        IRI identifierIri = i(persistentUrl + "#identifier");
        FactoryDefaults.add(s, FDP.METADATAIDENTIFIER, identifierIri, baseUrl);
        FactoryDefaults.add(s, identifierIri, RDF.TYPE, DATACITE.IDENTIFIER, baseUrl);
        FactoryDefaults.add(s, identifierIri, DCTERMS.IDENTIFIER, l(persistentUrl), baseUrl);
        // Repository Identifier
        FactoryDefaults.add(s, R3D.REPOSITORYIDENTIFIER, identifierIri, baseUrl);
        // Access Rights
        IRI arIri = i(persistentUrl + "#accessRights");
        FactoryDefaults.add(s, DCTERMS.ACCESS_RIGHTS, arIri, baseUrl);
        FactoryDefaults.add(s, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT, baseUrl);
        FactoryDefaults.add(s, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription), baseUrl);
        // Publisher
        IRI publisherIri = i(persistentUrl + "#publisher");
        FactoryDefaults.add(s, DCTERMS.PUBLISHER, publisherIri, baseUrl);
        FactoryDefaults.add(s, publisherIri, RDF.TYPE, FOAF.AGENT, baseUrl);
        FactoryDefaults.add(s, publisherIri, FOAF.NAME, l("Default Publisher"), baseUrl);
        return s;
    }

    public static List<Statement> fdpStatements(String persistentUrl, IRI license, IRI language, String accessRightsDescription) {
        List<Statement> s = new ArrayList<>();
        IRI baseUrl = i(persistentUrl);
        FactoryDefaults.add(s, RDF.TYPE, FDP.FAIRDATAPOINT, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, FDP.METADATASERVICE, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, DCAT.DATA_SERVICE, baseUrl);
        FactoryDefaults.add(s, RDF.TYPE, DCAT.RESOURCE, baseUrl);
        FactoryDefaults.add(s, DCTERMS.TITLE, l("My FAIR Data Point"), baseUrl);
        FactoryDefaults.add(s, RDFS.LABEL, l("My FAIR Data Point"), baseUrl);
        FactoryDefaults.add(s, DCTERMS.HAS_VERSION, l(1.0f), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAISSUED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, FDP.METADATAMODIFIED, l(OffsetDateTime.now()), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LICENSE, license, baseUrl);
        FactoryDefaults.add(s, DCTERMS.DESCRIPTION, l(LIPSUM_TEXT), baseUrl);
        FactoryDefaults.add(s, DCTERMS.LANGUAGE, language, baseUrl);
        // Identifier
        IRI identifierIri = i(persistentUrl + "#identifier");
        FactoryDefaults.add(s, FDP.METADATAIDENTIFIER, identifierIri, baseUrl);
        FactoryDefaults.add(s, identifierIri, RDF.TYPE, DATACITE.IDENTIFIER, baseUrl);
        FactoryDefaults.add(s, identifierIri, DCTERMS.IDENTIFIER, l(persistentUrl), baseUrl);
        // Access Rights
        IRI arIri = i(persistentUrl + "#accessRights");
        FactoryDefaults.add(s, DCTERMS.ACCESS_RIGHTS, arIri, baseUrl);
        FactoryDefaults.add(s, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT, baseUrl);
        FactoryDefaults.add(s, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription), baseUrl);
        // Publisher
        IRI publisherIri = i(persistentUrl + "#publisher");
        FactoryDefaults.add(s, DCTERMS.PUBLISHER, publisherIri, baseUrl);
        FactoryDefaults.add(s, publisherIri, RDF.TYPE, FOAF.AGENT, baseUrl);
        FactoryDefaults.add(s, publisherIri, FOAF.NAME, l("Default Publisher"), baseUrl);
        return s;
    }

    public static Metadata metadataRepository(String persistentUrl) {
        return Metadata.builder()
                .uri(persistentUrl)
                .state(MetadataState.PUBLISHED)
                .build();
    }

    private static void add(List<Statement> statements, IRI predicate, org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(base, predicate, object, base));
    }

    private static void add(List<Statement> statements, IRI subject, IRI predicate, org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(subject, predicate, object, base));
    }
}
