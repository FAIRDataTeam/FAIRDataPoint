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
package nl.dtls.fairdatapoint.service.dashboard;

import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardItemDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.membership.MembershipDTO;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.state.MetadataStateService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getTitle;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static nl.dtls.fairdatapoint.util.RdfUtil.getObjectsBy;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Service
public class DashboardService {

    @Autowired
    @Qualifier("genericMetadataService")
    private MetadataService metadataService;

    @Autowired
    private MetadataStateService metadataStateService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    public List<DashboardItemDTO> getDashboard(IRI repositoryUri) throws MetadataServiceException {
        ResourceDefinition rd = resourceDefinitionService.getByUrlPrefix("");
        Model repository = metadataService.retrieve(repositoryUri);
        return getDashboardItem(repositoryUri, repository, rd).getChildren();
    }

    private DashboardItemDTO getDashboardItem(IRI metadataUri, Model model, ResourceDefinition rd) throws MetadataServiceException {
        List<DashboardItemDTO> children = new ArrayList<>();
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            IRI relationUri = i(rdChild.getRelationUri());
            for (org.eclipse.rdf4j.model.Value childUri : getObjectsBy(model, metadataUri, relationUri)) {
                IRI childIri = i(childUri.stringValue());
                DashboardItemDTO child = getDashboardItem(
                        childIri,
                        metadataService.retrieve(childIri),
                        resourceDefinitionCache.getByUuid(rdChild.getResourceDefinitionUuid())
                );
                children.add(child);
            }
        }

        Optional<MemberDTO> oMember = memberService.getMemberForCurrentUser(metadataUri.stringValue(), Metadata.class);
        Optional<MembershipDTO> membership = oMember.map(MemberDTO::getMembership);
        Metadata state = metadataStateService.get(metadataUri);
        return new DashboardItemDTO(
                metadataUri.toString(),
                getTitle(model).getLabel(),
                children
                        .stream()
                        .filter(e -> e.getMembership().isPresent() || e.getChildren().size() > 0)
                        .collect(Collectors.toList()),
                membership,
                state.getState()
        );
    }

}
