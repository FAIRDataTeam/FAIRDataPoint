package nl.dtls.fairdatapoint.api.controller.metadata;

import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairdatapoint.service.rdf.RdfFileService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static nl.dtls.fairdatapoint.util.ResourceReader.loadResource;
import static nl.dtls.fairmetadata4j.util.RDFUtil.*;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;

@RestController
@RequestMapping("/")
public class GenericController extends MetadataController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RdfFileService rdfFileService;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @RequestMapping(value = "**/spec", method = RequestMethod.GET)
    public Model getFormMetadata() {
        return rdfFileService.readFile("form-specs/metamodel.ttl", "http://example.com");
    }

    @RequestMapping(value = "**", method = RequestMethod.GET)
    public Model getMetaData(HttpServletRequest request)
            throws MetadataServiceException, ResourceNotFoundException {
        // 1. Init
        String uri = getRequestURL(request);
        Model resultRdf = new LinkedHashModel();
        String urlPrefix = getResourceNameForList(uri);
        MetadataService metadataService = getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUriPrefix(urlPrefix);
        if (oRd.isEmpty()) {
            throw new MetadataServiceException("Unsupported resource type");
        }
        ResourceDefinition rd = oRd.get();

        // 3. Get entity
        IRI entityUri = getRequestURLasIRI(request);
        Model entity = metadataService.retrieve(entityUri);
        resultRdf.addAll(entity);

        // 3. Get children
        if (rd.getChild() != null) {
            for (org.eclipse.rdf4j.model.Value datasetUri : getObjectsBy(entity, entityUri, i(rd.getChild()))) {
                Model dataset = metadataService.retrieve(i(datasetUri.stringValue()));
                resultRdf.addAll(dataset);
            }
        }

        // 4. Get parent
        while (true) {
            IRI parentUri = i(getStringObjectBy(entity, entityUri, DCTERMS.IS_PART_OF));
            if (parentUri == null) {
                break;
            }
            Model parent = metadataService.retrieve(parentUri);
            resultRdf.addAll(parent);
            entity = parent;
            entityUri = parentUri;
            rd = rd.getParent();
        }

        // 5. Create response
        return resultRdf;
    }

    @RequestMapping(value = "**/member", method = RequestMethod.GET)
    public MemberDTO getMember(HttpServletRequest request) {
        String uri = getRequestURL(request);
        String entityId = removeLastPartOfIRI(i(uri)).getLocalName();
        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(entityId, Metadata.class);
        return oMember.orElse(new MemberDTO(null, null));
    }

    @RequestMapping(
            value = "**",
            method = RequestMethod.POST,
            consumes = {"text/plain", "text/turtle", "application/ld+json", "application/rdf+xml", "text/n3"})
    public ResponseEntity<Model> storeMetaData(HttpServletRequest request, HttpServletResponse response,
                                               @RequestBody String reqBody)
            throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request));
        MetadataService metadataService = getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUriPrefix(urlPrefix);
        if (oRd.isEmpty()) {
            throw new MetadataServiceException("Unsupported resource type");
        }
        ResourceDefinition rd = oRd.get();

        // 3. Generate URI
        IRI uri = generateNewIRI(request);

        // 4. Validate with Shacl
        String shacl = loadResource("form-specs/metamodel.ttl");
        rdfFileService.validate(shacl, reqBody, uri.stringValue());

        // 5. Parse metadata
        Model model = rdfFileService.read(reqBody, uri.toString());

        // 6. Store metadata
        metadataService.store(model, uri, rd);

        // 7. Create response
        response.addHeader(HttpHeaders.LOCATION, uri.toString());
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "**",
            method = RequestMethod.PUT,
            consumes = {"text/plain", "text/turtle", "application/ld+json", "application/rdf+xml", "text/n3"})
    public ResponseEntity updateMetaData(HttpServletRequest request, @RequestBody String reqBody)
            throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request));
        MetadataService metadataService = getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        Optional<ResourceDefinition> oRd = resourceDefinitionRepository.findByUriPrefix(urlPrefix);
        if (oRd.isEmpty()) {
            throw new MetadataServiceException("Unsupported resource type");
        }
        ResourceDefinition rd = oRd.get();

        // 3. Extract URI
        IRI uri = getRequestURLasIRI(request);

        // 4. Validate with Shacl
        String shacl = loadResource("form-specs/metamodel.ttl");
        rdfFileService.validate(shacl, reqBody, uri.stringValue());

        // 5. Parse metadata
        Model model = rdfFileService.read(reqBody, uri.toString());

        // 6. Store metadata
        metadataService.update(model, uri, rd);

        // 7. Create response
        return ResponseEntity.noContent().build();
    }


    private String getResourceNameForList(String url) throws MetadataServiceException {
        String[] parts = url.split("/");
        if (parts.length == 3) {
            return "";
        }
        if (parts.length != 5) {
            throw new MetadataServiceException("Unsupported URL");
        }
        return parts[3];
    }

    private String getResourceNameForDetail(String url) throws MetadataServiceException {
        String[] parts = url.split("/");
        if (parts.length != 4) {
            throw new MetadataServiceException("Unsupported URL");
        }
        return parts[3];
    }

}
