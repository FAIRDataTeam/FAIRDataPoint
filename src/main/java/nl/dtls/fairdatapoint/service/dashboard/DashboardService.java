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
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardCatalogDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDatasetDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadata.catalog.CatalogMetadataService;
import nl.dtls.fairdatapoint.service.metadata.dataset.DatasetMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private CatalogMetadataService catalogMetadataService;

    @Autowired
    private DatasetMetadataService datasetMetadataService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private DashboardMapper dashboardMapper;

    public List<DashboardCatalogDTO> getDashboard(FDPMetadata fdpMetadata) {
        List<CatalogMetadata> catalogs = catalogMetadataService.retrieve(fdpMetadata.getCatalogs());
        List<DashboardCatalogDTO> dto = new ArrayList<>();
        for (CatalogMetadata catalog : catalogs) {
            String catalogId = catalog.getIdentifier().getIdentifier().getLabel();
            Optional<MemberDTO> oCatalogMember =
                    memberService.getMemberForCurrentUser(catalogId, CatalogMetadata.class);
            if (oCatalogMember.isEmpty()) {
                continue;
            }
            MemberDTO catalogMember = oCatalogMember.get();
            List<DatasetMetadata> datasets = datasetMetadataService.retrieve(catalog.getDatasets());
            List<DashboardDatasetDTO> datasetDtos = new ArrayList<>();
            for (DatasetMetadata dataset : datasets) {
                String datasetId = dataset.getIdentifier().getIdentifier().getLabel();
                Optional<MemberDTO> oDatasetMember = memberService.getMemberForCurrentUser(datasetId,
                        DatasetMetadata.class);
                if (oDatasetMember.isEmpty()) {
                    continue;
                }
                MemberDTO datasetMember = oDatasetMember.get();
                datasetDtos.add(dashboardMapper.toDatasetDTO(dataset, datasetMember.getMembership()));
            }
            dto.add(dashboardMapper.toCatalogDTO(catalog, catalogMember.getMembership(), datasetDtos));
        }
        return dto;
    }

}
