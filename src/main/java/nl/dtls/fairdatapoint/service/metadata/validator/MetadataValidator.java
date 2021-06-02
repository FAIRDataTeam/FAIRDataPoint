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
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionService;
import nl.dtls.fairdatapoint.service.shape.ShapeService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    private ShapeService shapeService;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private ResourceDefinitionService resourceDefinitionService;

    public void validate(Model metadata, IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        validateByShacl(metadata, uri);
        if (!rd.getName().equals("Repository")) {
            validateParent(metadata, rd);
        }
    }

    private void validateByShacl(Model metadata, IRI uri) {
        Model shacl = shapeService.getShaclFromShapes();
        shaclValidator.validate(shacl, metadata, uri.stringValue());
    }

    private void validateParent(Model metadata, ResourceDefinition rd) throws MetadataServiceException {
        // 1. Check if parent exists
        IRI parent = getParent(metadata);
        if (parent == null) {
            throw new ValidationException("Not parent uri");
        }

        // 2. Check correctness of parent type
        try {
            ResourceDefinition rdParent = resourceDefinitionCache.getParentByUuid(rd.getUuid());
            if (rdParent != null) {
                for (String rdfType : resourceDefinitionService.getTargetClassUris(rdParent)) {
                    if (!metadataRepository.checkExistence(parent, RDF.TYPE, i(rdfType))) {
                        throw new ValidationException("Parent is not of correct type");
                    }
                }
            }
        } catch (MetadataRepositoryException e) {
            throw new MetadataServiceException(e.getMessage());
        }
    }

}
