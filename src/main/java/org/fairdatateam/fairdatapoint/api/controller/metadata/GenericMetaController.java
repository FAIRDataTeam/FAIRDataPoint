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
package org.fairdatateam.fairdatapoint.api.controller.metadata;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.fairdatateam.fairdatapoint.api.dto.member.MemberDTO;
import org.fairdatateam.fairdatapoint.api.dto.metadata.MetaDTO;
import org.fairdatateam.fairdatapoint.api.dto.metadata.MetaPathDTO;
import org.fairdatateam.fairdatapoint.api.dto.metadata.MetaStateChangeDTO;
import org.fairdatateam.fairdatapoint.api.dto.metadata.MetaStateDTO;
import org.fairdatateam.fairdatapoint.entity.metadata.Metadata;
import org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.service.member.MemberService;
import org.fairdatateam.fairdatapoint.service.metadata.common.MetadataService;
import org.fairdatateam.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.fairdatateam.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import org.fairdatateam.fairdatapoint.service.metadata.state.MetadataStateService;
import org.fairdatateam.fairdatapoint.service.resource.ResourceDefinitionService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataGetter.getMetadataIdentifier;
import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataGetter.getTitle;
import static org.fairdatateam.fairdatapoint.util.HttpUtil.getMetadataIRI;
import static org.fairdatateam.fairdatapoint.util.RdfUtil.getStringObjectBy;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.i;

@Tag(name = "Metadata")
@RestController
public class GenericMetaController {

    private static final int SUPPORTED_URL_FRAGMENTS = 3;

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
    @GetMapping(path = {"meta", "{oUrlPrefix:[^.]+}/{oRecordId:[^.]+}/meta"})
    public MetaDTO getMeta(
            @PathVariable final Optional<String> oUrlPrefix,
            @PathVariable final Optional<String> oRecordId
    ) throws MetadataServiceException {
        // 1. Init
        String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");
        final MetadataService metadataService =
                metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get resource definition
        ResourceDefinition definition = resourceDefinitionService.getByUrlPrefix(urlPrefix);

        // 3. Get and check existence entity
        IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        Model entity = metadataService.retrieve(entityUri);

        // 4. Get member
        final String entityId = getMetadataIdentifier(entity).getIdentifier().getLabel();
        final Optional<MemberDTO> oMember =
                memberService.getMemberForCurrentUser(entityId, Metadata.class);
        final MemberDTO member = oMember.orElse(new MemberDTO(null, null));

        // 5. Get state
        final MetaStateDTO state = metadataStateService.getState(entityUri, entity, definition);

        // 6. Make path map
        final Map<String, MetaPathDTO> pathMap = new HashMap<>();
        while (true) {
            final MetaPathDTO entry = new MetaPathDTO();
            entry.setResourceDefinitionUuid(definition.getUuid());
            entry.setTitle(getTitle(entity).stringValue());
            final IRI parentUri = i(getStringObjectBy(entity, entityUri, DCTERMS.IS_PART_OF));
            Optional.ofNullable(parentUri).map(IRI::toString).ifPresent(entry::setParent);
            pathMap.put(entityUri.toString(), entry);
            if (parentUri == null) {
                break;
            }
            entity = metadataService.retrieve(parentUri);
            entityUri = parentUri;
            urlPrefix = getResourceNameForList(parentUri.toString());
            definition = resourceDefinitionService.getByUrlPrefix(urlPrefix);
        }

        return new MetaDTO(member, state, pathMap);
    }

    @Operation(hidden = true)
    @PutMapping(path = {"meta/state", "{oUrlPrefix:[^.]+}/{oRecordId:[^.]+}/meta/state"})
    public MetaStateChangeDTO putMetaState(
            @PathVariable final Optional<String> oUrlPrefix,
            @PathVariable final Optional<String> oRecordId,
            @RequestBody @Valid MetaStateChangeDTO reqDto
    ) throws MetadataServiceException {
        // 1. Init
        final String urlPrefix = oUrlPrefix.orElse("");
        final String recordId = oRecordId.orElse("");
        final MetadataService metadataService =
                metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        final Model model = metadataService.retrieve(entityUri);

        // 3. Get state
        metadataStateService.modifyState(entityUri, reqDto);

        return reqDto;
    }

    private String getResourceNameForList(String url) throws MetadataServiceException {
        final String fixedUrl = url
                .replace(persistentUrl, "")
                .replace("/meta", "")
                .replace("/state", "");

        final String[] parts = fixedUrl.split("/");
        if (parts.length == 1) {
            return "";
        }
        if (parts.length != SUPPORTED_URL_FRAGMENTS) {
            throw new MetadataServiceException("Unsupported URL");
        }
        return parts[1];
    }
}
