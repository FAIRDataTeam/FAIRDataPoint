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
package nl.dtls.fairdatapoint.service.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.database.db.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    private static final String MSG_ADD = "Adding OpenAPI paths: {}";
    private static final String MSG_REMOVE = "Removing OpenAPI paths: {}";

    private final OpenAPI openAPI;

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private Paths getGenericPaths() {
        return (Paths) openAPI.getExtensions().get("fdpGenericPaths");
    }

    private boolean isRelatedToResourceDefinition(PathItem pathItem, ResourceDefinition definition) {
        final UUID rdUuid = (UUID) pathItem.getExtensions().getOrDefault("fdpResourceDefinition", "");
        return rdUuid.equals(definition.getUuid());
    }

    public void updateTags(List<ResourceDefinition> resourceDefinitions) {
        openAPI.setTags(OpenApiTagsUtils.listTags(resourceDefinitions));
    }

    public void removeGenericPaths(ResourceDefinition definition) {
        final Paths fdpGenericPaths = getGenericPaths();
        final Set<String> toRemove = fdpGenericPaths
                .entrySet()
                .stream()
                .filter(item -> isRelatedToResourceDefinition(item.getValue(), definition))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        log.info(MSG_REMOVE, toRemove);
        openAPI.getPaths().keySet().removeAll(toRemove);
        fdpGenericPaths.keySet().removeAll(toRemove);
        // Update tags
        updateTags(resourceDefinitionRepository.findAll());
    }

    public void removeAllGenericPaths() {
        final Paths fdpGenericPaths = getGenericPaths();
        log.info(MSG_REMOVE, fdpGenericPaths.keySet());
        openAPI.getPaths().keySet().removeAll(fdpGenericPaths.keySet());
        fdpGenericPaths.clear();
    }

    public void updateGenericPaths(ResourceDefinition definition) {
        final Paths fdpGenericPaths = getGenericPaths();
        // Cleanup
        removeGenericPaths(definition);
        // Generate
        OpenApiGenerator.generatePathsForResourceDefinition(fdpGenericPaths, definition);
        // Apply
        log.info(MSG_ADD, fdpGenericPaths.keySet());
        openAPI.getPaths().putAll(fdpGenericPaths);
        // Update tags
        updateTags(resourceDefinitionRepository.findAll());
    }

    public void updateAllGenericPaths() {
        final Paths fdpGenericPaths = getGenericPaths();
        // Cleanup
        removeAllGenericPaths();
        // Re-generate from Resource Definitions
        final List<ResourceDefinition> resourceDefinitions = resourceDefinitionRepository.findAll();
        resourceDefinitions.forEach(def -> OpenApiGenerator.generatePathsForResourceDefinition(fdpGenericPaths, def));
        // Apply
        log.info(MSG_ADD, fdpGenericPaths.keySet());
        openAPI.getPaths().putAll(fdpGenericPaths);
        updateTags(resourceDefinitions);
    }

    @PostConstruct
    public void init() {
        log.info("Initializing OpenAPI with generic paths");
        updateAllGenericPaths();
    }
}
