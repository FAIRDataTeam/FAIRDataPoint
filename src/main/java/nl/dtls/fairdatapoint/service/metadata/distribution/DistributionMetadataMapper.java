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
package nl.dtls.fairdatapoint.service.metadata.distribution;

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.dto.common.BreadcrumbDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataSimpleDTO;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataMapper;
import nl.dtls.fairdatapoint.service.uri.UriMapper;
import org.eclipse.rdf4j.model.Literal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

@Service
public class DistributionMetadataMapper implements MetadataMapper<DistributionMetadata, DistributionMetadataChangeDTO> {

    @Autowired
    private UriMapper uriMapper;

    public DistributionMetadataDTO toDTO(DistributionMetadata d, FDPMetadata repository, CatalogMetadata catalog,
                                         DatasetMetadata dataset, Optional<MemberDTO> member) {
        return new DistributionMetadataDTO(
                d.getIdentifier().getIdentifier().getLabel(),
                d.getUri().toString(),
                d.getTitle().getLabel(),
                ofNullable(d.getDescription()).map(Literal::getLabel),
                d.getIssued().getLabel(),
                d.getModified().getLabel(),
                d.getVersion().getLabel(),
                uriMapper.toDTO(d.getLicense()),
                d.getAccessRights().getDescription().getLabel(),
                uriMapper.toDTO(d.getSpecification()),
                ofNullable(d.getLanguage()).map(uriMapper::toDTO),
                uriMapper.toDTO(d.getPublisher().getUri()),
                d.getMediaType().getLabel(),
                d.getDownloadURL() != null ? d.getDownloadURL().toString() : null,
                d.getAccessURL() != null ? d.getAccessURL().toString() : null,
                new HashMap<>() {{
                    put("repository", new BreadcrumbDTO(repository.getTitle().getLabel(),
                            repository.getIdentifier().getIdentifier().getLabel()));
                    put("catalog", new BreadcrumbDTO(catalog.getTitle().getLabel(),
                            catalog.getIdentifier().getIdentifier().getLabel()));
                    put("dataset", new BreadcrumbDTO(dataset.getTitle().getLabel(),
                            dataset.getIdentifier().getIdentifier().getLabel()));
                }},
                member.map(MemberDTO::getMembership).orElse(null)
        );

    }

    public DistributionMetadataSimpleDTO toSimpleDTO(DistributionMetadata d) {
        return new DistributionMetadataSimpleDTO(
                d.getIdentifier().getIdentifier().getLabel(),
                d.getUri().toString(),
                d.getTitle().getLabel(),
                ofNullable(d.getDescription()).map(Literal::getLabel),
                d.getMediaType().getLabel(),
                d.getIssued().getLabel(),
                d.getModified().getLabel()
        );
    }

    public DistributionMetadata fromChangeDTO(DistributionMetadata metadata, DistributionMetadataChangeDTO reqDto) {
        metadata.setTitle(l(reqDto.getTitle()));
        metadata.setDescription(l(reqDto.getDescription()));
        metadata.setVersion(l(reqDto.getVersion()));
        metadata.setLicense(i(reqDto.getLicense()));
        metadata.setLanguage(i(reqDto.getLanguage()));
        metadata.setMediaType(l(reqDto.getMediaType()));
        metadata.setDownloadURL(reqDto.getDownloadUrl() != null ? i(reqDto.getDownloadUrl()) :
                null);
        metadata.setAccessURL(reqDto.getAccessUrl() != null ? i(reqDto.getAccessUrl()) : null);
        return metadata;
    }
}
