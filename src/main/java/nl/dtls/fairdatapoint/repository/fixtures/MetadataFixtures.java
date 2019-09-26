package nl.dtls.fairdatapoint.repository.fixtures;

import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtls.fairdatapoint.service.metadata.MetadataServiceException;

public interface MetadataFixtures {
    void importDefaultFixtures(String fdpUrl) throws MetadataException, MetadataServiceException;
}
