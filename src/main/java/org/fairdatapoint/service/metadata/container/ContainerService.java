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

        final List<Value> objects = getObjectsBy(entity, entityUri, relationUri);
        return objects.stream()
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
                .sorted((value1, value2) -> {
                    final String title1 = titles.get(value1.toString());
                    final String title2 = titles.get(value2.toString());
                    if (title1 == null) {
                        return -1;
                    }
                    if (title2 == null) {
                        return 1;
                    }
                    return title1.compareToIgnoreCase(title2);
                })
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
