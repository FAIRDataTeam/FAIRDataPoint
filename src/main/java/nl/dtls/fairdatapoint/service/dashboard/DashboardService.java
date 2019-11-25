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

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardCatalogDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDatasetDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDistributionDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataChangeDTO;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private MetadataService<CatalogMetadata, CatalogMetadataChangeDTO> catalogMetadataService;

    @Autowired
    private MetadataService<DatasetMetadata, DatasetMetadataChangeDTO> datasetMetadataService;

    @Autowired
    private MetadataService<DistributionMetadata, DistributionMetadataChangeDTO> distributionMetadataService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private DashboardMapper dashboardMapper;

    public List<DashboardCatalogDTO> getDashboard(FDPMetadata fdpMetadata) {
        return getDashboardCatalogs(fdpMetadata);
    }

    private List<DashboardCatalogDTO> getDashboardCatalogs(FDPMetadata fdpMetadata) {
        List<CatalogMetadata> catalogs = catalogMetadataService.retrieve(fdpMetadata.getCatalogs());
        return catalogs.stream()
                .map(this::getDashboardCatalog)
                .filter(c -> c.getMembership().isPresent() || c.getDatasets().size() > 0)
                .collect(Collectors.toList());
    }

    private DashboardCatalogDTO getDashboardCatalog(CatalogMetadata catalog) {
        String catalogId = catalog.getIdentifier().getIdentifier().getLabel();
        Optional<MemberDTO> oCatalogMember = memberService.getMemberForCurrentUser(catalogId,
                CatalogMetadata.class);
        List<DashboardDatasetDTO> datasetDtos = getDashboardDatasets(catalog);
        return dashboardMapper.toCatalogDTO(catalog, datasetDtos, oCatalogMember.map(MemberDTO::getMembership));
    }

    private List<DashboardDatasetDTO> getDashboardDatasets(CatalogMetadata catalog) {
        List<DatasetMetadata> datasets = datasetMetadataService.retrieve(catalog.getDatasets());
        return datasets.stream()
                .map(this::getDashboardDataset)
                .filter(d -> d.getMembership().isPresent() || d.getDistributions().size() > 0)
                .collect(Collectors.toList());
    }

    private DashboardDatasetDTO getDashboardDataset(DatasetMetadata dataset) {
        String datasetId = dataset.getIdentifier().getIdentifier().getLabel();
        Optional<MemberDTO> oDatasetMember = memberService.getMemberForCurrentUser(datasetId,
                DatasetMetadata.class);
        List<DashboardDistributionDTO> distributionDtos = getDashboardDistributions(dataset);
        return dashboardMapper.toDatasetDTO(dataset, distributionDtos, oDatasetMember.map(MemberDTO::getMembership));
    }

    private List<DashboardDistributionDTO> getDashboardDistributions(DatasetMetadata dataset) {
        List<DistributionMetadata> distributions = distributionMetadataService.retrieve(dataset.getDistributions());
        return distributions.stream()
                .map(this::getDashboardDistribution)
                .filter(d -> d.getMembership().isPresent())
                .collect(Collectors.toList());
    }

    private DashboardDistributionDTO getDashboardDistribution(DistributionMetadata distribution) {
        String distributionId = distribution.getIdentifier().getIdentifier().getLabel();
        Optional<MemberDTO> oDistributionMember = memberService.getMemberForCurrentUser(distributionId,
                DistributionMetadata.class);
        return dashboardMapper.toDistributionDTO(distribution, oDistributionMember.map(MemberDTO::getMembership));
    }

}
