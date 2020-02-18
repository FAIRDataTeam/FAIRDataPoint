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
package nl.dtls.fairdatapoint.service.metadata.catalog;

import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairmetadata4j.model.DatasetMetadata;
import nl.dtls.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.common.BreadcrumbDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataSimpleDTO;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataMapper;
import nl.dtls.fairdatapoint.service.metadata.dataset.DatasetMetadataMapper;
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
public class CatalogMetadataMapper implements MetadataMapper<CatalogMetadata, CatalogMetadataChangeDTO> {

    @Autowired
    private UriMapper uriMapper;

    @Autowired
    private DatasetMetadataMapper datasetMetadataMapper;

    public CatalogMetadataDTO toDTO(CatalogMetadata c, List<DatasetMetadata> datasets, FDPMetadata repository,
                                    Optional<MemberDTO> member) {
        return new CatalogMetadataDTO(
                c.getIdentifier().getIdentifier().getLabel(),
                c.getUri().toString(),
                c.getTitle().getLabel(),
                ofNullable(c.getDescription()).map(Literal::getLabel),
                c.getIssued().getLabel(),
                c.getModified().getLabel(),
                c.getVersion().getLabel(),
                ofNullable(c.getLicense()).map(uriMapper::toDTO),
                c.getAccessRights().getDescription().getLabel(),
                uriMapper.toDTO(c.getSpecification()),
                ofNullable(c.getLanguage()).map(uriMapper::toDTO),
                uriMapper.toDTO(c.getPublisher().getUri()),
                c.getThemeTaxonomys()
                        .stream()
                        .map(uriMapper::toDTO)
                        .collect(Collectors.toList()),
                datasets
                        .stream()
                        .map(datasetMetadataMapper::toSimpleDTO)
                        .collect(Collectors.toList()),
                new HashMap<>() {{
                    put("repository", new BreadcrumbDTO(repository.getTitle().getLabel(),
                            repository.getIdentifier().getIdentifier().getLabel()));
                }},
                member.map(MemberDTO::getMembership).orElse(null)
        );
    }

    public CatalogMetadataSimpleDTO toSimpleDTO(CatalogMetadata c) {
        return new CatalogMetadataSimpleDTO(
                c.getIdentifier().getIdentifier().getLabel(),
                c.getUri().toString(),
                c.getTitle().getLabel(),
                ofNullable(c.getDescription()).map(Literal::getLabel),
                c.getThemeTaxonomys().stream().map(uriMapper::toDTO).collect(Collectors.toList()),
                c.getDatasets().size(),
                c.getIssued().getLabel(),
                c.getModified().getLabel()
        );
    }

    public CatalogMetadata fromChangeDTO(CatalogMetadata metadata, CatalogMetadataChangeDTO reqDto) {
        metadata.setTitle(l(reqDto.getTitle()));
        metadata.setDescription(l(reqDto.getDescription()));
        metadata.setVersion(l(reqDto.getVersion()));
        metadata.setLicense(i(reqDto.getLicense()));
        metadata.setLanguage(i(reqDto.getLanguage()));
        metadata.setThemeTaxonomys(reqDto.getThemeTaxonomies()
                .stream()
                .map(ValueFactoryHelper::i)
                .collect(Collectors.toList()));
        return metadata;
    }

}
