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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
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
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import nl.dtls.fairdatapoint.vocabulary.Sio;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

public class FactoryDefaults {

    //== USERS
    // Changes: Migration_0001_Init
    public static final User USER_ALBERT = User.builder()
            .uuid("7e64818d-6276-46fb-8bb1-732e6e09f7e9")
            .firstName("Albert")
            .lastName("Einstein")
            .email("albert.einstein@example.com")
            .passwordHash("$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW")
            .role(UserRole.ADMIN)
            .build();

    public static final User USER_NIKOLA = User.builder()
            .uuid("b5b92c69-5ed9-4054-954d-0121c29b6800")
            .firstName("Nikola")
            .lastName("Tesla")
            .email("nikola.tesla@example.com")
            .passwordHash("$2a$10$t2foZfp7cZFQo2u/33ZqTu2WNitBqYd2EY2tQO0/rBUdf8QfsAxyW")
            .role(UserRole.USER)
            .build();

    //== MEMBERSHIPS
    // Changes: Migration_0001_Init, Migration_0004_ResourceDefinition
    public static final Membership MEMBERSHIP_OWNER = Membership.builder()
            .uuid("49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8")
            .name("Owner")
            .permissions(List.of(
                    new MembershipPermission(2, 'W'),
                    new MembershipPermission(4, 'C'),
                    new MembershipPermission(8, 'D'),
                    new MembershipPermission(16, 'A')
            ))
            .allowedEntities(List.of(
                    "a0949e72-4466-4d53-8900-9436d1049a4b",
                    "2f08228e-1789-40f8-84cd-28e3288c3604",
                    "02c649de-c579-43bb-b470-306abdc808c7"
            ))
            .build();

    public static final Membership MEMBERSHIP_DATA_PROVIDER = Membership.builder()
            .uuid("87a2d984-7db2-43f6-805c-6b0040afead5")
            .name("Data Provider")
            .permissions(List.of(
                    new MembershipPermission(4, 'C')
            ))
            .allowedEntities(List.of(
                    "a0949e72-4466-4d53-8900-9436d1049a4b"
            ))
            .build();

    //== SHAPE UUIDS
    private static final String SHAPE_RESOURCE_UUID = "6a668323-3936-4b53-8380-a4fd2ed082ee";

    private static final String SHAPE_REPOSITORY_UUID = "a92958ab-a414-47e6-8e17-68ba96ba3a2b";

    private static final String SHAPE_CATALOG_UUID = "2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660";

    private static final String SHAPE_DATASET_UUID = "866d7fb8-5982-4215-9c7c-18d0ed1bd5f3";

    private static final String SHAPE_DISTRIBUTION_UUID = "ebacbf83-cd4f-4113-8738-d73c0735b0ab";

    //== RESOURCE DEFINITIONS
    // Changes: Migration_0002_CustomMetamodel, Migration_0004_ResourceDefinition
    public static final ResourceDefinition RESOURCE_DEFINITION_REPOSITORY = ResourceDefinition.builder()
            .uuid("77aaad6a-0136-4c6e-88b9-07ffccd0ee4c")
            .name("Repository")
            .urlPrefix("")
            .shapeUuids(List.of(
                    SHAPE_RESOURCE_UUID,
                    SHAPE_REPOSITORY_UUID
            ))
            .children(List.of(
                    ResourceDefinitionChild.builder()
                            .resourceDefinitionUuid("a0949e72-4466-4d53-8900-9436d1049a4b")
                            .relationUri("http://www.re3data.org/schema/3-0#dataCatalog")
                            .listView(
                                    ResourceDefinitionChildListView.builder()
                                            .title("Catalogs")
                                            .tagsUri("http://www.w3.org/ns/dcat#themeTaxonomy")
                                            .metadata(List.of())
                                            .build()
                            )
                            .build()
            ))
            .externalLinks(List.of())
            .build();

    public static final ResourceDefinition RESOURCE_DEFINITION_CATALOG = ResourceDefinition.builder()
            .uuid("a0949e72-4466-4d53-8900-9436d1049a4b")
            .name("Catalog")
            .urlPrefix("catalog")
            .shapeUuids(List.of(
                    SHAPE_RESOURCE_UUID,
                    SHAPE_CATALOG_UUID
            ))
            .children(List.of(
                    ResourceDefinitionChild.builder()
                            .resourceDefinitionUuid("2f08228e-1789-40f8-84cd-28e3288c3604")
                            .relationUri("http://www.w3.org/ns/dcat#dataset")
                            .listView(
                                    ResourceDefinitionChildListView.builder()
                                            .title("Datasets")
                                            .tagsUri("http://www.w3.org/ns/dcat#theme")
                                            .metadata(List.of())
                                            .build()
                            )
                            .build()
            ))
            .externalLinks(List.of())
            .build();

    public static final ResourceDefinition RESOURCE_DEFINITION_DATASET = ResourceDefinition.builder()
            .uuid("2f08228e-1789-40f8-84cd-28e3288c3604")
            .name("Dataset")
            .urlPrefix("dataset")
            .shapeUuids(List.of(
                    SHAPE_RESOURCE_UUID,
                    SHAPE_DATASET_UUID
            ))
            .children(List.of(
                    ResourceDefinitionChild.builder()
                            .resourceDefinitionUuid("02c649de-c579-43bb-b470-306abdc808c7")
                            .relationUri("http://www.w3.org/ns/dcat#distribution")
                            .listView(
                                    ResourceDefinitionChildListView.builder()
                                            .title("Distributions")
                                            .tagsUri(null)
                                            .metadata(List.of(
                                                    new ResourceDefinitionChildListViewMetadata(
                                                            "Media Type",
                                                            "http://www.w3.org/ns/dcat#mediaType"
                                                    )
                                            ))
                                            .build()
                            )
                            .build()
            ))
            .externalLinks(List.of())
            .build();

    public static ResourceDefinition RESOURCE_DEFINITION_DISTRIBUTION = ResourceDefinition.builder()
            .uuid("02c649de-c579-43bb-b470-306abdc808c7")
            .name("Distribution")
            .urlPrefix("distribution")
            .shapeUuids(List.of(
                    SHAPE_RESOURCE_UUID,
                    SHAPE_DISTRIBUTION_UUID
            ))
            .children(List.of())
            .externalLinks(List.of(
                    new ResourceDefinitionLink(
                            "Access online",
                            "http://www.w3.org/ns/dcat#accessURL"
                    ),
                    new ResourceDefinitionLink(
                            "Download",
                            "http://www.w3.org/ns/dcat#downloadURL"
                    )
            ))
            .build();

    //== SHAPES
    //== Changes: Migration_0003_ShapeDefinition, Migration_0005_UpdateShapeDefinition, Migration_0006_ShapesSharing
    public static Shape shapeResource() throws Exception {
        String definition = loadShape("shape-resource.ttl");
        return Shape.builder()
                .uuid(SHAPE_RESOURCE_UUID)
                .name("Resource")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeRepository() throws Exception {
        String definition = loadShape("shape-repository.ttl");
        return Shape.builder()
                .uuid(SHAPE_REPOSITORY_UUID)
                .name("Repository")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeCatalog() throws Exception {
        String definition = loadShape("shape-catalog.ttl");
        return Shape.builder()
                .uuid(SHAPE_CATALOG_UUID)
                .name("Catalog")
                .type(ShapeType.INTERNAL)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeDataset() throws Exception {
        String definition = loadShape("shape-dataset.ttl");
        return Shape.builder()
                .uuid(SHAPE_DATASET_UUID)
                .name("Dataset")
                .type(ShapeType.CUSTOM)
                .published(false)
                .definition(definition)
                .targetClasses(ShapeShaclUtils.extractTargetClasses(definition))
                .build();
    }

    public static Shape shapeDistribution() throws Exception {
        String definition = loadShape("shape-distribution.ttl");
        return Shape.builder()
                .uuid(SHAPE_DISTRIBUTION_UUID)
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

    public static Metadata metadataRepository(String persistentUrl) {
        return Metadata.builder()
                .uri(persistentUrl)
                .state(MetadataState.PUBLISHED)
                .build();
    }

    private static String loadShape(String name) throws Exception {
        return Resources.toString(
                FactoryDefaults.class.getResource(name),
                Charsets.UTF_8
        );
    }

    private static void add(List<Statement> statements, IRI predicate, org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(base, predicate, object, base));
    }

    private static void add(List<Statement> statements, IRI subject, IRI predicate, org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(subject, predicate, object, base));
    }
}
