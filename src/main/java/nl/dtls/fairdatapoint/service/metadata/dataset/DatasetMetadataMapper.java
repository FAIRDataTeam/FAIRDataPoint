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
package nl.dtls.fairdatapoint.service.metadata.dataset;

import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairmetadata4j.model.DatasetMetadata;
import nl.dtls.fairmetadata4j.model.DistributionMetadata;
import nl.dtls.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.common.BreadcrumbDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DatasetMetadataSimpleDTO;
import nl.dtls.fairdatapoint.api.dto.uri.UriDTO;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataMapper;
import nl.dtls.fairdatapoint.service.metadata.distribution.DistributionMetadataMapper;
import nl.dtls.fairdatapoint.service.uri.UriMapper;
import nl.dtls.fairdatapoint.util.ValueFactoryHelper;
import org.eclipse.rdf4j.model.Literal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

@Service
public class DatasetMetadataMapper implements MetadataMapper<DatasetMetadata, DatasetMetadataChangeDTO> {

    @Autowired
    private UriMapper uriMapper;

    @Autowired
    private DistributionMetadataMapper distributionMetadataMapper;

    public DatasetMetadataDTO toDTO(DatasetMetadata d, List<DistributionMetadata> distributions,
                                    FDPMetadata repository, CatalogMetadata catalog, Optional<MemberDTO> member) {
        return new DatasetMetadataDTO(
                d.getIdentifier().getIdentifier().getLabel(),
                d.getUri().toString(),
                d.getTitle().getLabel(),
                ofNullable(d.getDescription()).map(Literal::getLabel),
                d.getIssued().getLabel(),
                d.getModified().getLabel(),
                d.getVersion().getLabel(),
                ofNullable(d.getLicense()).map(uriMapper::toDTO),
                d.getAccessRights().getDescription().getLabel(),
                uriMapper.toDTO(d.getSpecification()),
                ofNullable(d.getLanguage()).map(uriMapper::toDTO),
                uriMapper.toDTO(d.getPublisher().getUri()),
                d.getThemes().stream().map(uriMapper::toDTO).collect(Collectors.toList()),
                d.getKeywords().stream().map(Literal::getLabel).collect(Collectors.toList()),
                distributions
                        .stream()
                        .map(distributionMetadataMapper::toSimpleDTO)
                        .collect(Collectors.toList()),
                new HashMap<>() {{
                    put("repository", new BreadcrumbDTO(repository.getTitle().getLabel(),
                            repository.getIdentifier().getIdentifier().getLabel()));
                    put("catalog", new BreadcrumbDTO(catalog.getTitle().getLabel(),
                            catalog.getIdentifier().getIdentifier().getLabel()));
                }},
                member.map(MemberDTO::getMembership).orElse(null)
        );
    }

    public DatasetMetadataSimpleDTO toSimpleDTO(DatasetMetadata d) {
        return new DatasetMetadataSimpleDTO(
                d.getIdentifier().getIdentifier().getLabel(),
                d.getUri().toString(),
                d.getTitle().getLabel(),
                ofNullable(d.getDescription()).map(Literal::getLabel),
                d.getThemes().stream().map(tt -> new UriDTO(tt.getLocalName(), tt.toString())).collect(Collectors.toList()),
                d.getDistributions().size(),
                d.getIssued().getLabel(),
                d.getModified().getLabel()
        );
    }

    public DatasetMetadata fromChangeDTO(DatasetMetadata metadata, DatasetMetadataChangeDTO reqDto) {
        metadata.setTitle(l(reqDto.getTitle()));
        metadata.setDescription(l(reqDto.getDescription()));
        metadata.setVersion(l(reqDto.getVersion()));
        metadata.setLicense(i(reqDto.getLicense()));
        metadata.setLanguage(i(reqDto.getLanguage()));
        metadata.setThemes(reqDto.getThemes()
                .stream()
                .map(ValueFactoryHelper::i)
                .collect(Collectors.toList()));
        if (reqDto.getKeywords() != null) {
            metadata.setKeywords(reqDto.getKeywords()
                    .stream()
                    .map(ValueFactoryHelper::l)
                    .collect(Collectors.toList()));
        }
        return metadata;
    }
}
