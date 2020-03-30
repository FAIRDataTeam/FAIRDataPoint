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

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.database.rdf.repository.catalog.CatalogMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.common.AbstractMetadataService;
import nl.dtls.fairdatapoint.service.metadata.common.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;

import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.getThemeTaxonomies;
import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.*;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;

@Service("catalogMetadataService")
@Slf4j
public class CatalogMetadataService extends AbstractMetadataService {

    @Value("${instance.url}")
    private String instanceUrl;

    @Autowired
    private CatalogMetadataRepository catalogMetadataRepository;

    @Override
    public Model retrieve(@Nonnull IRI uri) throws MetadataServiceException, ResourceNotFoundException {
        Model catalog = super.retrieve(uri);
        try {
            List<IRI> themes = catalogMetadataRepository.getDatasetThemesForCatalog(uri);
            Set<IRI> set = new TreeSet<>(Comparator.comparing(IRI::toString));
            set.addAll(getThemeTaxonomies(catalog));
            set.addAll(themes);
            setThemeTaxonomies(catalog, uri, new ArrayList<>(set));
        } catch (MetadataRepositoryException ex) {
            log.error("Error retrieving the metadata");
            throw new MetadataServiceException(ex.getMessage());
        }
        return catalog;
    }

    @Override
    public void store(Model metadata, IRI uri, ResourceDefinition rd) throws MetadataServiceException {
        setParent(metadata, uri, i(instanceUrl));
        super.store(metadata, uri, rd);
    }

}
