package nl.dtls.fairdatapoint.service.profile;

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
    private ShapeService shapeService;

    public Optional<Model> getProfileByUuid(String uuid, IRI uri) {
        return shapeService.getShapeByUuid(uuid)
                .map(shape -> {
                    ModelBuilder modelBuilder = new ModelBuilder();
                    Resource resource = bn();
                    modelBuilder.subject(resource);
                    modelBuilder.add(RDF.TYPE, i(format("%s#ResourceDescriptor", PROFILE_PREFIX)));
                    modelBuilder.add(DCTERMS.FORMAT, i("https://w3id.org/mediatype/text/turtle"));
                    modelBuilder.add(DCTERMS.CONFORMS_TO, i("https://www.w3.org/TR/shacl/"));
                    modelBuilder.add(i(format("%s#hasRole", PROFILE_PREFIX)), i(format("%s/role#Validation",
                            PROFILE_PREFIX)));
                    modelBuilder.add(i(format("%s#hasArtifact", PROFILE_PREFIX)), i(format("%s/shapes/%s",
                            persistentUrl, uuid)));

                    Model profile = new LinkedHashModel();
                    profile.add(uri, RDF.TYPE, i(format("%s#Profile", PROFILE_PREFIX)));
                    profile.add(uri, RDFS.LABEL, l(format("Profile for %s", shape.getName())));
                    profile.add(uri, i(format("%s#isProfileOf", PROFILE_PREFIX)), i(format("%s/profile/core",
                            persistentUrl)));
                    profile.add(uri, i(format("%s#hasResource", PROFILE_PREFIX)), resource);
                    profile.addAll(new ArrayList<>(modelBuilder.build()));

                    return profile;
                });
    }

}
