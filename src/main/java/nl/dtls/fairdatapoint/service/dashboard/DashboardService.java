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
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.*;
import static nl.dtls.fairdatapoint.util.ThrowingFunction.suppress;

@Service
public class DashboardService {

    @Autowired
    @Qualifier("catalogMetadataService")
    private MetadataService catalogMetadataService;

    @Autowired
    @Qualifier("genericMetadataService")
    private MetadataService genericMetadataService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private DashboardMapper dashboardMapper;

    public List<DashboardItemDTO> getDashboard(IRI repositoryUri) throws MetadataServiceException {
        Model repository = genericMetadataService.retrieve(repositoryUri);
        return getDashboardCatalogs(repository);
    }

    private List<DashboardItemDTO> getDashboardCatalogs(Model fdpMetadata) throws MetadataServiceException {
        List<Model> catalogs = catalogMetadataService.retrieve(getCatalogs(fdpMetadata));
        return catalogs.stream()
                .map(suppress(this::getDashboardCatalog))
                .filter(c -> c.getMembership().isPresent() || c.getChildren().size() > 0)
                .collect(Collectors.toList());
    }

    private DashboardItemDTO getDashboardCatalog(Model catalog) throws MetadataServiceException {
        String catalogId = getMetadataIdentifier(catalog).getIdentifier().getLabel();
        Optional<MemberDTO> oCatalogMember = memberService.getMemberForCurrentUser(catalogId, Metadata.class);
        List<DashboardItemDTO> datasetDtos = getDashboardDatasets(catalog);
        return dashboardMapper.toCatalogDTO(catalog, datasetDtos, oCatalogMember.map(MemberDTO::getMembership));
    }

    private List<DashboardItemDTO> getDashboardDatasets(Model catalog) throws MetadataServiceException {
        List<Model> datasets = genericMetadataService.retrieve(getDatasets(catalog));
        return datasets.stream()
                .map(suppress(this::getDashboardDataset))
                .filter(d -> d.getMembership().isPresent() || d.getChildren().size() > 0)
                .collect(Collectors.toList());
    }

    private DashboardItemDTO getDashboardDataset(Model dataset) throws MetadataServiceException {
        String datasetId = getMetadataIdentifier(dataset).getIdentifier().getLabel();
        Optional<MemberDTO> oDatasetMember = memberService.getMemberForCurrentUser(datasetId, Metadata.class);
        List<DashboardItemDTO> distributionDtos = getDashboardDistributions(dataset);
        return dashboardMapper.toDatasetDTO(dataset, distributionDtos, oDatasetMember.map(MemberDTO::getMembership));
    }

    private List<DashboardItemDTO> getDashboardDistributions(Model dataset) throws MetadataServiceException {
        List<Model> distributions = genericMetadataService.retrieve(getDistributions(dataset));
        return distributions.stream()
                .map(this::getDashboardDistribution)
                .filter(d -> d.getMembership().isPresent())
                .collect(Collectors.toList());
    }

    private DashboardItemDTO getDashboardDistribution(Model distribution) {
        String distributionId = getMetadataIdentifier(distribution).getIdentifier().getLabel();
        Optional<MemberDTO> oDistributionMember = memberService.getMemberForCurrentUser(distributionId, Metadata.class);
        return dashboardMapper.toDistributionDTO(distribution, oDistributionMember.map(MemberDTO::getMembership));
    }

}
