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

import nl.dtls.fairmetadata4j.model.DistributionMetadata;
import nl.dtls.fairmetadata4j.utils.MetadataParserUtils;
import nl.dtls.fairdatapoint.api.dto.metadata.DistributionMetadataChangeDTO;
import nl.dtls.fairdatapoint.service.metadata.common.AbstractMetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataMapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;

@Service
public class DistributionMetadataService extends AbstractMetadataService<DistributionMetadata,
        DistributionMetadataChangeDTO> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DistributionMetadataService.class);

    @Autowired
    private DistributionMetadataMapper distributionMetadataMapper;

    public DistributionMetadataService(@Value("${metadataProperties.distributionSpecs:}") String specs) {
        super();
        this.specs = specs;
        this.parentType = DCAT.DATASET;
    }

    @Override
    public MetadataMapper<DistributionMetadata, DistributionMetadataChangeDTO> metadataMapper() {
        return distributionMetadataMapper;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected DistributionMetadata parse(@Nonnull List<Statement> statements, @Nonnull IRI iri) {
        return MetadataParserUtils.getDistributionParser().parse(statements, iri);
    }

    @Override
    protected void updateParent(DistributionMetadata metadata) {
        metadataUpdateService.visit(metadata);
    }
}
