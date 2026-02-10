/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.service.metadata.container;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.fairdatapoint.database.rdf.repository.RepositoryMode;
import org.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import org.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import org.fairdatapoint.entity.exception.ValidationException;
import org.fairdatapoint.service.metadata.common.MetadataService;
import org.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import org.fairdatapoint.service.metadata.state.MetadataStateService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.fairdatapoint.util.RdfUtil.getObjectsBy;
import static org.fairdatapoint.util.ValueFactoryHelper.i;

@Service
public class ContainerService {

    private final GenericMetadataRepository metadataRepository;

    private final MetadataServiceFactory metadataServiceFactory;

    private final MetadataStateService metadataStateService;

    private final String persistentUrl;

    /**
     * Constructor (autowiring)
     */
    public ContainerService(
            GenericMetadataRepository metadataRepository,
            MetadataServiceFactory metadataServiceFactory,
            MetadataStateService metadataStateService, String persistentUrl
    ) {
        this.metadataRepository = metadataRepository;
        this.metadataServiceFactory = metadataServiceFactory;
        this.metadataStateService = metadataStateService;
        this.persistentUrl = persistentUrl;
    }

    public List<Value> getContainedValues(
            String childPrefix,
            String urlPrefix,
            IRI entityUri,
            IRI relationUri,
            RepositoryMode repositoryMode
    ) throws MetadataServiceException, MetadataRepositoryException {

        // 4.1 Get all titles for sort
        final Map<String, String> titles = metadataRepository.findChildTitles(
                entityUri, relationUri, RepositoryMode.COMBINED);

        // 4.2 Get all children sorted
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);
        final Model entity = metadataService.retrieve(entityUri, repositoryMode);

        //  https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/model/Value.html
        final List<Value> values = getObjectsBy(entity, entityUri, relationUri);
        return values.stream()
                // todo: can we replace Value by MemIRI?
                // todo: this uses both Value.toString() and Value.stringValue(). should we only use tha latter?
                .filter(childUri -> getResourceNameForChild(childUri.toString()).equals(childPrefix))
                // although entity may be published, it could contain URIs of draft resources
                .filter(childUri -> {
                    try {
                        return repositoryMode == RepositoryMode.COMBINED
                                || metadataStateService.isPublished(i(childUri.stringValue()));
                    }
                    catch (MetadataServiceException exc) {
                        return false;
                    }
                })
                // todo: verify if Comparator implementation is equivalent to the original
                .sorted(Comparator.comparing(
                        childUri -> titles.get(childUri.toString()),
                        String.CASE_INSENSITIVE_ORDER)
                )
                .toList();
    }

    private String getResourceNameForChild(String url) {
        final String[] parts = url
                .replace(persistentUrl, "")
                .split("/");

        if (parts.length < 2) {
            throw new ValidationException("Unsupported URL");
        }

        // If URL is a repository -> return empty string
        if (parts[1].equals("page")) {
            return "";
        }

        return parts[1];
    }
}
