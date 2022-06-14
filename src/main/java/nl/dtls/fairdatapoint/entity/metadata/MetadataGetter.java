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
package nl.dtls.fairdatapoint.entity.metadata;

import nl.dtls.fairdatapoint.util.ValueFactoryHelper;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.util.RdfUtil.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

public class MetadataGetter {

    // ---------------------------------------------------------------------
    //  Basic
    // ---------------------------------------------------------------------
    public static IRI getUri(Model metadata) {
        return i(getSubjectBy(metadata, DCTERMS.TITLE, null));
    }

    public static Identifier getMetadataIdentifier(Model metadata) {
        return getIdentifier(metadata, FDP.METADATAIDENTIFIER);
    }

    public static IRI getParent(Model metadata) {
        return i(getObjectBy(metadata, null, DCTERMS.IS_PART_OF));
    }

    public static List<IRI> getChildren(Model metadata, IRI child) {
        return getObjectsBy(metadata, null, child)
                .stream()
                .map(ValueFactoryHelper::i)
                .collect(Collectors.toList());
    }

    public static IRI getSpecification(Model metadata) {
        return i(getObjectBy(metadata, null, DCTERMS.CONFORMS_TO));
    }

    // ---------------------------------------------------------------------
    //  Resource
    // ---------------------------------------------------------------------
    public static Literal getTitle(Model metadata) {
        return l(getObjectBy(metadata, null, DCTERMS.TITLE));
    }

    public static Literal getLabel(Model metadata) {
        return l(getObjectBy(metadata, null, RDFS.LABEL));
    }

    public static Literal getDescription(Model metadata) {
        return l(getObjectBy(metadata, null, DCTERMS.DESCRIPTION));
    }

    public static IRI getLanguage(Model metadata) {
        return i(getObjectBy(metadata, null, DCTERMS.LANGUAGE));
    }

    public static IRI getLicence(Model metadata) {
        return i(getObjectBy(metadata, null, DCTERMS.LICENSE));
    }

    public static OffsetDateTime getIssued(Model metadata) {
        final String result = getStringObjectBy(metadata, null, FDP.METADATAISSUED);
        return result != null ? parseDateTimeLiteral(result) : null;
    }

    public static OffsetDateTime getModified(Model metadata) {
        final String result = getStringObjectBy(metadata, null, FDP.METADATAMODIFIED);
        return result != null ? parseDateTimeLiteral(result) : null;
    }

    public static OffsetDateTime getMetadataIssued(Model metadata) {
        final String result = getStringObjectBy(metadata, null, DCTERMS.ISSUED);
        return result != null ? parseDateTimeLiteral(result) : null;
    }

    public static OffsetDateTime getMetadataModified(Model metadata) {
        final String result = getStringObjectBy(metadata, null, DCTERMS.MODIFIED);
        return result != null ? parseDateTimeLiteral(result) : null;
    }

    // ---------------------------------------------------------------------
    //  Children
    // ---------------------------------------------------------------------
    public static List<IRI> getCatalogs(Model metadata) {
        return getChildren(metadata, FDP.METADATACATALOG);
    }

    public static List<IRI> getDatasets(Model metadata) {
        return getChildren(metadata, DCAT.HAS_DATASET);
    }

    public static List<IRI> getDistributions(Model metadata) {
        return getChildren(metadata, DCAT.HAS_DISTRIBUTION);
    }

    // ---------------------------------------------------------------------
    //  Custom
    // ---------------------------------------------------------------------
    public static List<IRI> getThemeTaxonomies(Model metadata) {
        return getObjectsBy(metadata, null, DCAT.THEME_TAXONOMY)
                .stream()
                .map(ValueFactoryHelper::i)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------------------------------------------------
    //  Utils
    // ------------------------------------------------------------------------------------------------------------
    private static OffsetDateTime parseDateTimeLiteral(String literal) {
        return OffsetDateTime.parse(literal, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static Identifier getIdentifier(Model metadata, IRI pred) {
        final IRI idUri = i(getObjectBy(metadata, null, pred));
        if (idUri == null) {
            return null;
        }
        final IRI rdfType = i(getObjectBy(metadata, idUri, RDF.TYPE));
        final Literal id = l(getObjectBy(metadata, idUri, DCTERMS.IDENTIFIER));
        return new Identifier(idUri, rdfType, id);
    }

}
