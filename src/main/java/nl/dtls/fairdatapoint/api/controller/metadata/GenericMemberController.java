package nl.dtls.fairdatapoint.api.controller.metadata;

import nl.dtls.fairdatapoint.api.dto.member.MemberCreateDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.getIdentifier;
import static nl.dtls.fairmetadata4j.util.RDFUtil.removeLastPartOfIRI;

@RestController
public class GenericMemberController extends MetadataController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "**/members", method = RequestMethod.GET)
    public ResponseEntity<List<MemberDTO>> getMembers(HttpServletRequest request)
            throws ResourceNotFoundException, MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request));
        MetadataService metadataService = getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI uri = getRequestURLasIRI(request);
        IRI entityUri = removeLastPartOfIRI(uri);
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Get members
        String entityId = getIdentifier(metadata).getIdentifier().getLabel();
        List<MemberDTO> dto = memberService.getMembers(entityId, Metadata.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "**/members/{userUuid}", method = RequestMethod.PUT)
    public Object putMember(@PathVariable final String userUuid, HttpServletRequest request,
                            @RequestBody @Valid MemberCreateDTO reqBody) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request));
        MetadataService metadataService = getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI uri = getRequestURLasIRI(request);
        IRI entityUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Create / Update member
        String entityId = getIdentifier(metadata).getIdentifier().getLabel();
        MemberDTO dto = memberService.createOrUpdateMember(entityId, Metadata.class, userUuid,
                reqBody.getMembershipUuid());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "**/members/{userUuid}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMember(@PathVariable final String userUuid, HttpServletRequest request)
            throws ResourceNotFoundException, MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request));
        MetadataService metadataService = getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get entity
        IRI uri = getRequestURLasIRI(request);
        IRI entityUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Delete member
        String entityId = getIdentifier(metadata).getIdentifier().getLabel();
        memberService.deleteMember(entityId, Metadata.class, userUuid);
        return ResponseEntity.noContent().build();
    }

    private String getResourceNameForList(String url) throws MetadataServiceException {
        String[] parts = url.split("/");
        if (parts.length != 6) {
            throw new MetadataServiceException("Unsupported URL");
        }
        return parts[3];
    }

    private String getResourceNameForDetail(String url) throws MetadataServiceException {
        String[] parts = url.split("/");
        if (parts.length != 7) {
            throw new MetadataServiceException("Unsupported URL");
        }
        return parts[3];
    }
}
