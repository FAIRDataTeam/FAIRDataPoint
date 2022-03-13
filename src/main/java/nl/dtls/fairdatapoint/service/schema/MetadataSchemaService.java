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

import nl.dtls.fairdatapoint.api.dto.schema.*;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaDraftRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Meta;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Service
public class MetadataSchemaService {

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
        String uuid = UUID.randomUUID().toString();

        // Validate
        metadataSchemaValidator.validateAllExist(reqDto.getExtendsSchemaUuids());

        MetadataSchemaDraft draft = metadataSchemaMapper.fromChangeDTO(reqDto, uuid);
        metadataSchemaDraftRepository.save(draft);
        return metadataSchemaMapper.toDraftDTO(draft);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDraftDTO> getSchemaDraft(String uuid) {
        Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        if (oDraft.isPresent()) {
            return oDraft.map(metadataSchemaMapper::toDraftDTO);
        }
        return metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid).map(metadataSchemaMapper::toDraftDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDraftDTO> updateSchemaDraft(String uuid, MetadataSchemaChangeDTO reqDto) {
        Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        // Check if present
        if (oDraft.isEmpty()) {
            return empty();
        }
        // Validate
        metadataSchemaValidator.validateAllExist(reqDto.getExtendsSchemaUuids());
        metadataSchemaValidator.validateNoExtendsCycle(uuid, reqDto.getExtendsSchemaUuids());
        // Save
        MetadataSchemaDraft draft = oDraft.get();
        MetadataSchemaDraft updatedDraft = metadataSchemaMapper.fromChangeDTO(reqDto, draft);
        metadataSchemaDraftRepository.save(updatedDraft);
        return of(metadataSchemaMapper.toDraftDTO(updatedDraft));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchemaDraft(String uuid) {
        Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        if (oDraft.isEmpty()) {
            return false;
        }
        MetadataSchemaDraft draft = oDraft.get();
        metadataSchemaDraftRepository.delete(draft);
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDTO> publishDraft(String uuid, MetadataSchemaPublishDTO reqDto) {
        Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        // Check if present
        if (oDraft.isEmpty()) {
            return empty();
        }
        // Update
        MetadataSchemaDraft draft = oDraft.get();
        MetadataSchema newLatest = metadataSchemaMapper.fromPublishDTO(reqDto, draft);
        Optional<MetadataSchema> oLatest = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        oLatest.ifPresent(newLatest::setPreviousVersion);
        // Validate & Save
        if (oLatest.isPresent()) {
            MetadataSchema oldLatest = oLatest.get();
            oldLatest.setLatest(false);  // transactions would be nice
            metadataSchemaValidator.validateAllExist(newLatest.getExtendSchemas());
            metadataSchemaValidator.validate(newLatest);
            metadataSchemaRepository.save(oldLatest);
        } else {
            metadataSchemaValidator.validateAllExist(newLatest.getExtendSchemas());
            metadataSchemaValidator.validate(newLatest);
        }
        metadataSchemaRepository.save(newLatest);
        // Update cache
        targetClassesCache.computeCache();
        List<MetadataSchema> versions = metadataSchemaRepository.findByUuid(uuid);
        return of(metadataSchemaMapper.toDTO(newLatest, draft, versions));
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
    public Optional<MetadataSchemaVersionDTO> updateVersion(String uuid, String version, MetadataSchemaUpdateDTO reqDto) {
        Optional<MetadataSchema> oSchema = getByUuidAndVersion(uuid, version);
        if (oSchema.isEmpty()) {
            return empty();
        }
        MetadataSchema updatedSchema = metadataSchemaRepository.save(metadataSchemaMapper.fromUpdateDTO(oSchema.get(), reqDto));
        return of(metadataSchemaMapper.toVersionDTO(updatedSchema));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteVersion(String uuid, String version) {
        Optional<MetadataSchema> oSchema = getByUuidAndVersion(uuid, version);
        // Check if present
        if (oSchema.isEmpty()) {
            return false;
        }
        // Validate and fix links
        MetadataSchema schema = oSchema.get();
        MetadataSchema previous = schema.getPreviousVersion();
        Optional<MetadataSchema> oNewer = metadataSchemaRepository.findByPreviousVersion(schema);
        if (schema.isLatest()) {
            if (previous == null) {
                metadataSchemaValidator.validateNotUsed(uuid);
            } else {
                previous.setLatest(true);
                metadataSchemaValidator.validateNoExtendsCycle(uuid, previous.getExtendSchemas());
                metadataSchemaRepository.save(previous);
            }
        } else if (oNewer.isPresent()) {
            MetadataSchema newer = oNewer.get();
            newer.setPreviousVersion(previous);
            metadataSchemaRepository.save(newer);
        }
        metadataSchemaRepository.delete(schema);
        return true;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchemaFull(String uuid) {
        List<MetadataSchema> schemas = metadataSchemaRepository.findByUuid(uuid);
        Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
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

    public List<MetadataSchemaDTO> getSchemasWithoutDrafts() {
        return metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .map(schema -> {
                    List<MetadataSchema> versions = metadataSchemaRepository.findByUuid(schema.getUuid());
                    return metadataSchemaMapper.toDTO(schema, null, versions);
                })
                .toList();
    }

    public List<MetadataSchemaDTO> getSchemasWithDrafts() {
        Set<String> listedUuids = new HashSet<>();
        Stream<MetadataSchemaDTO> schemas = metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .map(schema -> {
                    List<MetadataSchema> versions = metadataSchemaRepository.findByUuid(schema.getUuid());
                    Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(schema.getUuid());
                    listedUuids.add(schema.getUuid());
                    return metadataSchemaMapper.toDTO(schema, oDraft.orElse(null), versions);
                });

        Stream<MetadataSchemaDTO> drafts = metadataSchemaDraftRepository
                .findAll()
                .stream()
                .filter(d -> !listedUuids.contains(d.getUuid()))
                .map(draft -> metadataSchemaMapper.toDTO(null, draft, Collections.emptyList()));
        return Stream.concat(schemas, drafts).toList();
    }

    public Optional<MetadataSchemaDTO> getSchemaByUuid(String uuid) {
        Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        Optional<MetadataSchemaDraft> oDraft = metadataSchemaDraftRepository.findByUuid(uuid);
        return oSchema.map(schema -> {
            List<MetadataSchema> versions = metadataSchemaRepository.findByUuid(schema.getUuid());
            return metadataSchemaMapper.toDTO(schema, oDraft.orElse(null), versions);
        });
    }

    public Optional<Model> getSchemaContentByUuid(String uuid) {
        // TODO: cache (?)
        Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuidAndLatestIsTrue(uuid);
        if (oSchema.isEmpty()) {
            return empty();
        }
        MetadataSchema schema = oSchema.get();
        List<MetadataSchema> schemas = resolveExtends(schema);
        return of(mergeSchemaDefinitions(schemas));
    }

    public Model getShaclFromSchemas() {
        Model shacl = new LinkedHashModel();
        List<MetadataSchema> metadataSchemas = metadataSchemaRepository.findAll();
        metadataSchemas.stream()
                .map(s -> RdfIOUtil.read(s.getDefinition(), ""))
                .forEach(m -> shacl.addAll(new ArrayList<>(m)));
        return shacl;
    }

    // ===============================================================================================
    // Extends and SHACL manipulation
    private List<MetadataSchema> resolveExtends(MetadataSchema schema) {
        Map<String, MetadataSchema> allSchemas = metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .collect(Collectors.toMap(MetadataSchema::getUuid, Function.identity()));
        Set<String> addedSchemaUuids = new HashSet<>();
        List<MetadataSchema> result = new ArrayList<>();
        addedSchemaUuids.add(schema.getUuid());
        result.add(schema);
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
        Model model = new LinkedHashModel();
        schemas.stream()
                .map(s -> RdfIOUtil.read(s.getDefinition(), ""))
                .forEach(m -> model.addAll(new ArrayList<>(m)));
        return model;
    }

    // ===============================================================================================
    // Importing and sharing

    public List<MetadataSchemaRemoteDTO> getPublishedSchemas() {
        return metadataSchemaRepository
                .findAllByPublishedIsTrue()
                .stream()
                .map(schema -> metadataSchemaMapper.toRemoteDTO(schema, persistentUrl))
                .toList();
    }

    public List<MetadataSchemaRemoteDTO> getRemoteSchemas(String fdpUrl) {
        return MetadataSchemaRetrievalUtils.retrievePublishedMetadataSchemas(fdpUrl);
    }

    private MetadataSchemaVersionDTO importSchema(MetadataSchemaRemoteDTO reqDto) {
        metadataSchemaValidator.validate(reqDto);
        String uuid = UUID.randomUUID().toString();
        MetadataSchema metadataSchema = metadataSchemaMapper.fromRemoteDTO(reqDto, uuid);
        MetadataSchema newMetadataSchema = metadataSchemaRepository.save(metadataSchema);
        return metadataSchemaMapper.toVersionDTO(newMetadataSchema);
    }

    public List<MetadataSchemaVersionDTO> importSchemas(List<MetadataSchemaRemoteDTO> reqDtos) {
        List<MetadataSchemaVersionDTO> result =
                reqDtos
                        .stream()
                        .map(this::importSchema)
                        .collect(Collectors.toList());
        targetClassesCache.computeCache();
        return result;
    }
}
