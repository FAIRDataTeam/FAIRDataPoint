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
package nl.dtls.fairdatapoint.service.metadata.repository;

import com.google.common.base.Preconditions;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.model.Identifier;
import nl.dtl.fairmetadata4j.utils.MetadataParserUtils;
import nl.dtl.fairmetadata4j.utils.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.service.metadata.common.AbstractMetadataService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@Service
public class FdpMetadataService extends AbstractMetadataService<FDPMetadata> {
    private final static Logger LOGGER = LoggerFactory.getLogger(FdpMetadataService.class);

    public FdpMetadataService(@Value("${metadataProperties.rootSpecs:}") String specs) {
        super();
        this.specs = specs;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected FDPMetadata parse(@Nonnull List<Statement> statements, @Nonnull IRI iri) {
        return MetadataParserUtils.getFdpParser().parse(statements, iri);
    }

    @Override
    protected void checkPreconditions(@Nonnull FDPMetadata metadata) {
        Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
    }

    @Override
    protected void updateParent(FDPMetadata metadata) {
        metadataUpdateService.visit(metadata);
    }

    protected void addDefaultValues(@Nonnull FDPMetadata metadata) {
        super.addDefaultValues(metadata);

        if (metadata.getRepostoryIdentifier() == null) {
            LOGGER.info("Repository ID is null or empty, this field value will be generated automatically");
            Identifier id = generateIdentifier(metadata.getUri());
            metadata.setRepostoryIdentifier(id);
        }
    }

    private Identifier generateIdentifier(IRI iri) {
        Identifier id = new Identifier();
        id.setUri(VALUE_FACTORY.createIRI(iri.stringValue() + "#repositoryID"));
        UUID uid = UUID.randomUUID();
        id.setIdentifier(VALUE_FACTORY.createLiteral(uid.toString(), XMLSchema.STRING));
        id.setType(DATACITE.IDENTIFIER);
        return id;
    }
}
