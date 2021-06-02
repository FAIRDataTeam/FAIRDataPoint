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

import nl.dtls.fairdatapoint.database.mongo.repository.ShapeRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.shape.ShapeService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.*;

@Service
public class ProfileService {

    private static final String PROFILE_PREFIX = "http://www.w3.org/ns/dx/prof";

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private ShapeRepository shapeRepository;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    private Model getProfileForResourceDefinition(ResourceDefinition rd, IRI uri) {
        Model profile = new LinkedHashModel();
        profile.add(uri, RDF.TYPE, i(format("%s#Profile", PROFILE_PREFIX)));
        profile.add(uri, RDFS.LABEL, l(format("%s Profile", rd.getName())));
        profile.add(uri, i(format("%s#isProfileOf", PROFILE_PREFIX)), i(format("%s/profile/core",
                persistentUrl)));
        rd.getShapeUuids().forEach(shapeUuid -> shapeRepository.findByUuid(shapeUuid).map(shape -> {
            ModelBuilder modelBuilder = new ModelBuilder();
            Resource resource = bn();
            modelBuilder.subject(resource);
            modelBuilder.add(RDF.TYPE, i(format("%s#ResourceDescriptor", PROFILE_PREFIX)));
            modelBuilder.add(DCTERMS.FORMAT, i("https://w3id.org/mediatype/text/turtle"));
            modelBuilder.add(DCTERMS.CONFORMS_TO, i("https://www.w3.org/TR/shacl/"));
            modelBuilder.add(i(format("%s#hasRole", PROFILE_PREFIX)), i(format("%s/role#Validation",
                    PROFILE_PREFIX)));
            modelBuilder.add(i(format("%s#hasArtifact", PROFILE_PREFIX)), i(format("%s/shapes/%s",
                    persistentUrl, shapeUuid)));
            profile.add(uri, i(format("%s#hasResource", PROFILE_PREFIX)), resource);
            profile.addAll(new ArrayList<>(modelBuilder.build()));
            return null;
        }));
        return profile;
    }

    public Optional<Model> getProfileByUuid(String uuid, IRI uri) {
        return resourceDefinitionService.getByUuid(uuid).map(rd -> getProfileForResourceDefinition(rd, uri));
    }

    public IRI getProfileUri(ResourceDefinition rd) {
        return i(format("%s/profile/%s", persistentUrl, rd.getUuid()));
    }
}
