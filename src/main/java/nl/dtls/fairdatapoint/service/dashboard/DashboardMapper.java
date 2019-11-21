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
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardCatalogDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDatasetDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDistributionDTO;
import nl.dtls.fairdatapoint.api.dto.membership.MembershipDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardMapper {

    public DashboardCatalogDTO toCatalogDTO(CatalogMetadata c, List<DashboardDatasetDTO> datasets,
                                            Optional<MembershipDTO> membership) {
        return new DashboardCatalogDTO(
                c.getIdentifier().getIdentifier().getLabel(),
                c.getUri().toString(),
                c.getTitle().getLabel(),
                datasets,
                membership
        );
    }

    public DashboardDatasetDTO toDatasetDTO(DatasetMetadata d, List<DashboardDistributionDTO> datasets,
                                            Optional<MembershipDTO> membership) {
        return new DashboardDatasetDTO(
                d.getIdentifier().getIdentifier().getLabel(),
                d.getUri().toString(),
                d.getTitle().getLabel(),
                datasets,
                membership
        );
    }

    public DashboardDistributionDTO toDistributionDTO(DistributionMetadata d, Optional<MembershipDTO> membership) {
        return new DashboardDistributionDTO(
                d.getIdentifier().getIdentifier().getLabel(),
                d.getUri().toString(),
                d.getTitle().getLabel(),
                membership
        );
    }
}
