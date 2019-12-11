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
package nl.dtls.fairdatapoint.database.rdf.migration.production;

import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtl.fairmetadata4j.utils.vocabulary.DATACITE;
import nl.dtl.fairmetadata4j.utils.vocabulary.FDP;
import nl.dtl.fairmetadata4j.utils.vocabulary.R3D;
import nl.dtl.fairmetadata4j.utils.vocabulary.Sio;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.rdf.migration.entity.RdfMigrationAnnotation;
import nl.dtls.rdf.migration.runner.RdfProductionMigration;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

@RdfMigrationAnnotation(
        number = 1,
        name = "Init migration",
        description = "Load basic fixtures for repository, catalog, dataset and distribution")
@Slf4j
@Service
public class Rdf_Migration_0001_Init implements RdfProductionMigration {

    private static final ValueFactory VF = SimpleValueFactory.getInstance();

    @Autowired
    protected Repository repository;

    @Value("${instance.url}")
    private String instanceUrl;

    @Value("${metadataProperties.rootSpecs:}")
    private String specs;

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Autowired
    private IRI license;

    @Autowired
    private IRI language;

    @Autowired
    private Agent publisher;

    @Autowired
    @Qualifier("metadataMetrics")
    private Map<String, String> metadataMetrics;

    @Autowired
    private PIDSystem pidSystem;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void runMigration() {
        createRepositoryInTripleStore();
        storePermissionForRepository();
    }

    private void createRepositoryInTripleStore() {
        try (RepositoryConnection conn = repository.getConnection()) {
            List<Statement> s = new ArrayList<>();
            add(s, RDF.TYPE, R3D.REPOSITORY);
            add(s, DCTERMS.TITLE, l("My FAIR Data Point"));
            add(s, RDFS.LABEL, l("My FAIR Data Point"));
            add(s, DCTERMS.HAS_VERSION, l(1.0f));
            add(s, FDP.METADATAISSUED, l(LocalDateTime.now()));
            add(s, FDP.METADATAMODIFIED, l(LocalDateTime.now()));
            add(s, DCTERMS.LICENSE, license);
            add(s, DCTERMS.DESCRIPTION, l("Duis pellentesque, nunc a fringilla varius, magna dui porta quam, nec " +
                    "ultricies augue turpis sed velit. Donec id consectetur ligula. Suspendisse pharetra egestas " +
                    "massa, vel varius leo viverra at. Donec scelerisque id ipsum id semper. Maecenas facilisis augue" +
                    " vel justo molestie aliquet. Maecenas sed mattis lacus, sed viverra risus. Donec iaculis quis " +
                    "lacus vitae scelerisque. Nullam fermentum lectus nisi, id vulputate nisi congue nec. Morbi " +
                    "fermentum justo at justo bibendum, at tempus ipsum tempor. Donec facilisis nibh sed lectus " +
                    "blandit venenatis. Cras ullamcorper, justo vitae feugiat commodo, orci metus suscipit purus, " +
                    "quis sagittis turpis ante eget ex. Pellentesque malesuada a metus eu pulvinar. Morbi rutrum " +
                    "euismod eros at varius. Duis finibus dapibus ex, a hendrerit mauris efficitur at."));
            add(s, DCTERMS.CONFORMS_TO, i(specs));
            add(s, DCTERMS.LANGUAGE, language);
            // Identifier
            IRI pidIri = i(instanceUrl + "#" + i(instanceUrl).getLocalName());
            add(s, FDP.METADATAIDENTIFIER, pidIri);
            add(s, pidIri, RDF.TYPE, DATACITE.RESOURCEIDENTIFIER);
            add(s, pidIri, DCTERMS.IDENTIFIER, VF.createLiteral(pidSystem.getId(pidIri), XMLSchema.STRING));
            // Access Rights
            IRI arIri = i(instanceUrl + "#accessRights");
            add(s, DCTERMS.ACCESS_RIGHTS, arIri);
            add(s, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT);
            add(s, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription));
            // Publisher
            add(s, DCTERMS.PUBLISHER, publisher.getUri());
            add(s, publisher.getUri(), RDF.TYPE, publisher.getType());
            add(s, publisher.getUri(), FOAF.NAME, publisher.getName());
            // Repository ID
            IRI repoIri = i(instanceUrl + "#repositoryID");
            add(s, R3D.REPOSITORYIDENTIFIER, repoIri);
            add(s, repoIri, RDF.TYPE, DATACITE.IDENTIFIER);
            add(s, repoIri, DCTERMS.IDENTIFIER, l(UUID.randomUUID().toString()));
            // Metrics
            metadataMetrics.forEach((metric, metricValue) -> {
                IRI metUri = i(format("%s/metrics/%s", instanceUrl, DigestUtils.md5Hex(metric)));
                add(s, Sio.REFERS_TO, metUri);
                add(s, metUri, Sio.IS_ABOUT, i(metric));
                add(s, metUri, Sio.REFERS_TO, i(metricValue));
            });
            conn.add(s);
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void storePermissionForRepository() {
        MongoCollection<Document> aclCol = mongoTemplate.getCollection("ACL");
        aclCol.insertOne(repositoryPermission());
    }

    private Document repositoryPermission() {
        String albertUuid = "7e64818d-6276-46fb-8bb1-732e6e09f7e9";
        BasicBSONObject owner = new BasicBSONObject().append("name", albertUuid).append("isPrincipal", true);
        Document acl = new Document();
        acl.append("className", "nl.dtl.fairmetadata4j.model.FDPMetadata");
        acl.append("instanceId", instanceUrl);
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

    private void add(List<Statement> statements, IRI predicate, org.eclipse.rdf4j.model.Value object) {
        statements.add(VF.createStatement(i(instanceUrl), predicate, object, i(instanceUrl)));
    }

    private void add(List<Statement> statements, IRI subject, IRI predicate, org.eclipse.rdf4j.model.Value object) {
        statements.add(VF.createStatement(subject, predicate, object, i(instanceUrl)));
    }

    private IRI i(String iri) {
        return VF.createIRI(iri);
    }

    private Literal l(String literal) {
        return VF.createLiteral(literal);
    }

    private Literal l(float literal) {
        return VF.createLiteral(literal);
    }

    private Literal l(LocalDateTime literal) {
        return VF.createLiteral(literal.toString(), XMLSchema.DATETIME);
    }

}
