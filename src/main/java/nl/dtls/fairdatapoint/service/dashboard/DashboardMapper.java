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
import nl.dtls.fairdatapoint.api.dto.membership.MembershipDTO;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getTitle;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;

@Service
public class DashboardMapper {

    public DashboardItemDTO toCatalogDTO(Model c, List<DashboardItemDTO> datasets, Optional<MembershipDTO> membership) {
        return new DashboardItemDTO(
                getUri(c).toString(),
                getTitle(c).getLabel(),
                datasets,
                membership
        );
    }

    public DashboardItemDTO toDatasetDTO(Model d, List<DashboardItemDTO> datasets, Optional<MembershipDTO> membership) {
        return new DashboardItemDTO(
                getUri(d).toString(),
                getTitle(d).getLabel(),
                datasets,
                membership
        );
    }

    public DashboardItemDTO toDistributionDTO(Model d, Optional<MembershipDTO> membership) {
        return new DashboardItemDTO(
                getUri(d).toString(),
                getTitle(d).getLabel(),
                List.of(),
                membership
        );
    }
}
