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
package org.fairdatapoint.service.schema;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.api.dto.schema.*;
import org.fairdatapoint.database.db.repository.MetadataSchemaExtensionRepository;
import org.fairdatapoint.database.db.repository.MetadataSchemaRepository;
import org.fairdatapoint.database.db.repository.MetadataSchemaUsageRepository;
import org.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import org.fairdatapoint.entity.exception.ResourceNotFoundException;
import org.fairdatapoint.entity.exception.ValidationException;
import org.fairdatapoint.entity.resource.MetadataSchemaUsage;
import org.fairdatapoint.entity.schema.*;
import org.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import org.fairdatapoint.util.RdfIOUtil;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataSchemaService {

    private static final String MSG_ERROR_PARENTS_PUBLISH =
            "Cannot publish as not all parents (via extends) are published";

    private final MetadataSchemaRepository schemaRepository;

    private final MetadataSchemaVersionRepository versionRepository;

    private final MetadataSchemaUsageRepository usageRepository;

    private final MetadataSchemaExtensionRepository extensionRepository;

    private final MetadataSchemaMapper metadataSchemaMapper;

    private final MetadataSchemaValidator metadataSchemaValidator;

    private final ResourceDefinitionTargetClassesCache targetClassesCache;

    private final String persistentUrl;

    private final EntityManager entityManager;

    // ===============================================================================================
    // Schema drafts

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MetadataSchemaDraftDTO createSchemaDraft(MetadataSchemaChangeDTO reqDto) {
        // Validate
        metadataSchemaValidator.validateAllExist(reqDto.getExtendsSchemaUuids());
        final List<MetadataSchema> extendedSchemas = getAll(reqDto.getExtendsSchemaUuids());

        // create new MetadataSchema (bundle)
        final MetadataSchema schema = schemaRepository.saveAndFlush(metadataSchemaMapper.newSchema());
        entityManager.refresh(schema);
        // create new MetadataSchemaVersion (draft)
        final MetadataSchemaVersion draft = versionRepository
                .saveAndFlush(metadataSchemaMapper.fromChangeDTO(reqDto, schema));

        // create Extensions
        draft.setExtensions(new ArrayList<>());
        for (int index = 0; index < extendedSchemas.size(); index++) {
            draft.getExtensions().add(metadataSchemaMapper.newExtension(draft, extendedSchemas.get(index), index));
        }
        extensionRepository.saveAllAndFlush(draft.getExtensions());

        return metadataSchemaMapper.toDraftDTO(draft);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDraftDTO> getSchemaDraft(UUID uuid) {
        final Optional<MetadataSchemaVersion> oDraft = versionRepository.getDraftBySchemaUuid(uuid);
        final Optional<MetadataSchemaVersion> oLatest = versionRepository.getLatestBySchemaUuid(uuid);
        if (oDraft.isPresent()) {
            return oDraft.map(metadataSchemaMapper::toDraftDTO);
        }
        return oLatest.map(metadataSchemaMapper::toNewDraftDTO);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDraftDTO> updateSchemaDraft(UUID uuid, MetadataSchemaChangeDTO reqDto) {
        final Optional<MetadataSchemaVersion> oDraft = versionRepository.getDraftBySchemaUuid(uuid);
        final Optional<MetadataSchemaVersion> oLatest = versionRepository.getLatestBySchemaUuid(uuid);
        final MetadataSchemaVersion baseDraft;
        // Check if present
        if (oDraft.isPresent()) {
            baseDraft = oDraft.get();
        }
        else {
            if (oLatest.isEmpty()) {
                return empty();
            }
            baseDraft = metadataSchemaMapper.toDraft(oLatest.get());
        }
        // Validate
        metadataSchemaValidator.validateAllExist(reqDto.getExtendsSchemaUuids());
        metadataSchemaValidator.validateNoExtendsCycle(uuid, reqDto.getExtendsSchemaUuids());
        final List<MetadataSchema> extendedSchemas = getAll(reqDto.getExtendsSchemaUuids());
        // Save
        final MetadataSchemaVersion updatedDraft = versionRepository.saveAndFlush(
                metadataSchemaMapper.fromChangeDTO(reqDto, baseDraft)
        );

        // Update schema extensions
        extensionRepository.deleteAll(updatedDraft.getExtensions());
        extensionRepository.saveAllAndFlush(extendedSchemas.stream().map(schema -> {
            return metadataSchemaMapper.newExtension(updatedDraft, schema, 0);
        }).toList());

        entityManager.refresh(updatedDraft);

        return of(metadataSchemaMapper.toDraftDTO(updatedDraft));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchemaDraft(UUID uuid) {
        final Optional<MetadataSchemaVersion> oDraft = versionRepository.getDraftBySchemaUuid(uuid);
        if (oDraft.isEmpty()) {
            return false;
        }
        final MetadataSchemaVersion draft = oDraft.get();
        final boolean isOnlyVersion = versionRepository.getBySchemaUuid(draft.getSchema().getUuid()).size() == 1;
        entityManager.clear();

        if (isOnlyVersion) {
            schemaRepository.delete(draft.getSchema());
        }
        else {
            versionRepository.delete(draft);
        }
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDTO> releaseDraft(UUID uuid, MetadataSchemaReleaseDTO reqDto) {
        final Optional<MetadataSchemaVersion> oDraft = versionRepository.getDraftBySchemaUuid(uuid);
        // Check if present
        if (oDraft.isEmpty()) {
            return empty();
        }
        // Update
        final MetadataSchemaVersion newLatest = metadataSchemaMapper.fromReleaseDTO(reqDto, oDraft.get());
        final Optional<MetadataSchemaVersion> oLatest = Optional.ofNullable(newLatest.getPreviousVersion());
        // validate all parents are published if publishing
        if (reqDto.isPublished()) {
            final List<MetadataSchemaVersion> parents = resolveExtends(newLatest);
            if (!parents.stream().allMatch(MetadataSchemaVersion::isPublished)) {
                throw new ValidationException(MSG_ERROR_PARENTS_PUBLISH);
            }
        }
        if (oLatest.isPresent()) {
            final MetadataSchemaVersion oldLatest = oLatest.get();
            oldLatest.setState(MetadataSchemaState.LEGACY);
            metadataSchemaValidator.validate(newLatest, oldLatest);
            versionRepository.save(oldLatest);
        }
        else {
            metadataSchemaValidator.validate(newLatest);
        }
        versionRepository.save(newLatest);
        // Update cache
        targetClassesCache.computeCache();
        final List<MetadataSchemaVersion> versions = versionRepository.getBySchemaUuid(uuid);
        final List<MetadataSchemaVersion> childs = extensionRepository
                .findByExtendedMetadataSchema(newLatest.getSchema())
                .stream()
                .map(MetadataSchemaExtension::getMetadataSchemaVersion)
                .toList();
        return of(metadataSchemaMapper.toDTO(newLatest, null, versions, childs));
    }

    // ===============================================================================================
    // Schema versions

    private Optional<MetadataSchemaVersion> getByUuidAndVersion(UUID uuid, String version) {
        if (Objects.equals(version, "latest")) {
            return versionRepository.getLatestBySchemaUuid(uuid);
        }
        return versionRepository.getBySchemaUuidAndVersion(uuid, version);
    }

    public Optional<MetadataSchemaVersionDTO> getVersion(UUID uuid, String version) {
        return getByUuidAndVersion(uuid, version).map(metadataSchemaMapper::toVersionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaVersionDTO> updateVersion(
            UUID uuid, String version, MetadataSchemaUpdateDTO reqDto
    ) {
        final Optional<MetadataSchemaVersion> oSchema = getByUuidAndVersion(uuid, version);
        if (oSchema.isEmpty()) {
            return empty();
        }
        final MetadataSchemaVersion schema = oSchema.get();
        // validate all parents are published if publishing
        if (!schema.isPublished() && reqDto.isPublished()) {
            final List<MetadataSchemaVersion> parents = resolveExtends(schema);
            if (!parents.stream().allMatch(MetadataSchemaVersion::isPublished)) {
                throw new ValidationException(MSG_ERROR_PARENTS_PUBLISH);
            }
        }
        // result
        final MetadataSchemaVersion updatedSchema =
                versionRepository.saveAndFlush(metadataSchemaMapper.fromUpdateDTO(schema, reqDto));
        return of(metadataSchemaMapper.toVersionDTO(updatedSchema));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteVersion(UUID uuid, String version) {
        final Optional<MetadataSchemaVersion> oSchema = getByUuidAndVersion(uuid, version);
        // Check if present
        if (oSchema.isEmpty()) {
            return false;
        }
        // Validate and fix links
        final MetadataSchemaVersion schema = oSchema.get();
        final boolean isOnlyVersion = versionRepository.getBySchemaUuid(schema.getSchema().getUuid()).size() == 1;
        if (schema.isLatest()) {
            if (schema.getPreviousVersion() == null) {
                metadataSchemaValidator.validateNotUsed(schema.getSchema());
            }
            else {
                schema.getPreviousVersion().setState(MetadataSchemaState.LATEST);
                metadataSchemaValidator.validateNoExtendsCycle(uuid,
                        schema.getExtensions()
                                .stream()
                                .map(extension -> extension.getExtendedMetadataSchema().getUuid())
                                .toList());
                versionRepository.save(schema.getPreviousVersion());
            }
        }
        if (schema.getNextVersion() != null) {
            schema.getNextVersion().setPreviousVersion(schema.getPreviousVersion());
            versionRepository.save(schema.getNextVersion());
        }
        entityManager.flush();
        entityManager.clear();

        if (isOnlyVersion) {
            schemaRepository.delete(schema.getSchema());
        }
        else {
            versionRepository.delete(schema);
        }
        return true;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchemaFull(UUID uuid) {
        final Optional<MetadataSchema> oSchema = schemaRepository.findByUuid(uuid);
        // Check if present
        if (oSchema.isEmpty()) {
            return false;
        }
        final MetadataSchema schema = oSchema.get();
        // Validate
        metadataSchemaValidator.validateNotUsed(schema);
        if (schema.getVersions().stream().anyMatch(version -> version.getType() == MetadataSchemaType.INTERNAL)) {
            throw new ValidationException("You can't delete INTERNAL Shape");
        }
        // Delete
        schemaRepository.delete(schema);
        entityManager.flush();
        // Update cache
        targetClassesCache.computeCache();
        return true;
    }

    // ===============================================================================================
    // Reading schemas

    public List<MetadataSchemaDTO> getSchemasWithoutDrafts(boolean includeAbstract) {
        return versionRepository
                .findAllByState(MetadataSchemaState.LATEST)
                .stream()
                .filter(schema -> includeAbstract || !schema.isAbstractSchema())
                .map(schema -> {
                    final List<MetadataSchemaVersion> versions =
                            schema.getSchema().getVersions();
                    final List<MetadataSchemaVersion> children =
                            extensionRepository
                                    .findByExtendedMetadataSchema(schema.getSchema())
                                    .stream().map(MetadataSchemaExtension::getMetadataSchemaVersion)
                                    .toList();
                    return metadataSchemaMapper.toDTO(schema, null, versions, children);
                })
                .toList();
    }

    public List<MetadataSchemaDTO> getSchemasWithDrafts(boolean includeAbstract) {
        final Set<UUID> listedUuids = new HashSet<>();
        final Stream<MetadataSchemaDTO> schemas = versionRepository
                .findAllByState(MetadataSchemaState.LATEST)
                .stream()
                .filter(schema -> includeAbstract || !schema.isAbstractSchema())
                .map(schema -> {
                    final List<MetadataSchemaVersion> versions =
                            schema.getSchema().getVersions();
                    final List<MetadataSchemaVersion> children =
                            extensionRepository
                                    .findByExtendedMetadataSchema(schema.getSchema())
                                    .stream().map(MetadataSchemaExtension::getMetadataSchemaVersion)
                                    .toList();
                    final Optional<MetadataSchemaVersion> oDraft =
                            versionRepository.getDraftBySchemaUuid(schema.getSchema().getUuid());
                    listedUuids.add(schema.getSchema().getUuid());
                    return metadataSchemaMapper.toDTO(schema, oDraft.orElse(null), versions, children);
                });

        final Stream<MetadataSchemaDTO> drafts = versionRepository
                .findAllByState(MetadataSchemaState.DRAFT)
                .stream()
                .filter(draft -> {
                    return !listedUuids.contains(draft.getSchema().getUuid())
                            && (includeAbstract || !draft.isAbstractSchema());
                })
                .map(draft -> {
                    return metadataSchemaMapper.toDTO(null, draft, Collections.emptyList(), Collections.emptyList());
                });
        return Stream.concat(schemas, drafts).toList();
    }

    public Optional<MetadataSchemaDTO> getSchemaByUuid(UUID uuid) {
        final Optional<MetadataSchemaVersion> oSchema = versionRepository.getLatestBySchemaUuid(uuid);
        final Optional<MetadataSchemaVersion> oDraft = versionRepository.getDraftBySchemaUuid(uuid);
        return oSchema.map(schema -> {
            final List<MetadataSchemaVersion> versions =
                    schema.getSchema().getVersions();
            final List<MetadataSchemaVersion> children =
                    extensionRepository
                            .findByExtendedMetadataSchema(schema.getSchema())
                            .stream().map(MetadataSchemaExtension::getMetadataSchemaVersion)
                            .toList();
            return metadataSchemaMapper.toDTO(schema, oDraft.orElse(null), versions, children);
        });
    }

    public Optional<Model> getSchemaContentByUuid(UUID uuid) {
        // TODO: cache (?)
        final Optional<MetadataSchemaVersion> oSchema = versionRepository.getLatestBySchemaUuid(uuid);
        if (oSchema.isEmpty()) {
            return empty();
        }
        final MetadataSchemaVersion schema = oSchema.get();
        final List<MetadataSchemaVersion> schemas = resolveExtends(schema);
        return of(mergeSchemaDefinitions(schemas));
    }

    public Model getShaclFromSchemas() {
        return mergeSchemaDefinitions(versionRepository.findAllByState(MetadataSchemaState.LATEST));
    }

    public Model getShaclFromSchemas(MetadataSchemaPreviewRequestDTO reqDto) {
        return getShaclFromSchemas(reqDto.getMetadataSchemaUuids());
    }

    public Model getShaclFromSchemas(List<UUID> metadataSchemaUuids) {
        final Set<UUID> schemaUuids = new HashSet<>(metadataSchemaUuids);
        final List<MetadataSchemaVersion> schemas = schemaUuids
                .stream()
                .map(schemaUuid -> {
                    return versionRepository
                                    .getLatestBySchemaUuid(schemaUuid)
                                    .orElseThrow(() -> raiseNotFound(schemaUuid));
                })
                .toList();
        return mergeSchemaDefinitions(resolveExtends(schemas));
    }

    public Model getShaclFromSchemaUsages(List<MetadataSchemaUsage> usages) {
        return getShaclFromSchemas(usages.stream().map(usage -> usage.getUsedMetadataSchema().getUuid()).toList());
    }

    private static ResourceNotFoundException raiseNotFound(UUID schemaUuid) {
        return new ResourceNotFoundException(format("Metadata schema '%s' not found", schemaUuid));
    }

    // ===============================================================================================
    // Extends and SHACL manipulation
    private List<MetadataSchemaVersion> resolveExtends(MetadataSchemaVersion draft) {
        return resolveExtends(
                draft
                        .getExtensions()
                        .stream()
                        .map(extension -> {
                            return versionRepository
                                    .getLatestBySchemaUuid(extension.getExtendedMetadataSchema().getUuid())
                                    .orElse(null);
                        })
                        .toList()
        );
    }

    private List<MetadataSchemaVersion> resolveExtends(List<MetadataSchemaVersion> schemas) {
        final Map<UUID, MetadataSchemaVersion> allSchemas = versionRepository
                .findAllByState(MetadataSchemaState.LATEST)
                .stream()
                .collect(Collectors.toMap(MetadataSchemaVersion::extractSchemaUuid, Function.identity()));
        final Set<UUID> addedSchemaUuids = new HashSet<>();
        final List<MetadataSchemaVersion> result = new ArrayList<>();
        schemas.forEach(schema -> {
            addedSchemaUuids.add(schema.extractSchemaUuid());
            result.add(schema);
        });
        int index = 0;
        while (index < result.size()) {
            result.get(index).getExtensions().forEach(extension -> {
                final UUID extendUuid = extension.getExtendedMetadataSchema().getUuid();
                if (!addedSchemaUuids.contains(extendUuid) && allSchemas.containsKey(extendUuid)) {
                    result.add(allSchemas.get(extendUuid));
                }
            });
            index++;
        }
        return result;
    }

    private Model mergeSchemaDefinitions(List<MetadataSchemaVersion> schemas) {
        final Model model = new LinkedHashModel();
        schemas.stream()
                .map(schema -> RdfIOUtil.read(schema.getDefinition(), ""))
                .forEach(schemaModel -> model.addAll(new ArrayList<>(schemaModel)));
        return model;
    }

    // ===============================================================================================
    // Importing and sharing

    public List<MetadataSchemaVersionDTO> getPublishedSchemas() {
        return versionRepository
                .findAllByPublishedIsTrue()
                .stream()
                .map(schema -> metadataSchemaMapper.toPublishedVersionDTO(schema, persistentUrl))
                .toList();
    }

    private MetadataSchemaRemoteDTO toRemoteSchema(MetadataSchemaVersionDTO remoteDto) {
        final Optional<MetadataSchemaVersion> localVersion =
                versionRepository.findByUuid(remoteDto.getVersionUuid());
        final List<MetadataSchemaVersion> localSchemas =
                versionRepository.getBySchemaUuid(remoteDto.getUuid());
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

    public void importVersions(UUID schemaUuid, List<MetadataSchemaVersionDTO> remoteVersions) {
        // prepare and check local versions
        final MetadataSchema schema = schemaRepository.findByUuid(schemaUuid)
                .orElseThrow(() -> raiseNotFound(schemaUuid));
        final Map<UUID, MetadataSchemaVersion> versions = versionRepository
                .getBySchemaUuid(schemaUuid)
                .stream()
                .collect(toMap(MetadataSchemaVersion::getUuid, Function.identity()));
        if (versions.values().stream().anyMatch(version -> version.getType() == MetadataSchemaType.CUSTOM)) {
            throw new ValidationException(format("Schema has CUSTOM version(s): %s", schemaUuid));
        }
        // update from remote
        remoteVersions.forEach(remoteVersion -> {
            if (versions.containsKey(remoteVersion.getVersionUuid())) {
                versions.put(remoteVersion.getVersionUuid(),
                        metadataSchemaMapper.fromRemoteVersion(schema, remoteVersion,
                                versions.get(remoteVersion.getVersionUuid())));
            }
            else {
                versions.put(remoteVersion.getVersionUuid(),
                        metadataSchemaMapper.fromRemoteVersion(schema, remoteVersion));
            }
        });
        // fix versions chain
        final Map<String, MetadataSchemaVersion> versionMap =
                versions.values().stream().collect(toMap(MetadataSchemaVersion::getVersion, Function.identity()));
        final List<String> versionsSorted =
                versionMap.keySet().stream().map(SemVer::new).sorted().map(SemVer::toString).toList();
        MetadataSchemaVersion previousVersion = null;
        final List<MetadataSchemaVersion> schemaVersions = new ArrayList<>();
        for (String version : versionsSorted) {
            versionMap.get(version).setPreviousVersion(previousVersion);
            if (previousVersion != null) {
                previousVersion.setNextVersion(versionMap.get(version));
            }
            versionMap.get(version).setState(MetadataSchemaState.LEGACY);
            previousVersion = versionMap.get(version);
            schemaVersions.add(previousVersion);
        }
        versionMap.get(versionsSorted.get(versionsSorted.size() - 1)).setState(MetadataSchemaState.LATEST);
        schemaVersions.forEach(versionRepository::saveAndFlush);

        // extensions
        remoteVersions.forEach(remoteVersion -> {
            for (int i = 0; i < remoteVersion.getExtendsSchemaUuids().size(); i++) {
                final MetadataSchemaVersion version = versionMap.get(remoteVersion.getVersion());
                final MetadataSchema extend =
                        schemaRepository.findByUuid(remoteVersion.getExtendsSchemaUuids().get(i)).get();
                extensionRepository.save(metadataSchemaMapper.newExtension(version, extend, i));
            }
        });
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    public List<MetadataSchemaVersionDTO> importSchemas(List<MetadataSchemaVersionDTO> reqDtos) {
        // Validate
        reqDtos.forEach(metadataSchemaValidator::validate);
        final List<MetadataSchemaVersion> localLatestSchemas =
                versionRepository.findAllByState(MetadataSchemaState.LATEST);
        final Map<UUID, List<MetadataSchemaVersionDTO>> schemas =
                reqDtos.stream().collect(groupingBy(MetadataSchemaVersionDTO::getUuid));
        final Set<UUID> toBePresentUuids = localLatestSchemas
                .stream()
                .map(MetadataSchemaVersion::extractSchemaUuid)
                .collect(Collectors.toSet());
        // Create new metadata schemas (root object with matching UUID)
        schemaRepository.saveAllAndFlush(
                reqDtos.stream()
                        .map(MetadataSchemaVersionDTO::getUuid)
                        .filter(uuid -> !toBePresentUuids.contains(uuid))
                        .collect(Collectors.toSet())
                        .stream()
                        .map(metadataSchemaMapper::newSchema)
                        .toList()
        );
        entityManager.flush();
        // Check extends relations
        toBePresentUuids.addAll(schemas.keySet());
        reqDtos.forEach(dto -> {
            if (dto.getExtendsSchemaUuids().stream().anyMatch(uuid -> !toBePresentUuids.contains(uuid))) {
                throw new ValidationException("Missing schema for extends relation");
            }
        });
        schemas.forEach(this::importVersions);
        entityManager.flush();
        return reqDtos
                .stream()
                .map(schema -> versionRepository.findByUuid(schema.getVersionUuid()).orElse(null))
                .filter(Objects::nonNull)
                .map(metadataSchemaMapper::toVersionDTO)
                .toList();
    }

    private List<MetadataSchemaRemoteDTO> checkForUpdates(String fdpUrl) {
        try {
            final Map<UUID, List<MetadataSchemaVersionDTO>> remoteSchemas = MetadataSchemaRetrievalUtils
                    .retrievePublishedMetadataSchemas(fdpUrl)
                    .stream()
                    .collect(groupingBy(MetadataSchemaVersionDTO::getUuid));
            final List<MetadataSchemaVersionDTO> updates = new ArrayList<>();
            remoteSchemas.forEach((schemaUuid, remoteVersions) -> {
                final List<MetadataSchemaVersion> localVersions = versionRepository.getBySchemaUuid(schemaUuid);
                final boolean hasCustom = localVersions
                        .stream()
                        .anyMatch(schema -> schema.getType() == MetadataSchemaType.CUSTOM);
                final boolean allImportedFromThis = localVersions
                        .stream()
                        .allMatch(schema -> schema.getImportedFrom().equals(fdpUrl));
                if (!hasCustom && allImportedFromThis && !localVersions.isEmpty()) {
                    final Set<UUID> localVersionUuids = localVersions
                            .stream()
                            .map(MetadataSchemaVersion::getUuid)
                            .collect(Collectors.toSet());
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
        final Set<String> importSources = versionRepository
                .findAllByImportedFromIsNotNull()
                .stream()
                .map(MetadataSchemaVersion::getImportedFrom)
                .collect(Collectors.toSet());
        return importSources
                .stream()
                .map(this::checkForUpdates)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<MetadataSchema> getAll(List<UUID> metadataSchemaUuids) {
        return schemaRepository.findAllById(metadataSchemaUuids);
    }
}
