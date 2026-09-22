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

import org.fairdatateam.fairdatapoint.rdf.metadata.dto.MetaStateChangeDTO;
import org.fairdatateam.fairdatapoint.rdf.metadata.dto.MetaStateDTO;
import org.fairdatateam.fairdatapoint.common.error.ResourceNotFoundException;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionChild;
import org.fairdatateam.fairdatapoint.security.CurrentUserProvider;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.fairdatateam.fairdatapoint.rdf.RdfUtil.getObjectsBy;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;

@Service
public class MetadataStateService {

    private static final String MSG_NOT_FOUND = "Metadata info '%s' was not found";

    @Autowired
    private MetadataStateRepository metadataStateRepository;

    @Autowired
    private MetadataStateValidator metadataStateValidator;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    public MetadataState get(IRI metadataUri) {
        return metadataStateRepository
                .findByUri(metadataUri)
                .orElseThrow(() -> new ResourceNotFoundException(format(MSG_NOT_FOUND, metadataUri)));
    }

    /**
     * States of several records at once; records without a state are absent from the map.
     *
     * @param metadataUris the records to look up
     * @return the state of each record that has one
     */
    public Map<IRI, MetadataState> getStates(Collection<IRI> metadataUris) {
        return metadataStateRepository.findByUris(metadataUris);
    }

    public MetaStateDTO getState(IRI metadataUri, Model model, ResourceDefinition definition) {
        // 1. Return null if user is not log in
        if (currentUserProvider.getCurrentUser().isEmpty()) {
            return null;
        }

        // 2. Get metadata info for current
        final MetadataState state = get(metadataUri);

        // 3. Get metadata info for children
        final List<IRI> childrenUris = new ArrayList<>();
        for (ResourceDefinitionChild rdChild : definition.getChildren()) {
            final IRI relationUri = i(rdChild.getRelationUri());
            // children are Values in general; only IRIs can have a state, blank nodes and
            // literals never do, so they are skipped rather than turned into an invalid IRI
            getObjectsBy(model, metadataUri, relationUri)
                    .stream()
                    .filter(IRI.class::isInstance)
                    .map(IRI.class::cast)
                    .forEach(childrenUris::add);
        }
        final Map<String, MetadataState> children = metadataStateRepository
                .findByUris(childrenUris)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().stringValue(), Map.Entry::getValue));

        // 4. Build response
        return new MetaStateDTO(
                state,
                children
        );
    }

    public void initState(IRI metadataUri) {
        metadataStateRepository.save(metadataUri, MetadataState.DRAFT);
    }

    public void modifyState(IRI metadataUri, MetaStateChangeDTO reqDto) {
        // 1. Get metadata info for current
        final Optional<MetadataState> oState = metadataStateRepository.findByUri(metadataUri);
        if (oState.isEmpty()) {
            throw new ResourceNotFoundException(format(MSG_NOT_FOUND, metadataUri));
        }

        // 2. Validate
        metadataStateValidator.validate(reqDto, oState.get());

        // 3. Update
        metadataStateRepository.save(metadataUri, reqDto.getCurrent());
    }

    public void deleteState(IRI metadataUri) {
        metadataStateRepository.delete(metadataUri);
    }

}
