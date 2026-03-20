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
package nl.dtls.fairdatapoint.service.schema;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.schema.*;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaDraftRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class MetadataSchemaService {

    private static final String MSG_ERROR_PARENTS_PUBLISH =
            "Cannot publish as not all parents (via extends) are published";

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaDraftRepository metadataSchemaDraftRepository;

    @Autowired
    private MetadataSchemaMapper metadataSchemaMapper;

    @Autowired
    private MetadataSchemaValidator metadataSchemaValidator;

    @Autowired
    private ResourceDefinitionTargetClassesCache targetClassesCache;

    @Autowired
    private String persistentUrl;

    // ===============================================================================================
    // Schema drafts

    @PreAuthorize("hasRole('ADMIN')")
    public MetadataSchemaDraftDTO createSchemaDraft(MetadataSchemaChangeDTO reqDto) {
        final String uuid = UUID.randomUUID().toString();

        // Validate
        metadataSchemaValidator.validateAllExist(reqDto.getExtendsSchemaUuids());

        final MetadataSchemaDraft draft = metadataSchemaMapper.fromChangeDTO(reqDto, uuid);
        metadataSchemaDraftRepository.save(draft);
        return metadataSchemaMapper.toDraftDTO(draft, null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDraftDTO> getSchemaDraft(String uuid) {
        final Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        final Optional<MetadataSchema> oLatest = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        if (oDraft.isPresent()) {
            return oDraft.map(draft -> metadataSchemaMapper.toDraftDTO(draft, oLatest.orElse(null)));
        }
        return oLatest.map(latest -> metadataSchemaMapper.toDraftDTO(latest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDraftDTO> updateSchemaDraft(String uuid, MetadataSchemaChangeDTO reqDto) {
        final Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        final Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        final MetadataSchemaDraft baseDraft;
        // Check if present
        if (oDraft.isPresent()) {
            baseDraft = oDraft.get();
        }
        else {
            if (oSchema.isEmpty()) {
                return empty();
            }
            baseDraft = metadataSchemaMapper.toDraft(oSchema.get());
        }
        // Validate
        metadataSchemaValidator.validateAllExist(reqDto.getExtendsSchemaUuids());
        metadataSchemaValidator.validateNoExtendsCycle(uuid, reqDto.getExtendsSchemaUuids());
        // Save
        final MetadataSchemaDraft updatedDraft = metadataSchemaMapper.fromChangeDTO(reqDto, baseDraft);
        metadataSchemaDraftRepository.save(updatedDraft);
        return of(metadataSchemaMapper.toDraftDTO(updatedDraft, oSchema.orElse(null)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchemaDraft(String uuid) {
        final Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        if (oDraft.isEmpty()) {
            return false;
        }
        metadataSchemaDraftRepository.delete(oDraft.get());
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDTO> releaseDraft(String uuid, MetadataSchemaReleaseDTO reqDto) {
        final Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        // Check if present
        if (oDraft.isEmpty()) {
            return empty();
        }
        // Update
        final MetadataSchemaDraft draft = oDraft.get();
        final String versionUuid = UUID.randomUUID().toString();
        final MetadataSchema newLatest = metadataSchemaMapper.fromReleaseDTO(reqDto, draft, versionUuid);
        final Optional<MetadataSchema> oLatest = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        oLatest.map(MetadataSchema::getVersionUuid).ifPresent(newLatest::setPreviousVersionUuid);
        // Validate & Save
        metadataSchemaValidator.validateAllExist(newLatest.getExtendSchemas());
        // validate all parents are published if publishing
        if (reqDto.isPublished()) {
            final List<MetadataSchema> parents = resolveExtends(draft);
            if (!parents.stream().allMatch(MetadataSchema::isPublished)) {
                throw new ValidationException(MSG_ERROR_PARENTS_PUBLISH);
            }
        }
        if (oLatest.isPresent()) {
            final MetadataSchema oldLatest = oLatest.get();
            oldLatest.setLatest(false);
            metadataSchemaValidator.validate(newLatest, oldLatest);
            metadataSchemaRepository.save(oldLatest);
        }
        else {
            metadataSchemaValidator.validate(newLatest);
        }
        metadataSchemaRepository.save(newLatest);
        metadataSchemaDraftRepository.delete(draft);
        // Update cache
        targetClassesCache.computeCache();
        final List<MetadataSchema> versions = metadataSchemaRepository.findByUuid(uuid);
        final List<MetadataSchema> childs = metadataSchemaRepository.findAllByExtendSchemasContains(uuid);
        return of(metadataSchemaMapper.toDTO(newLatest, draft, versions, childs));
    }

    // ===============================================================================================
    // Schema versions

    private Optional<MetadataSchema> getByUuidAndVersion(String uuid, String version) {
        if (Objects.equals(version, "latest")) {
            return metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        }
        return metadataSchemaRepository.findByUuidAndVersionString(uuid, version);
    }

    public Optional<MetadataSchemaVersionDTO> getVersion(String uuid, String version) {
        return getByUuidAndVersion(uuid, version).map(metadataSchemaMapper::toVersionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaVersionDTO> updateVersion(
            String uuid, String version, MetadataSchemaUpdateDTO reqDto
    ) {
        final Optional<MetadataSchema> oSchema = getByUuidAndVersion(uuid, version);
        if (oSchema.isEmpty()) {
            return empty();
        }
        final MetadataSchema schema = oSchema.get();
        // validate all parents are published if publishing
        if (!schema.isPublished() && reqDto.isPublished()) {
            final List<MetadataSchema> parents = resolveExtends(schema);
            if (!parents.stream().allMatch(MetadataSchema::isPublished)) {
                throw new ValidationException(MSG_ERROR_PARENTS_PUBLISH);
            }
        }
        // result
        final MetadataSchema updatedSchema =
                metadataSchemaRepository.save(metadataSchemaMapper.fromUpdateDTO(schema, reqDto));
        return of(metadataSchemaMapper.toVersionDTO(updatedSchema));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteVersion(String uuid, String version) {
        final Optional<MetadataSchema> oSchema = getByUuidAndVersion(uuid, version);
        // Check if present
        if (oSchema.isEmpty()) {
            return false;
        }
        // Validate and fix links
        final MetadataSchema schema = oSchema.get();
        MetadataSchema previous = null;
        if (schema.getPreviousVersionUuid() != null) {
            previous = metadataSchemaRepository.findByVersionUuid(schema.getPreviousVersionUuid()).orElse(null);
        }
        final Optional<MetadataSchema> oNewer =
                metadataSchemaRepository.findByPreviousVersionUuid(schema.getVersionUuid());
        if (schema.isLatest()) {
            if (previous == null) {
                metadataSchemaValidator.validateNotUsed(uuid);
            }
            else {
                previous.setLatest(true);
                metadataSchemaValidator.validateNoExtendsCycle(uuid, previous.getExtendSchemas());
                metadataSchemaRepository.save(previous);
            }
        }
        else if (oNewer.isPresent()) {
            final MetadataSchema newer = oNewer.get();
            newer.setPreviousVersionUuid(previous == null ? null : previous.getVersionUuid());
            metadataSchemaRepository.save(newer);
        }
        metadataSchemaRepository.delete(schema);
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchemaFull(String uuid) {
        final List<MetadataSchema> schemas = metadataSchemaRepository.findByUuid(uuid);
        final Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        // Check if present
        if (schemas.isEmpty() && oDraft.isEmpty()) {
            return false;
        }
        // Validate
        metadataSchemaValidator.validateNotUsed(uuid);
        if (schemas.stream().anyMatch(schema -> schema.getType() == MetadataSchemaType.INTERNAL)) {
            throw new ValidationException("You can't delete INTERNAL Shape");
        }
        // Delete
        if (!schemas.isEmpty()) {
            metadataSchemaRepository.deleteAll(schemas);
        }
        oDraft.ifPresent(draft -> metadataSchemaDraftRepository.delete(draft));
        // Update cache
        targetClassesCache.computeCache();
        return true;
    }

    // ===============================================================================================
    // Reading schemas

    public List<MetadataSchemaDTO> getSchemasWithoutDrafts(boolean includeAbstract) {
        return metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .filter(schema -> includeAbstract || !schema.isAbstractSchema())
                .map(schema -> {
                    final List<MetadataSchema> versions =
                            metadataSchemaRepository.findByUuid(schema.getUuid());
                    final List<MetadataSchema> children =
                            metadataSchemaRepository.findAllByExtendSchemasContains(schema.getUuid());
                    return metadataSchemaMapper.toDTO(schema, null, versions, children);
                })
                .toList();
    }

    public List<MetadataSchemaDTO> getSchemasWithDrafts(boolean includeAbstract) {
        final Set<String> listedUuids = new HashSet<>();
        final Stream<MetadataSchemaDTO> schemas = metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .filter(schema -> includeAbstract || !schema.isAbstractSchema())
                .map(schema -> {
                    final List<MetadataSchema> versions = metadataSchemaRepository.findByUuid(schema.getUuid());
                    final List<MetadataSchema> children =
                            metadataSchemaRepository.findAllByExtendSchemasContains(schema.getUuid());
                    final Optional<MetadataSchemaDraft> oDraft =
                            metadataSchemaDraftRepository.findByUuid(schema.getUuid());
                    listedUuids.add(schema.getUuid());
                    return metadataSchemaMapper.toDTO(schema, oDraft.orElse(null), versions, children);
                });

        final Stream<MetadataSchemaDTO> drafts = metadataSchemaDraftRepository
                .findAll()
                .stream()
                .filter(draft -> {
                    return !listedUuids.contains(draft.getUuid()) && (includeAbstract || !draft.isAbstractSchema());
                })
                .map(draft -> {
                    return metadataSchemaMapper.toDTO(null, draft, Collections.emptyList(), Collections.emptyList());
                });
        return Stream.concat(schemas, drafts).toList();
    }

    public Optional<MetadataSchemaDTO> getSchemaByUuid(String uuid) {
        final Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        final Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        return oSchema.map(schema -> {
            final List<MetadataSchema> versions =
                    metadataSchemaRepository.findByUuid(schema.getUuid());
            final List<MetadataSchema> children =
                    metadataSchemaRepository.findAllByExtendSchemasContains(schema.getUuid());
            return metadataSchemaMapper.toDTO(schema, oDraft.orElse(null), versions, children);
        });
    }

    public Optional<Model> getSchemaContentByUuid(String uuid) {
        // TODO: cache (?)
        final Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        if (oSchema.isEmpty()) {
            return empty();
        }
        final MetadataSchema schema = oSchema.get();
        final List<MetadataSchema> schemas = resolveExtends(schema);
        return of(mergeSchemaDefinitions(schemas));
    }

    public Model getShaclFromSchemas() {
        return mergeSchemaDefinitions(metadataSchemaRepository.findAllByLatestIsTrue());
    }

    public Model getShaclFromSchemas(MetadataSchemaPreviewRequestDTO reqDto) {
        return getShaclFromSchemas(reqDto.getMetadataSchemaUuids());
    }

    public Model getShaclFromSchemas(List<String> metadataSchemaUuids) {
        final Set<String> schemaUuids = new HashSet<>(metadataSchemaUuids);
        final List<MetadataSchema> schemas = schemaUuids
                .stream()
                .map(schemaUuid -> {
                    return metadataSchemaRepository
                                    .findByUuidAndLatestIsTrue(schemaUuid)
                                    .orElseThrow(() -> raiseNotFound(schemaUuid));
                })
                .toList();
        return mergeSchemaDefinitions(resolveExtends(schemas));
    }

    private static ResourceNotFoundException raiseNotFound(String schemaUuid) {
        return new ResourceNotFoundException(format("Metadata schema '%s' not found", schemaUuid));
    }

    // ===============================================================================================
    // Extends and SHACL manipulation
    private List<MetadataSchema> resolveExtends(MetadataSchema schema) {
        return resolveExtends(List.of(schema));
    }

    private List<MetadataSchema> resolveExtends(MetadataSchemaDraft draft) {
        return resolveExtends(
                draft
                        .getExtendSchemas()
                        .stream()
                        .map(schemaUuid -> metadataSchemaRepository.findByUuidAndLatestIsTrue(schemaUuid).orElse(null))
                        .toList()
        );
    }

    private List<MetadataSchema> resolveExtends(List<MetadataSchema> schemas) {
        final Map<String, MetadataSchema> allSchemas = metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .collect(Collectors.toMap(MetadataSchema::getUuid, Function.identity()));
        final Set<String> addedSchemaUuids = new HashSet<>();
        final List<MetadataSchema> result = new ArrayList<>();
        schemas.forEach(schema -> {
            addedSchemaUuids.add(schema.getUuid());
            result.add(schema);
        });
        int index = 0;
        while (index < result.size()) {
            result.get(index).getExtendSchemas().forEach(extendUuid -> {
                if (!addedSchemaUuids.contains(extendUuid) && allSchemas.containsKey(extendUuid)) {
                    result.add(allSchemas.get(extendUuid));
                }
            });
            index++;
        }
        return result;
    }

    private Model mergeSchemaDefinitions(List<MetadataSchema> schemas) {
        final Model model = new LinkedHashModel();
        schemas.stream()
                .map(schema -> RdfIOUtil.read(schema.getDefinition(), ""))
                .forEach(schemaModel -> model.addAll(new ArrayList<>(schemaModel)));
        return model;
    }

    // ===============================================================================================
    // Importing and sharing

    public List<MetadataSchemaVersionDTO> getPublishedSchemas() {
        return metadataSchemaRepository
                .findAllByPublishedIsTrue()
                .stream()
                .map(schema -> metadataSchemaMapper.toPublishedVersionDTO(schema, persistentUrl))
                .toList();
    }

    private MetadataSchemaRemoteDTO toRemoteSchema(MetadataSchemaVersionDTO remoteDto) {
        final Optional<MetadataSchema> localVersion =
                metadataSchemaRepository.findByVersionUuid(remoteDto.getVersionUuid());
        final List<MetadataSchema> localSchemas =
                metadataSchemaRepository.findByUuid(remoteDto.getUuid());
        boolean isDirty = false;
        MetadataSchemaRemoteState status = MetadataSchemaRemoteState.NOT_IMPORTED;
        if (localVersion.isPresent()) {
            isDirty = !Objects.equals(localVersion.get().getDefinition(), remoteDto.getDefinition());
            // TODO: compare more
        }
        final boolean hasConflict = localSchemas
                .stream()
                .anyMatch(schema -> schema.getType() == MetadataSchemaType.CUSTOM);
        if (localVersion.isEmpty() && hasConflict) {
            status = MetadataSchemaRemoteState.CONFLICT;
        }
        else if (isDirty) {
            status = MetadataSchemaRemoteState.DIRTY;
        }
        else if (localVersion.isPresent()) {
            status = MetadataSchemaRemoteState.ALREADY_IMPORTED;
        }
        return MetadataSchemaRemoteDTO
                .builder()
                .schema(remoteDto)
                .status(status)
                .canImport(!hasConflict && localVersion.isEmpty())
                .build();
    }

    public List<MetadataSchemaRemoteDTO> getRemoteSchemas(String fdpUrl) {
        return MetadataSchemaRetrievalUtils
                .retrievePublishedMetadataSchemas(fdpUrl)
                .parallelStream()
                .map(this::toRemoteSchema)
                .toList();
    }

    public List<MetadataSchema> importSchemas(String schemaUuid, List<MetadataSchemaVersionDTO> remoteVersions) {
        // prepare and check local versions
        final Map<String, MetadataSchema> versions = metadataSchemaRepository
                .findByUuid(schemaUuid)
                .stream()
                .collect(toMap(MetadataSchema::getVersionUuid, Function.identity()));
        if (versions.values().stream().anyMatch(version -> version.getType() == MetadataSchemaType.CUSTOM)) {
            throw new ValidationException(format("Schema has CUSTOM version(s): %s", schemaUuid));
        }
        // update from remote
        remoteVersions.forEach(remoteVersion -> {
            if (versions.containsKey(remoteVersion.getVersionUuid())) {
                versions.put(remoteVersion.getVersionUuid(),
                        metadataSchemaMapper.fromRemoteVersion(remoteVersion,
                                versions.get(remoteVersion.getVersionUuid())));
            }
            else {
                versions.put(remoteVersion.getVersionUuid(), metadataSchemaMapper.fromRemoteVersion(remoteVersion));
            }
        });
        // fix versions chain
        final Map<String, MetadataSchema> versionMap =
                versions.values().stream().collect(toMap(MetadataSchema::getVersionString, Function.identity()));
        final List<String> versionsSorted =
                versionMap.keySet().stream().map(SemVer::new).sorted().map(SemVer::toString).toList();
        String previousVersionUuid = null;
        for (String version : versionsSorted) {
            versionMap.get(version).setPreviousVersionUuid(previousVersionUuid);
            versionMap.get(version).setLatest(false);
            previousVersionUuid = versionMap.get(version).getVersionUuid();
        }
        versionMap.get(versionsSorted.get(versionsSorted.size() - 1)).setLatest(true);
        return versionMap.values().stream().toList();
    }

    public List<MetadataSchemaVersionDTO> importSchemas(@Valid List<MetadataSchemaVersionDTO> reqDtos) {
        // Validate
        reqDtos.forEach(dto -> metadataSchemaValidator.validate(dto));
        final List<MetadataSchema> localLatestSchemas = metadataSchemaRepository.findAllByLatestIsTrue();
        final Map<String, List<MetadataSchemaVersionDTO>> schemas =
                reqDtos.stream().collect(groupingBy(MetadataSchemaVersionDTO::getUuid));
        final Set<String> toBePresentUuids = localLatestSchemas
                .stream()
                .map(MetadataSchema::getUuid)
                .collect(Collectors.toSet());
        toBePresentUuids.addAll(schemas.keySet());
        reqDtos.forEach(dto -> {
            if (dto.getExtendsSchemaUuids().stream().anyMatch(uuid -> !toBePresentUuids.contains(uuid))) {
                throw new ValidationException("Missing schema for extends relation");
            }
        });
        final List<MetadataSchema> toSave = new ArrayList<>();
        schemas.forEach((schemaUuid, versions) -> {
            toSave.addAll(importSchemas(schemaUuid, versions));
        });
        metadataSchemaRepository.saveAll(toSave);
        return reqDtos
                .parallelStream()
                .map(version -> metadataSchemaRepository.findByVersionUuid(version.getVersionUuid()).orElse(null))
                .filter(Objects::nonNull)
                .map(metadataSchemaMapper::toVersionDTO)
                .toList();
    }

    private List<MetadataSchemaRemoteDTO> checkForUpdates(String fdpUrl) {
        try {
            final Map<String, List<MetadataSchemaVersionDTO>> remoteSchemas = MetadataSchemaRetrievalUtils
                    .retrievePublishedMetadataSchemas(fdpUrl)
                    .stream()
                    .collect(groupingBy(MetadataSchemaVersionDTO::getUuid));
            final List<MetadataSchemaVersionDTO> updates = new ArrayList<>();
            remoteSchemas.forEach((schemaUuid, remoteVersions) -> {
                final List<MetadataSchema> localVersions = metadataSchemaRepository.findByUuid(schemaUuid);
                final boolean hasCustom = localVersions
                        .stream()
                        .anyMatch(schema -> schema.getType() == MetadataSchemaType.CUSTOM);
                final boolean allImportedFromThis = localVersions
                        .stream()
                        .allMatch(schema -> schema.getImportedFrom().equals(fdpUrl));
                if (!hasCustom && allImportedFromThis && !localVersions.isEmpty()) {
                    final Set<String> localVersionUuids = localVersions
                            .stream()
                            .map(MetadataSchema::getVersionUuid).collect(Collectors.toSet());
                    updates.addAll(
                            remoteVersions
                                    .stream()
                                    .filter(version -> !localVersionUuids.contains(version.getVersionUuid()))
                                    .toList()
                    );
                }
            });
            return updates
                    .stream()
                    .map(schemaVersion -> {
                        return MetadataSchemaRemoteDTO.builder()
                                        .canImport(true)
                                        .schema(schemaVersion)
                                        .status(MetadataSchemaRemoteState.NOT_IMPORTED)
                                        .build();
                    })
                    .toList();
        }
        catch (Exception exception) {
            log.warn(format("Failed to check for updates from %s: %s", fdpUrl, exception.getMessage()));
            return Collections.emptyList();
        }
    }

    public List<MetadataSchemaRemoteDTO> checkForUpdates() {
        final Set<String> importSources = metadataSchemaRepository
                .findAllByImportedFromIsNotNull()
                .stream()
                .map(MetadataSchema::getImportedFrom)
                .collect(Collectors.toSet());
        return importSources
                .stream()
                .map(this::checkForUpdates)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
