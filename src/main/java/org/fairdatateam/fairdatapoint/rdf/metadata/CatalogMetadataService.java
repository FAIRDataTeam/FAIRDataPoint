/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.rdf.metadata;

import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nonnull;


@Service("catalogMetadataService")
@Slf4j
public class CatalogMetadataService extends AbstractMetadataService {

// TODO: this class is now completely redundant, so we need to remove it,
//  merge GenericMetadataService and AbstractMetadataService into a single MetadataService,
//  and remove MetadataServiceFactory

    @Override
    public Model retrieve(@Nonnull IRI uri) throws MetadataServiceException {
        final Model catalog = super.retrieve(uri);
        return catalog;
    }

    @Override
    public Model store(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        return super.store(metadata, uri, resourceDefinition);
    }

    @Override
    public Model update(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition, boolean validate
    ) throws MetadataServiceException {
        return super.update(metadata, uri, resourceDefinition, validate);
    }
}
