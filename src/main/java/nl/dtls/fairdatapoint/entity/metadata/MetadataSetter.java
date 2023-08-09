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

import nl.dtls.fairdatapoint.vocabulary.DCAT3;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.Sio;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.*;

import java.util.List;

import static nl.dtls.fairdatapoint.util.RdfUtil.update;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

public class MetadataSetter {

    // ------------------------------------------------------------------------------
    //  Basic
    // ------------------------------------------------------------------------------
    public static void setRdfTypes(Model metadata, IRI uri, IRI... rdfTypes) {
        setRdfTypes(metadata, uri, List.of(rdfTypes));
    }

    public static void setRdfTypes(Model metadata, IRI uri, List<IRI> rdfTypes) {
        metadata.remove(uri, RDF.TYPE, null);
        for (IRI rdfType : rdfTypes) {
            metadata.add(uri, RDF.TYPE, rdfType);
        }
    }

    public static void setMetadataIdentifier(Model metadata, IRI uri, Identifier identifier) {
        setIdentifier(metadata, uri, identifier, FDP.METADATAIDENTIFIER);
    }

    public static void setParent(Model metadata, IRI uri, IRI parent) {
        update(metadata, uri, DCTERMS.IS_PART_OF, parent);
    }

    public static void setSpecification(Model metadata, IRI uri, IRI specs) {
        update(metadata, uri, DCTERMS.CONFORMS_TO, specs);
    }

    // ------------------------------------------------------------------------------
    //  Resource
    // ------------------------------------------------------------------------------
    public static void setTitle(Model metadata, IRI uri, Literal title) {
        update(metadata, uri, DCTERMS.TITLE, title);
    }

    public static void setLabel(Model metadata, IRI uri, Literal label) {
        update(metadata, uri, RDFS.LABEL, label);
    }

    public static void setDescription(Model metadata, IRI uri, Literal description) {
        update(metadata, uri, DCTERMS.DESCRIPTION, description);
    }

    public static void setVersion(Model metadata, IRI uri, Literal version) {
        update(metadata, uri, DCAT3.VERSION, version);
    }

    public static void setLanguage(Model metadata, IRI uri, IRI language) {
        update(metadata, uri, DCTERMS.LANGUAGE, language);
    }

    public static void setLicence(Model metadata, IRI uri, IRI license) {
        update(metadata, uri, DCTERMS.LICENSE, license);
    }

    public static void setAccessRights(Model metadata, IRI uri, IRI arIri, String accessRightsDescription) {
        update(metadata, uri, DCTERMS.ACCESS_RIGHTS, arIri);
        update(metadata, arIri, RDF.TYPE, DCTERMS.RIGHTS_STATEMENT);
        update(metadata, arIri, DCTERMS.DESCRIPTION, l(accessRightsDescription));
    }

    public static void setIssued(Model metadata, IRI uri, Literal dateTime) {
        update(metadata, uri, FDP.METADATAISSUED, dateTime);
    }

    public static void setModified(Model metadata, IRI uri, Literal dateTime) {
        update(metadata, uri, FDP.METADATAMODIFIED, dateTime);
    }

    public static void setMetadataIssued(Model metadata, IRI uri, Literal dataRecordIssued) {
        update(metadata, uri, DCTERMS.ISSUED, dataRecordIssued);
    }

    public static void setMetadataModified(Model metadata, IRI uri, Literal dataRecordModified) {
        update(metadata, uri, DCTERMS.MODIFIED, dataRecordModified);
    }

    // ------------------------------------------------------------------------------
    //  Custom
    // ------------------------------------------------------------------------------

    public static void setPublisher(Model metadata, IRI uri, Agent publisher) {
        setAgent(metadata, uri, publisher, DCTERMS.PUBLISHER);
    }

    public static void setMetrics(Model metadata, IRI uri, List<Metric> metrics) {
        metadata.remove(null, Sio.IS_ABOUT, null);
        metadata.remove(null, Sio.REFERS_TO, null);
        metrics.forEach(metric -> {
            metadata.add(uri, Sio.REFERS_TO, metric.getUri());
            metadata.add(metric.getUri(), Sio.IS_ABOUT, metric.getMetricType());
            metadata.add(metric.getUri(), Sio.REFERS_TO, metric.getValue());
        });
    }

    public static void setThemeTaxonomies(Model metadata, IRI uri, List<IRI> themeTaxonomies) {
        update(metadata, uri, DCAT.THEME_TAXONOMY, themeTaxonomies);
    }

    public static void setThemes(Model metadata, IRI uri, List<IRI> themes) {
        update(metadata, uri, DCAT.THEME, themes);
    }

    public static void setMediaType(Model metadata, IRI uri, Literal mediaType) {
        update(metadata, uri, DCAT.MEDIA_TYPE, mediaType);
    }

    public static void setDownloadURL(Model metadata, IRI uri, IRI downloadURL) {
        update(metadata, uri, DCAT.DOWNLOAD_URL, downloadURL);
    }

    public static void setAccessURL(Model metadata, IRI uri, IRI accessURL) {
        update(metadata, uri, DCAT.ACCESS_URL, accessURL);
    }

    public static void setKeywords(Model metadata, IRI uri, List<Literal> keywords) {
        update(metadata, uri, DCAT.KEYWORD, keywords);
    }

    // ------------------------------------------------------------------------------
    //  Utils
    // ------------------------------------------------------------------------------
    private static void setIdentifier(Model metadata, IRI uri, Identifier id, IRI pred) {
        if (id == null) {
            metadata.filter(uri, pred, null)
                    .stream()
                    .findFirst()
                    .ifPresent(statement -> {
                        final IRI identifier = i(statement.getObject().stringValue());
                        metadata.remove(uri, pred, identifier);
                        metadata.remove(identifier, RDF.TYPE, null);
                        metadata.remove(identifier, DCTERMS.IDENTIFIER, null);
                    });
        }
        else {
            update(metadata, uri, pred, id.getUri());
            update(metadata, id.getUri(), RDF.TYPE, id.getType());
            update(metadata, id.getUri(), DCTERMS.IDENTIFIER, id.getIdentifier());
        }
    }

    public static void setAgent(Model metadata, IRI uri, Agent agent, IRI agentType) {
        if (agent == null) {
            metadata.filter(uri, agentType, null)
                    .stream()
                    .findFirst()
                    .ifPresent(statement -> {
                        final IRI publisherUri = i(statement.getObject().stringValue());
                        metadata.remove(uri, agentType, publisherUri);
                        metadata.remove(publisherUri, RDF.TYPE, null);
                        metadata.remove(publisherUri, FOAF.NAME, null);
                    });
        }
        else {
            update(metadata, uri, agentType, agent.getUri());
            update(metadata, agent.getUri(), RDF.TYPE, agent.getType());
            update(metadata, agent.getUri(), FOAF.NAME, agent.getName());
        }
    }

}
