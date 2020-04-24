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
package nl.dtls.fairdatapoint.service.metadata.enhance;

import nl.dtls.fairdatapoint.entity.metadata.Agent;
import nl.dtls.fairdatapoint.entity.metadata.Identifier;
import nl.dtls.fairdatapoint.entity.metadata.MetadataSetter;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadatametrics.FairMetadataMetricsService;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getIssued;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataSetter.*;
import static nl.dtls.fairdatapoint.util.RdfUtil.containsObject;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

@Service
public class MetadataEnhancer {

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Autowired
    @Qualifier("language")
    private IRI language;

    @Autowired
    @Qualifier("license")
    private IRI license;

    @Autowired
    @Qualifier("publisher")
    private Agent publisher;

    @Autowired
    private FairMetadataMetricsService fmMetricsService;

    public void enhance(Model metadata, IRI uri, ResourceDefinition resourceDefinition, Model oldMetadata) {
        enhance(metadata, uri, resourceDefinition);
        setIssued(metadata, uri, l(getIssued(oldMetadata)));
    }

    public void enhance(Model metadata, IRI uri, ResourceDefinition resourceDefinition) {
        addDefaultValues(metadata, uri, resourceDefinition);
        setSpecification(metadata, uri, resourceDefinition);
        setTimestamps(metadata, uri);
    }

    private void addDefaultValues(Model metadata, IRI uri, ResourceDefinition resourceDefinition) {
        // Add RDF Type
        setRdfTypes(metadata, uri, i(resourceDefinition.getRdfType()), i("http://www.w3.org/ns/dcat#Resource"));

        // Add PID
        setMetadataIdentifier(metadata, uri, createMetadataIdentifier(uri));

        // Add default publisher
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.PUBLISHER.stringValue()) && publisher != null) {
            setPublisher(metadata, uri, publisher);
        }

        // Add default language
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.LANGUAGE.stringValue()) && language != null) {
            setLanguage(metadata, uri, language);
        }

        // Add default license
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.LICENSE.stringValue()) && license != null) {
            setLicence(metadata, uri, license);
        }

        // Add access rights
        if (!containsObject(metadata, uri.stringValue(), DCTERMS.ACCESS_RIGHTS.stringValue())) {
            IRI arIri = i(uri.stringValue() + "#accessRights");
            setAccessRights(metadata, uri, arIri, accessRightsDescription);
        }

        // Add FAIR metrics
        setMetrics(metadata, uri, fmMetricsService.getMetrics(uri));
    }

    private void setSpecification(Model metadata, IRI uri, ResourceDefinition rd) {
        MetadataSetter.setSpecification(metadata, uri, i(rd.getSpecs()));
    }

    private void setTimestamps(Model metadata, IRI uri) {
        setIssued(metadata, uri, l(LocalDateTime.now()));
        setModified(metadata, uri, l(LocalDateTime.now()));
    }

    private Identifier createMetadataIdentifier(IRI uri) {
        IRI identifierUri = i(uri.stringValue() + "#identifier");
        return new Identifier(identifierUri, DATACITE.IDENTIFIER, l(uri));
    }

}


