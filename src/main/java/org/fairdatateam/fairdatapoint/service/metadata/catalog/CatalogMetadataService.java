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
package org.fairdatateam.fairdatapoint.service.metadata.catalog;

import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.database.rdf.repository.catalog.CatalogMetadataRepository;
import org.fairdatateam.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import org.fairdatateam.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.service.metadata.common.AbstractMetadataService;
import org.fairdatateam.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;

import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataSetter.setThemeTaxonomies;

@Service("catalogMetadataService")
@Slf4j
public class CatalogMetadataService extends AbstractMetadataService {

    @Autowired
    @Qualifier("catalogMetadataRepository")
    private CatalogMetadataRepository metadataRepository;

    @Override
    public Model retrieve(@Nonnull IRI uri) throws MetadataServiceException {
        final Model catalog = super.retrieve(uri);
        try {
            final List<IRI> themes = metadataRepository.getDatasetThemesForCatalog(uri);
            setThemeTaxonomies(catalog, uri, themes);
        }
        catch (MetadataRepositoryException exception) {
            log.error("Error retrieving the metadata");
            throw new MetadataServiceException(exception.getMessage());
        }
        return catalog;
    }

    @Override
    public Model store(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition
    ) throws MetadataServiceException {
        setThemeTaxonomies(metadata, uri, null);
        return super.store(metadata, uri, resourceDefinition);
    }

    @Override
    public Model update(
            Model metadata, IRI uri, ResourceDefinition resourceDefinition, boolean validate
    ) throws MetadataServiceException {
        setThemeTaxonomies(metadata, uri, null);
        return super.update(metadata, uri, resourceDefinition, validate);
    }
}
