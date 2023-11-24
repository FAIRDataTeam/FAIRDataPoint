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

import nl.dtls.fairdatapoint.entity.schema.SemVer;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.DCAT3;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

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
    public static final String FIELD_SID = "sid";
    public static final String FIELD_PERM = "permission";
    public static final String FIELD_GRANT = "granting";
    public static final String FIELD_AUDIT_FAILURE = "auditFailure";
    public static final String FIELD_AUDIT_SUCCESS = "auditSuccess";
    public static final String DEFAULT_FDP_TITLE = "My FAIR Data Point";
    public static final String DEFAULT_PUBLISHER = "Default Publisher";
    public static final String SUFFIX_IDENTIFIER = "#identifier";
    public static final String SUFFIX_ACCESS_RIGHTS = "#accessRights";
    public static final String SUFFIX_PUBLISHER = "#publisher";
    public static final String FDP_APP_URL = "https://purl.org/fairdatapoint/app";

    public static final SemVer SEMVER_V1 = new SemVer("1.0.0");

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

    private static void add(List<Statement> statements, IRI predicate,
                            org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(base, predicate, object, base));
    }

    private static void add(List<Statement> statements, IRI subject, IRI predicate,
                            org.eclipse.rdf4j.model.Value object, IRI base) {
        statements.add(s(subject, predicate, object, base));
    }
}
