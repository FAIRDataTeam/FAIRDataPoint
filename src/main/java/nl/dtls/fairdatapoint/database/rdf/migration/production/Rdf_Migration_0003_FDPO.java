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
import org.fairdatateam.rdf.migration.entity.RdfMigrationAnnotation;
import org.fairdatateam.rdf.migration.runner.RdfProductionMigration;
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
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.s;

@RdfMigrationAnnotation(
        number = 3,
        name = "FDPO Compliance",
        description = "Comply with FDP-O Metadata Service")
@Slf4j
@Service
public class Rdf_Migration_0003_FDPO implements RdfProductionMigration {

    private static final String LEGACY_CONFORMS_TO = "https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata";

    private static final IRI OLD_METADATA_IDENTIFIER =
            i("http://rdf.biosemantics.org/ontologies/fdp-o#metadataIdentifier");
    private static final IRI OLD_METADATA_ISSUED =
            i("http://rdf.biosemantics.org/ontologies/fdp-o#metadataIssued");
    private static final IRI OLD_METADATA_MODIFIED =
            i("http://rdf.biosemantics.org/ontologies/fdp-o#metadataModified");

    private static final String MSG_ADD = "Adding: {} {} {}";
    private static final String MSG_REMOVE = "Removing: {} {} {}";

    @Autowired
    private Repository repository;

    public void runMigration() {
        removeOldConformsTo();
        updateRepositoryStatements();
        updateRepositoryCatalogLinks();
        updateOldFdpoStatements();
    }

    private void removeOldConformsTo() {
        // remove conformsTo for repository if present (https://www.purl.org/fairtools/fdp/schema/0.1/fdpMetadata)
        try (RepositoryConnection conn = repository.getConnection()) {
            final RepositoryResult<Statement> queryResult =
                    conn.getStatements(null, DCTERMS.CONFORMS_TO, i(LEGACY_CONFORMS_TO));
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                log.warn(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void updateRepositoryStatements() {
        // change r3d:Repository -> fdp-o:FAIRDataPoint (and dcat:DataService + fdp-o:MetadataService?)
        final List<IRI> newTypes = List.of(DCAT.DATA_SERVICE, FDP.METADATASERVICE, FDP.FAIRDATAPOINT);
        try (RepositoryConnection conn = repository.getConnection()) {
            final RepositoryResult<Statement> queryResult = conn.getStatements(null, RDF.TYPE, R3D.REPOSITORY);
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                for (IRI type : newTypes) {
                    log.debug(MSG_ADD, st.getSubject(), RDF.TYPE, type);
                    conn.add(s(st.getSubject(), RDF.TYPE, type, st.getSubject()));
                }
                log.debug(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void updateOldFdpoStatements() {
        // update old FDP-O generated metadata
        try (RepositoryConnection conn = repository.getConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(null, OLD_METADATA_IDENTIFIER, null);
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                log.debug(MSG_ADD, st.getSubject(), FDP.METADATAIDENTIFIER, st.getObject());
                conn.add(s(st.getSubject(), FDP.METADATAIDENTIFIER, st.getObject(), st.getSubject()));
                log.debug(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
            queryResult = conn.getStatements(null, OLD_METADATA_ISSUED, null);
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                log.debug(MSG_ADD, st.getSubject(), FDP.METADATAISSUED, st.getObject());
                conn.add(s(st.getSubject(), FDP.METADATAISSUED, st.getObject(), st.getSubject()));
                log.debug(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
            queryResult = conn.getStatements(null, OLD_METADATA_MODIFIED, null);
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                log.debug(MSG_ADD, st.getSubject(), FDP.METADATAMODIFIED, st.getObject());
                conn.add(s(st.getSubject(), FDP.METADATAMODIFIED, st.getObject(), st.getSubject()));
                log.debug(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void updateRepositoryCatalogLinks() {
        // change r3d:dataCatalog to fdp-o:metadataCatalog property (between Repository/FDP and Catalogs)
        try (RepositoryConnection conn = repository.getConnection()) {
            final RepositoryResult<Statement> queryResult = conn.getStatements(null, R3D.DATACATALOG, null);
            while (queryResult.hasNext()) {
                final Statement st = queryResult.next();
                log.debug(MSG_ADD, st.getSubject(), FDP.METADATACATALOG, st.getObject());
                conn.add(s(st.getSubject(), FDP.METADATACATALOG, st.getObject(), st.getSubject()));
                log.debug(MSG_REMOVE, st.getSubject(), st.getPredicate(), st.getObject());
                conn.remove(st);
            }
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
