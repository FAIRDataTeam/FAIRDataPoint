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

import nl.dtls.fairdatapoint.entity.metadata.Identifier;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.metric.MetricsMetadataService;
import nl.dtls.fairdatapoint.util.ValueFactoryHelper;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.*;
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
    private MetricsMetadataService metricsMetadataService;

    public void enhance(Model metadata, IRI uri, ResourceDefinition rd, Model oldMetadata) {
        enhance(metadata, uri, rd);

        // Populate with current data from the triple store
        setIssued(metadata, uri, l(getIssued(oldMetadata)));
        if (rd.getUrlPrefix().equals("catalog")) {
            setMetadataIssued(metadata, uri, l(getMetadataIssued(oldMetadata)));
        }
    }

    public void enhance(Model metadata, IRI uri, ResourceDefinition rd) {
        // Add RDF Type
        List<IRI> targetClassUris = rd.getTargetClassUris()
                .stream()
                .map(ValueFactoryHelper::i)
                .collect(Collectors.toList());
        setRdfTypes(metadata, uri, targetClassUris);

        // Add identifiers
        Identifier identifier = createMetadataIdentifier(uri);
        setMetadataIdentifier(metadata, uri, identifier);
        if (rd.getUrlPrefix().equals("")) {
            setRepositoryIdentifier(metadata, uri, identifier);
        }

        // Add label
        if (containsObject(metadata, uri.stringValue(), DCTERMS.TITLE.stringValue())) {
            setLabel(metadata, uri, getTitle(metadata));
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
        setMetrics(metadata, uri, metricsMetadataService.generateMetrics(uri));

        // Add timestamps
        LocalDateTime timestamp = LocalDateTime.now();
        setIssued(metadata, uri, l(timestamp));
        setModified(metadata, uri, l(timestamp));
        if (rd.getUrlPrefix().equals("catalog")) {
            setMetadataIssued(metadata, uri, l(timestamp));
            setMetadataModified(metadata, uri, l(timestamp));
        }
    }

    private Identifier createMetadataIdentifier(IRI uri) {
        IRI identifierUri = i(uri.stringValue() + "#identifier");
        return new Identifier(identifierUri, DATACITE.IDENTIFIER, l(uri));
    }

}


