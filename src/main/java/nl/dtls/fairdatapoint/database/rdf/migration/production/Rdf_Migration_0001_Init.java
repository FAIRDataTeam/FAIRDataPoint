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
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import nl.dtls.fairdatapoint.vocabulary.Sio;
import nl.dtls.rdf.migration.entity.RdfMigrationAnnotation;
import nl.dtls.rdf.migration.runner.RdfProductionMigration;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

@RdfMigrationAnnotation(
        number = 1,
        name = "Init migration",
        description = "Load basic fixtures for repository, catalog, dataset and distribution")
@Slf4j
@Service
public class Rdf_Migration_0001_Init implements RdfProductionMigration {

    @Autowired
    protected Repository repository;

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Autowired
    private IRI license;

    @Autowired
    private IRI language;

    @Autowired
    @Qualifier("metadataMetrics")
    private Map<String, String> metadataMetrics;

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
            add(s, RDF.TYPE, i("http://www.w3.org/ns/dcat#Resource"));
            add(s, DCTERMS.TITLE, l("My FAIR Data Point"));
            add(s, RDFS.LABEL, l("My FAIR Data Point"));
            add(s, DCTERMS.HAS_VERSION, l(1.0f));
            add(s, FDP.METADATAISSUED, l(OffsetDateTime.now()));
            add(s, FDP.METADATAMODIFIED, l(OffsetDateTime.now()));
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
            add(s, DCTERMS.CONFORMS_TO, i("https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata"));
            add(s, DCTERMS.LANGUAGE, language);
            // Identifier
            IRI identifierIri = i(persistentUrl + "#identifier");
            add(s, FDP.METADATAIDENTIFIER, identifierIri);
            add(s, identifierIri, RDF.TYPE, DATACITE.IDENTIFIER);
            add(s, identifierIri, DCTERMS.IDENTIFIER, l(persistentUrl));
            // Repository Identifier
            add(s, R3D.REPOSITORYIDENTIFIER, identifierIri);
            // Access Rights
            IRI arIri = i(persistentUrl + "#accessRights");
            add(s, DCTERMS.ACCESS_RIGHTS, arIri);
            add(s, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT);
            add(s, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription));
            // Publisher
            IRI publisherIri = i(persistentUrl + "#publisher");
            add(s, DCTERMS.PUBLISHER, publisherIri);
            add(s, publisherIri, RDF.TYPE, FOAF.AGENT);
            add(s, publisherIri, FOAF.NAME, l("Default Publisher"));
            // Metrics
            metadataMetrics.forEach((metric, metricValue) -> {
                IRI metUri = i(format("%s/metrics/%s", persistentUrl, DigestUtils.md5DigestAsHex(metric.getBytes())));
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

    private void add(List<Statement> statements, IRI predicate, org.eclipse.rdf4j.model.Value object) {
        statements.add(s(i(persistentUrl), predicate, object, i(persistentUrl)));
    }

    private void add(List<Statement> statements, IRI subject, IRI predicate, org.eclipse.rdf4j.model.Value object) {
        statements.add(s(subject, predicate, object, i(persistentUrl)));
    }

}
