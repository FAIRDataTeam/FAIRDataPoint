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
package nl.dtls.fairdatapoint.service.metadata.state;

import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateChangeDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.entity.metadata.MetadataState;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import nl.dtls.fairdatapoint.service.metadata.validator.MetadataStateValidator;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static nl.dtls.fairdatapoint.util.RdfUtil.getObjectsBy;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Service
public class MetadataStateService {

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private MetadataStateValidator metadataStateValidator;

    @Autowired
    private CurrentUserService currentUserService;

    public Metadata get(IRI metadataUri) {
        Optional<Metadata> oMetadata = metadataRepository.findByUri(metadataUri.stringValue());
        if (oMetadata.isEmpty()) {
            throw new ResourceNotFoundException(format("Metadata info '%s' was not found", metadataUri));
        }
        return oMetadata.get();
    }

    public MetaStateDTO getState(IRI metadataUri, Model model, ResourceDefinition rd) {
        // 1. Return null if user is not log in
        if (currentUserService.getCurrentUser().isEmpty()) {
            return null;
        }

        // 2. Get metadata info for current
        Optional<Metadata> oMetadata = metadataRepository.findByUri(metadataUri.stringValue());
        if (oMetadata.isEmpty()) {
            throw new ResourceNotFoundException(format("Metadata info '%s' was not found", metadataUri));
        }
        Metadata metadata = oMetadata.get();

        // 3. Get metadata info for children
        List<String> childrenUris = new ArrayList<>();
        for (ResourceDefinitionChild rdChild : rd.getChildren()) {
            IRI relationUri = i(rdChild.getRelationUri());
            for (org.eclipse.rdf4j.model.Value childUri : getObjectsBy(model, metadataUri, relationUri)) {
                childrenUris.add(childUri.stringValue());
            }
        }
        Map<String, MetadataState> children =
                metadataRepository.findByUriIn(childrenUris)
                        .stream()
                        .collect(Collectors.toMap(Metadata::getUri, Metadata::getState));

        // 4. Build response
        return new MetaStateDTO(
                metadata.getState(),
                children
        );
    }

    public void initState(IRI metadataUri) {
        Metadata metadata = new Metadata(null, metadataUri.stringValue(), MetadataState.DRAFT);
        metadataRepository.save(metadata);
    }

    public void modifyState(IRI metadataUri, MetaStateChangeDTO reqDto) {
        // 1. Get metadata info for current
        Optional<Metadata> oMetadata = metadataRepository.findByUri(metadataUri.stringValue());
        if (oMetadata.isEmpty()) {
            throw new ResourceNotFoundException(format("Metadata info '%s' was not found", metadataUri));
        }
        Metadata metadata = oMetadata.get();

        // 2. Validate
        metadataStateValidator.validate(reqDto, metadata);

        // 3. Update
        metadata.setState(reqDto.getCurrent());
        metadataRepository.save(metadata);
    }

}
