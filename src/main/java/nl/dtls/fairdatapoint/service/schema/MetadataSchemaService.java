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

import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaRemoteDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Service
public class MetadataSchemaService {

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private MetadataSchemaMapper metadataSchemaMapper;

    @Autowired
    private MetadataSchemaValidator metadataSchemaValidator;

    @Autowired
    private ResourceDefinitionTargetClassesCache targetClassesCache;

    public List<MetadataSchemaDTO> getSchemas() {
        List<MetadataSchema> metadataSchemas = metadataSchemaRepository.findAll();
        return
                metadataSchemas
                        .stream()
                        .map(metadataSchemaMapper::toDTO)
                        .collect(toList());
    }

    public List<MetadataSchemaDTO> getPublishedSchemas() {
        List<MetadataSchema> metadataSchemas = metadataSchemaRepository.findAllByPublishedIsTrue();
        return
                metadataSchemas
                        .stream()
                        .map(metadataSchemaMapper::toDTO)
                        .collect(toList());
    }

    public Optional<MetadataSchemaDTO> getSchemaByUuid(String uuid) {
        return
                metadataSchemaRepository
                        .findByUuid(uuid)
                        .map(metadataSchemaMapper::toDTO);
    }

    public Optional<Model> getSchemaContentByUuid(String uuid) {
        return
                metadataSchemaRepository
                        .findByUuid(uuid)
                        .map(schema -> RdfIOUtil.read(schema.getDefinition(), ""));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public MetadataSchemaDTO createSchema(MetadataSchemaChangeDTO reqDto) {
        metadataSchemaValidator.validate(reqDto);
        String uuid = UUID.randomUUID().toString();
        MetadataSchema metadataSchema = metadataSchemaMapper.fromChangeDTO(reqDto, uuid);
        metadataSchemaRepository.save(metadataSchema);
        targetClassesCache.computeCache();
        return metadataSchemaMapper.toDTO(metadataSchema);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<MetadataSchemaDTO> updateSchema(String uuid, MetadataSchemaChangeDTO reqDto) {
        metadataSchemaValidator.validate(reqDto);
        Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuid(uuid);
        if (oSchema.isEmpty()) {
            return empty();
        }
        MetadataSchema metadataSchema = oSchema.get();
        MetadataSchema updatedMetadataSchema = metadataSchemaMapper.fromChangeDTO(reqDto, metadataSchema);
        metadataSchemaRepository.save(updatedMetadataSchema);
        targetClassesCache.computeCache();
        return of(metadataSchemaMapper.toDTO(updatedMetadataSchema));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteSchema(String uuid) {
        Optional<MetadataSchema> oSchema = metadataSchemaRepository.findByUuid(uuid);
        if (oSchema.isEmpty()) {
            return false;
        }
        MetadataSchema metadataSchema = oSchema.get();

        List<ResourceDefinition> resourceDefinitions = resourceDefinitionRepository.findByMetadataSchemaUuidsIsContaining(metadataSchema.getUuid());
        if (!resourceDefinitions.isEmpty()) {
            throw new ValidationException(format("Metadata schema is used in %d resource definitions", resourceDefinitions.size()));
        }

        if (metadataSchema.getType() == MetadataSchemaType.INTERNAL) {
            throw new ValidationException("You can't delete INTERNAL metadata schema");
        }
        metadataSchemaRepository.delete(metadataSchema);
        targetClassesCache.computeCache();
        return true;
    }

    public Model getShaclFromSchemas() {
        Model shacl = new LinkedHashModel();
        List<MetadataSchema> metadataSchemas = metadataSchemaRepository.findAll();
        metadataSchemas.stream()
                .map(s -> RdfIOUtil.read(s.getDefinition(), ""))
                .forEach(m -> shacl.addAll(new ArrayList<>(m)));
        return shacl;
    }

    public List<MetadataSchemaRemoteDTO> getRemoteSchemas(String fdpUrl) {
        List<MetadataSchemaDTO> schemas = MetadataSchemaRetrievalUtils.retrievePublishedMetadataSchemas(fdpUrl);
        return schemas
                .stream()
                .map(s -> metadataSchemaMapper.toRemoteDTO(fdpUrl, s))
                .collect(Collectors.toList());
    }

    private MetadataSchemaDTO importSchema(MetadataSchemaChangeDTO reqDto) {
        metadataSchemaValidator.validate(reqDto);
        String uuid = UUID.randomUUID().toString();
        MetadataSchema metadataSchema = metadataSchemaMapper.fromChangeDTO(reqDto, uuid);
        metadataSchemaRepository.save(metadataSchema);
        return metadataSchemaMapper.toDTO(metadataSchema);
    }

    public List<MetadataSchemaDTO> importSchemas(List<MetadataSchemaRemoteDTO> reqDtos) {
        List<MetadataSchemaDTO> result =
                reqDtos
                        .stream()
                        .map(s -> metadataSchemaMapper.fromRemoteDTO(s))
                        .map(this::importSchema)
                        .collect(Collectors.toList());
        targetClassesCache.computeCache();
        return result;
    }
}
