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
package org.fairdatateam.fairdatapoint.service.dashboard;

import org.fairdatateam.fairdatapoint.api.dto.dashboard.DashboardItemDTO;
import org.fairdatateam.fairdatapoint.api.dto.member.MemberDTO;
import org.fairdatateam.fairdatapoint.api.dto.membership.MembershipDTO;
import org.fairdatateam.fairdatapoint.entity.metadata.Metadata;
import org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.fairdatateam.fairdatapoint.service.member.MemberService;
import org.fairdatateam.fairdatapoint.service.metadata.common.MetadataService;
import org.fairdatateam.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.fairdatateam.fairdatapoint.service.metadata.state.MetadataStateService;
import org.fairdatateam.fairdatapoint.service.resource.ResourceDefinitionCache;
import org.fairdatateam.fairdatapoint.service.resource.ResourceDefinitionService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataGetter.getTitle;
import static org.fairdatateam.fairdatapoint.util.RdfUtil.getObjectsBy;
import static org.fairdatateam.fairdatapoint.util.ValueFactoryHelper.i;

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
        final ResourceDefinition resourceDefinition = resourceDefinitionService.getByUrlPrefix("");
        final Model repository = metadataService.retrieve(repositoryUri);
        return getDashboardItem(repositoryUri, repository, resourceDefinition).getChildren();
    }

    private DashboardItemDTO getDashboardItem(
            IRI metadataUri, Model model, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        final List<DashboardItemDTO> children = new ArrayList<>();
        for (ResourceDefinitionChild rdChild : resourceDefinition.getChildren()) {
            final IRI relationUri = i(rdChild.getRelationUri());
            for (Value childUri : getObjectsBy(model, metadataUri, relationUri)) {
                final IRI childIri = i(childUri.stringValue());
                final DashboardItemDTO child = getDashboardItem(
                        childIri,
                        metadataService.retrieve(childIri),
                        resourceDefinitionCache.getByUuid(rdChild.getResourceDefinitionUuid())
                );
                children.add(child);
            }
        }

        final Optional<MemberDTO> member =
                memberService.getMemberForCurrentUser(metadataUri.stringValue(), Metadata.class);
        final Optional<MembershipDTO> membership = member.map(MemberDTO::getMembership);
        final Metadata state = metadataStateService.get(metadataUri);
        return new DashboardItemDTO(
                metadataUri.toString(),
                getTitle(model).getLabel(),
                children
                        .stream()
                        .filter(this::childOnDashboard)
                        .toList(),
                membership,
                state.getState()
        );
    }

    private boolean childOnDashboard(DashboardItemDTO item) {
        return item.getMembership().isPresent() || item.getChildren().size() > 0;
    }
}
