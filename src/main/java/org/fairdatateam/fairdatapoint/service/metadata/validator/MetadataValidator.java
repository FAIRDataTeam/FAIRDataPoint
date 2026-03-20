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
package nl.dtls.fairdatapoint.service.metadata.validator;

import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.rdf.ShaclValidator;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.schema.MetadataSchemaService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getParent;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Service
public class MetadataValidator {

    @Autowired
    @Qualifier("genericMetadataRepository")
    private MetadataRepository metadataRepository;

    @Autowired
    private ShaclValidator shaclValidator;

    @Autowired
    private MetadataSchemaService metadataSchemaService;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    public void validate(Model metadata, IRI uri, ResourceDefinition definition) throws MetadataServiceException {
        validateByShacl(metadata, uri);
        if (!definition.getUrlPrefix().isEmpty()) {
            validateParent(metadata, definition);
        }
    }

    private void validateByShacl(Model metadata, IRI uri) {
        final Model shacl = metadataSchemaService.getShaclFromSchemas();
        shaclValidator.validate(shacl, metadata, uri.stringValue());
    }

    private void validateParent(Model metadata, ResourceDefinition definition) throws MetadataServiceException {
        // 1. Check if parent exists
        final IRI parent = getParent(metadata);
        if (parent == null) {
            throw new ValidationException("Not parent uri");
        }

        // 2. Get parent resource definition
        final ResourceDefinition rdParent = resourceDefinitionService.getByUrl(parent.toString());
        if (rdParent
                .getChildren()
                .stream()
                .noneMatch(rdChild -> rdChild.getResourceDefinitionUuid().equals(definition.getUuid()))
        ) {
            throw new ValidationException(format("Parent is not of correct type (RD: %s)", rdParent.getName()));
        }

        // 3. Check correctness of parent type
        try {
            // select parent based on URI prefix
            for (String rdfType : resourceDefinitionService.getTargetClassUris(rdParent)) {
                if (!metadataRepository.checkExistence(parent, RDF.TYPE, i(rdfType))) {
                    throw new ValidationException(format("Parent is not of type (missing type: %s)", rdfType));
                }
            }
        }
        catch (MetadataRepositoryException exception) {
            throw new MetadataServiceException(exception.getMessage());
        }
    }

}
