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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaPathDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateDTO;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import nl.dtls.fairdatapoint.service.metadata.state.MetadataStateService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getMetadataIdentifier;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getTitle;
import static nl.dtls.fairdatapoint.util.HttpUtil.getRequestURL;
import static nl.dtls.fairdatapoint.util.RdfUtil.getStringObjectBy;
import static nl.dtls.fairdatapoint.util.RdfUtil.removeLastPartOfIRI;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Tag(name = "Metadata")
@RestController
public class GenericMetaController {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MetadataServiceFactory metadataServiceFactory;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Operation(hidden = true)
    @RequestMapping(path = "**/meta", method = RequestMethod.GET)
    public MetaDTO getMeta(HttpServletRequest request) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request, persistentUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 3. Get and check existence entity
        IRI uri = i(getRequestURL(request, persistentUrl));
        IRI entityUri = removeLastPartOfIRI(uri);
        Model entity = metadataService.retrieve(entityUri);

        // 4. Get member
        String entityId = getMetadataIdentifier(entity).getIdentifier().getLabel();
        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(entityId, Metadata.class);
        MemberDTO member = oMember.orElse(new MemberDTO(null, null));

        // 5. Get state
        MetaStateDTO state = metadataStateService.getState(entityUri, entity, rd);

        // 6. Make path map
        HashMap<String, MetaPathDTO> pathMap = new HashMap<>();
        while (true) {
            MetaPathDTO entry = new MetaPathDTO();
            entry.setResourceDefinitionUuid(rd.getUuid());
            entry.setTitle(getTitle(entity).stringValue());
            IRI parentUri = i(getStringObjectBy(entity, entityUri, DCTERMS.IS_PART_OF));
            Optional.ofNullable(parentUri).map(IRI::toString).ifPresent(entry::setParent);
            pathMap.put(entityUri.toString(), entry);
            if (parentUri == null) {
                break;
            }
            entity = metadataService.retrieve(parentUri);
            entityUri = parentUri;
            urlPrefix = getResourceNameForList(parentUri.toString());
            rd = resourceDefinitionService.getByUrlPrefix(urlPrefix);
        }

        return new MetaDTO(member, state, pathMap);
    }

    @Operation(hidden = true)
    @RequestMapping(path = "**/meta/state", method = RequestMethod.PUT)
    public MetaStateChangeDTO putMetaState(HttpServletRequest request, @RequestBody @Valid MetaStateChangeDTO reqDto) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = getResourceNameForList(getRequestURL(request, persistentUrl));
        MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        IRI uri = i(getRequestURL(request, persistentUrl));
        IRI entityUri = removeLastPartOfIRI(removeLastPartOfIRI(uri));
        Model model = metadataService.retrieve(entityUri);

        // 3. Get state
        metadataStateService.modifyState(entityUri, reqDto);

        return reqDto;
    }

    private String getResourceNameForList(String url) throws MetadataServiceException {
        url = url.replace(persistentUrl, "")
                .replace("/meta", "")
                .replace("/state", "");

        String[] parts = url.split("/");
        if (parts.length == 1) {
            return "";
        }

        if (parts.length != 3) {
            throw new MetadataServiceException("Unsupported URL");
        }
        return parts[1];
    }
}
