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
import nl.dtls.fairdatapoint.service.reset.FactoryDefaults;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import nl.dtls.fairdatapoint.vocabulary.Sio;
import nl.dtls.rdf.migration.entity.RdfMigrationAnnotation;
import nl.dtls.rdf.migration.runner.RdfProductionMigration;
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
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

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
    private MongoTemplate mongoTemplate;

    public void runMigration() {
        createRepositoryInTripleStore();
        storePermissionForRepository();
    }

    private void createRepositoryInTripleStore() {
        try (RepositoryConnection conn = repository.getConnection()) {
            List<Statement> s = FactoryDefaults.repositoryStatements(
                    persistentUrl,
                    license,
                    language,
                    accessRightsDescription
            );
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
        return FactoryDefaults.aclRepository(persistentUrl);
    }

}
