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

import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.AbstractMetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import nl.dtls.fairmetadata4j.model.Identifier;
import nl.dtls.fairmetadata4j.vocabulary.DATACITE;
import nl.dtls.fairmetadata4j.vocabulary.R3D;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.setRdfType;
import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.setRepositoryIdentifier;
import static nl.dtls.fairmetadata4j.util.RDFUtil.containsObject;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.l;

@Service("repositoryMetadataService")
public class RepositoryMetadataService extends AbstractMetadataService {

    @Override
    protected void checkParent(Model metadata, IRI uri, ResourceDefinition resourceDefinition) throws MetadataServiceException {
        // Nothing to check
    }

    @Override
    protected void addDefaultValues(Model metadata, IRI uri, ResourceDefinition resourceDefinition) {
        super.addDefaultValues(metadata, uri, resourceDefinition);
        if (!containsObject(metadata, uri.stringValue(), R3D.REPOSITORYIDENTIFIER.stringValue())) {
            Identifier id = generateIdentifier(uri);
            setRepositoryIdentifier(metadata, uri, id);
        }
    }

    private Identifier generateIdentifier(IRI iri) {
        Identifier id = new Identifier();
        id.setUri(i(iri.stringValue() + "#repositoryID"));
        UUID uid = UUID.randomUUID();
        id.setIdentifier(l(uid.toString()));
        id.setType(DATACITE.IDENTIFIER);
        return id;
    }
}
