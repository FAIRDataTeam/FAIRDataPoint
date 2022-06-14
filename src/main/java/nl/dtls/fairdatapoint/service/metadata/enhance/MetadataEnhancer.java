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
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import nl.dtls.fairdatapoint.service.actuator.AppInfoContributor;
import nl.dtls.fairdatapoint.service.metadata.metric.MetricsMetadataService;
import nl.dtls.fairdatapoint.service.profile.ProfileService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.util.ValueFactoryHelper;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.*;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataSetter.*;
import static nl.dtls.fairdatapoint.util.RdfUtil.containsObject;
import static nl.dtls.fairdatapoint.util.RdfUtil.getObjectsBy;
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
    private String persistentUrl;

    @Autowired
    private MetricsMetadataService metricsMetadataService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Autowired
    private AppInfoContributor appInfoContributor;

    public void enhance(Model metadata, IRI uri, ResourceDefinition definition, Model oldMetadata) {
        enhance(metadata, uri, definition);

        // Populate with current data from the triple store
        setIssued(metadata, uri, l(getIssued(oldMetadata)));
        if (definition.isCatalog()) {
            setMetadataIssued(metadata, uri, l(getMetadataIssued(oldMetadata)));
        }
    }

    public void enhance(Model metadata, IRI uri, ResourceDefinition definition) {
        // Add RDF Type
        final List<IRI> targetClassUris = resourceDefinitionService
                .getTargetClassUris(definition)
                .stream()
                .map(ValueFactoryHelper::i)
                .collect(Collectors.toList());
        setRdfTypes(metadata, uri, targetClassUris);

        // Add identifiers
        final Identifier identifier = createMetadataIdentifier(uri);
        setMetadataIdentifier(metadata, uri, identifier);

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
            final IRI arIri = i(uri.stringValue() + "#accessRights");
            setAccessRights(metadata, uri, arIri, accessRightsDescription);
        }

        // Add FAIR metrics
        setMetrics(metadata, uri, metricsMetadataService.generateMetrics(uri));

        // Add timestamps
        final OffsetDateTime timestamp = OffsetDateTime.now();
        setIssued(metadata, uri, l(timestamp));
        setModified(metadata, uri, l(timestamp));
        if (definition.isCatalog()) {
            setMetadataIssued(metadata, uri, l(timestamp));
            setMetadataModified(metadata, uri, l(timestamp));
        }
    }

    public void enhanceWithLinks(IRI entityUri, Model entity, ResourceDefinition definition, String url,
                                 Model resultRdf) {
        for (ResourceDefinitionChild child : definition.getChildren()) {
            final ResourceDefinition rdChild = resourceDefinitionCache.getByUuid(child.getResourceDefinitionUuid());
            final IRI container = i(format("%s/%s/", url, rdChild.getUrlPrefix()));

            resultRdf.add(container, RDF.TYPE, LDP.DIRECT_CONTAINER);
            resultRdf.add(container, DCTERMS.TITLE, l(child.getListView().getTitle()));
            resultRdf.add(container, LDP.MEMBERSHIP_RESOURCE, entityUri);
            resultRdf.add(container, LDP.HAS_MEMBER_RELATION, i(child.getRelationUri()));
            for (org.eclipse.rdf4j.model.Value childUri : getObjectsBy(entity, entityUri, i(child.getRelationUri()))) {
                resultRdf.add(container, LDP.CONTAINS, i(childUri.stringValue()));
            }
        }
    }

    public void enhanceWithResourceDefinition(IRI entityUri, ResourceDefinition definition, Model resultRdf) {
        resultRdf.add(entityUri, DCTERMS.CONFORMS_TO, profileService.getProfileUri(definition));
        resultRdf.add(profileService.getProfileUri(definition), RDFS.LABEL,
                l(format("%s Profile", definition.getName())));
        if (definition.isRoot()) {
            resultRdf.add(entityUri, FDP.FDPSOFTWAREVERSION, l(format("FDP:%s", appInfoContributor.getFdpVersion())));
            resultRdf.add(entityUri, DCAT.ENDPOINT_URL, i(persistentUrl));
        }
    }

    private Identifier createMetadataIdentifier(IRI uri) {
        final IRI identifierUri = i(uri.stringValue() + "#identifier");
        return new Identifier(identifierUri, DATACITE.IDENTIFIER, l(uri));
    }

}
