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

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import nl.dtls.rdf.migration.entity.RdfMigrationAnnotation;
import nl.dtls.rdf.migration.runner.RdfProductionMigration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.eclipse.rdf4j.model.util.Statements.statement;

@RdfMigrationAnnotation(
        number = 3,
        name = "FDPO Compliance",
        description = "Comply with FDP-O Metadata Service")
@Slf4j
@Service
public class Rdf_Migration_0003_FDPO implements RdfProductionMigration {

    @Autowired
    protected Repository repository;

    private static final String LEGACY_CONFORMS_TO = "https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata";

    public void runMigration() {
        removeOldConformsTo();
        updateRepositoryStatements();
        updateRepositoryCatalogLinks();
        updateOldFdpoStatements();
    }

    private void removeOldConformsTo() {
        // remove conformsTo for repository if present (https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata)
        try (RepositoryConnection conn = repository.getConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(null, DCTERMS.CONFORMS_TO, i(LEGACY_CONFORMS_TO));
            while (queryResult.hasNext()) {
                Statement st = queryResult.next();
                log.debug("Removing old conformsTo: {} {} {}", st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
                conn.remove(st);
            }
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateRepositoryStatements() {
        // change r3d:Repository -> fdp-o:FAIRDataPoint (and dcat:DataService + fdp-o:MetadataService?)
        List<IRI> newTypes = List.of(DCAT.DATA_SERVICE, FDP.METADATASERVICE, FDP.FAIRDATAPOINT);
        try (RepositoryConnection conn = repository.getConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(null, RDF.TYPE, R3D.REPOSITORY);
            while (queryResult.hasNext()) {
                Statement st = queryResult.next();
                for (IRI type : newTypes) {
                    log.debug("Adding: {} {} {}", st.getSubject().stringValue(), RDF.TYPE.stringValue(), type.stringValue());
                    conn.add(statement(st.getSubject(), RDF.TYPE, type, st.getContext()));
                }
                log.debug("Removing: {} {} {}", st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
                conn.remove(st);
            }
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateOldFdpoStatements() {
        // update old FDP-O generated metadata
        try (RepositoryConnection conn = repository.getConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(null, i("http://rdf.biosemantics.org/ontologies/fdp-o#metadataIdentifier"), null);
            while (queryResult.hasNext()) {
                Statement st = queryResult.next();
                log.debug("Adding: {} {} {}", st.getSubject().stringValue(), FDP.METADATAIDENTIFIER.stringValue(), st.getObject());
                conn.add(statement(st.getSubject(), FDP.METADATAIDENTIFIER, st.getObject(), st.getContext()));
                log.debug("Removing: {} {} {}", st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
                conn.remove(st);
            }
            queryResult = conn.getStatements(null, i("http://rdf.biosemantics.org/ontologies/fdp-o#metadataIssued"), null);
            while (queryResult.hasNext()) {
                Statement st = queryResult.next();
                log.debug("Adding: {} {} {}", st.getSubject().stringValue(), FDP.METADATAISSUED.stringValue(), st.getObject());
                conn.add(statement(st.getSubject(), FDP.METADATAISSUED, st.getObject(), st.getContext()));
                log.debug("Removing: {} {} {}", st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
                conn.remove(st);
            }
            queryResult = conn.getStatements(null, i("http://rdf.biosemantics.org/ontologies/fdp-o#metadataModified"), null);
            while (queryResult.hasNext()) {
                Statement st = queryResult.next();
                log.debug("Adding: {} {} {}", st.getSubject().stringValue(), FDP.METADATAMODIFIED.stringValue(), st.getObject());
                conn.add(statement(st.getSubject(), FDP.METADATAMODIFIED, st.getObject(), st.getContext()));
                log.debug("Removing: {} {} {}", st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
                conn.remove(st);
            }
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateRepositoryCatalogLinks() {
        // change r3d:dataCatalog to fdp-o:metadataCatalog property (between Repository/FDP and Catalogs)
        try (RepositoryConnection conn = repository.getConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(null, R3D.DATACATALOG, null);
            while (queryResult.hasNext()) {
                Statement st = queryResult.next();
                log.debug("Adding: {} {} {}", st.getSubject().stringValue(), FDP.METADATACATALOG.stringValue(), st.getObject().stringValue());
                conn.add(statement(st.getSubject(), FDP.METADATACATALOG, st.getObject(), st.getContext()));
                log.debug("Removing: {} {} {}", st.getSubject().stringValue(), st.getPredicate().stringValue(), st.getObject().stringValue());
                conn.remove(st);
            }
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
        }
    }
}
