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
package nl.dtls.fairdatapoint.service.profile;

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final String PROFILE_PREFIX = "http://www.w3.org/ns/dx/prof/";

    private final String persistentUrl;

    private final MetadataSchemaVersionRepository metadataSchemaRepository;

    private final ResourceDefinitionService resourceDefinitionService;

    private Model getProfileForResourceDefinition(
            ResourceDefinition resourceDefinition, IRI uri
    ) {
        final Model profile = new LinkedHashModel();
        profile.add(uri, RDF.TYPE, i(format("%sProfile", PROFILE_PREFIX)));
        profile.add(uri, RDFS.LABEL, l(format("%s Profile", resourceDefinition.getName())));
        profile.add(
                uri,
                i(format("%sisProfileOf", PROFILE_PREFIX)),
                i(format("%s/profile/core", persistentUrl))
        );
        resourceDefinition
                .getMetadataSchemaUsages()
                .forEach(usage -> {
                    metadataSchemaRepository.getLatestBySchemaUuid(usage.getUsedMetadataSchema().getUuid()).ifPresent(schema -> {
                        addSchemaToProfile(uri, profile, schema);
                    });
                });
        return profile;
    }

    private void addSchemaToProfile(IRI uri, Model profile, MetadataSchemaVersion schema) {
        final ModelBuilder modelBuilder = new ModelBuilder();
        final Resource resource = bn();
        modelBuilder.subject(resource);
        modelBuilder.add(RDF.TYPE, i(format("%s#ResourceDescriptor", PROFILE_PREFIX)));
        modelBuilder.add(RDFS.LABEL, l(schema.getName()));
        modelBuilder.add(DCTERMS.FORMAT, i("https://w3id.org/mediatype/text/turtle"));
        modelBuilder.add(DCTERMS.CONFORMS_TO, i("https://www.w3.org/TR/shacl/"));
        modelBuilder.add(i(format("%shasRole", PROFILE_PREFIX)), i(format("%srole/Validation",
                PROFILE_PREFIX)));
        modelBuilder.add(i(format("%shasArtifact", PROFILE_PREFIX)), i(format("%s/metadata-schemas/%s",
                persistentUrl, schema.getUuid())));
        profile.add(uri, i(format("%shasResource", PROFILE_PREFIX)), resource);
        profile.addAll(new ArrayList<>(modelBuilder.build()));
    }

    public Optional<Model> getProfileByUuid(UUID uuid, IRI uri) {
        return resourceDefinitionService
                .getByUuid(uuid)
                .map(definition -> getProfileForResourceDefinition(definition, uri));
    }

    public IRI getProfileUri(ResourceDefinition definition) {
        return i(format("%s/profile/%s", persistentUrl, definition.getUuid()));
    }
}
