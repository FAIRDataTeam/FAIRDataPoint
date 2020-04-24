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
package nl.dtls.fairdatapoint.api.controller.metadata;

import nl.dtls.fairdatapoint.api.dto.member.MemberCreateDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getMetadataIdentifier;
import static nl.dtls.fairdatapoint.util.HttpUtil.getRequestURL;
import static nl.dtls.fairdatapoint.util.RdfUtil.removeLastPartOfIRI;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@RestController
public class GenericMemberController {

    @Value("${instance.url}")
    private String instanceUrl;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MetadataServiceFactory metadataServiceFactory;

    @RequestMapping(value = "**/member", method = RequestMethod.GET)
    public MemberDTO getMember(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        IRI uri = i(getRequestURL(request, instanceUrl));
        IRI entityUri = removeLastPartOfIRI(uri);
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Get member
        String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(entityId, Metadata.class);
        return oMember.orElse(new MemberDTO(null, null));
    }

    @RequestMapping(value = "**/members", method = RequestMethod.GET)
    public ResponseEntity<List<MemberDTO>> getMembers(HttpServletRequest request)
            throws ResourceNotFoundException, MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        IRI uri = i(getRequestURL(request, instanceUrl));
        IRI entityUri = removeLastPartOfIRI(uri);
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Get members
        String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        List<MemberDTO> dto = memberService.getMembers(entityId, Metadata.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "**/members/{userUuid}", method = RequestMethod.PUT)
    public ResponseEntity<MemberDTO> putMember(@PathVariable final String userUuid,
                                               HttpServletRequest request,
                                               @RequestBody @Valid MemberCreateDTO reqBody)
            throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        IRI uri = i(getRequestURL(request, instanceUrl));
        IRI entityUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Create / Update member
        String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        MemberDTO dto = memberService.createOrUpdateMember(entityId, Metadata.class, userUuid,
                reqBody.getMembershipUuid());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "**/members/{userUuid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteMember(@PathVariable final String userUuid, HttpServletRequest request)
            throws ResourceNotFoundException, MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForDetail(getRequestURL(request, instanceUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        IRI uri = i(getRequestURL(request, instanceUrl));
        IRI entityUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        Model metadata = metadataService.retrieve(entityUri);

        // 3. Delete member
        String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        memberService.deleteMember(entityId, Metadata.class, userUuid);
        return ResponseEntity.noContent().build();
    }

    private String getResourceNameForList(String url) throws MetadataServiceException {
        String[] parts = url.split("/");
        if (!(parts.length == 4 || parts.length == 6)) {
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
