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
package nl.dtls.fairdatapoint.service.shape;

import nl.dtls.fairdatapoint.api.dto.shape.ShapeChangeDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeDTO;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeRemoteDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.ShapeRepository;
import nl.dtls.fairdatapoint.entity.exception.ShapeImportException;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.shape.Shape;
import nl.dtls.fairdatapoint.entity.shape.ShapeType;
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
public class ShapeService {

    @Autowired
    private ShapeRepository shapeRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ShapeMapper shapeMapper;

    @Autowired
    private ShapeValidator shapeValidator;

    @Autowired
    private ResourceDefinitionTargetClassesCache targetClassesCache;

    public List<ShapeDTO> getShapes() {
        List<Shape> shapes = shapeRepository.findAll();
        return
                shapes
                        .stream()
                        .map(shapeMapper::toDTO)
                        .collect(toList());
    }

    public List<ShapeDTO> getPublishedShapes() {
        List<Shape> shapes = shapeRepository.findAllByPublishedIsTrue();
        return
                shapes
                        .stream()
                        .map(shapeMapper::toDTO)
                        .collect(toList());
    }

    public Optional<ShapeDTO> getShapeByUuid(String uuid) {
        return
                shapeRepository
                        .findByUuid(uuid)
                        .map(shapeMapper::toDTO);
    }

    public Optional<Model> getShapeContentByUuid(String uuid) {
        return
                shapeRepository
                        .findByUuid(uuid)
                        .map(shape -> RdfIOUtil.read(shape.getDefinition(), ""));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ShapeDTO createShape(ShapeChangeDTO reqDto) {
        shapeValidator.validate(reqDto);
        String uuid = UUID.randomUUID().toString();
        Shape shape = shapeMapper.fromChangeDTO(reqDto, uuid);
        shapeRepository.save(shape);
        targetClassesCache.computeCache();
        return shapeMapper.toDTO(shape);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<ShapeDTO> updateShape(String uuid, ShapeChangeDTO reqDto) {
        shapeValidator.validate(reqDto);
        Optional<Shape> oShape = shapeRepository.findByUuid(uuid);
        if (oShape.isEmpty()) {
            return empty();
        }
        Shape shape = oShape.get();
        Shape updatedShape = shapeMapper.fromChangeDTO(reqDto, shape);
        shapeRepository.save(updatedShape);
        targetClassesCache.computeCache();
        return of(shapeMapper.toDTO(updatedShape));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteShape(String uuid) {
        Optional<Shape> oShape = shapeRepository.findByUuid(uuid);
        if (oShape.isEmpty()) {
            return false;
        }
        Shape shape = oShape.get();

        List<ResourceDefinition> resourceDefinitions = resourceDefinitionRepository.findByShapeUuidsIsContaining(shape.getUuid());
        if (!resourceDefinitions.isEmpty()) {
            throw new ValidationException(format("Shape is used in %d resource definitions", resourceDefinitions.size()));
        }

        if (shape.getType() == ShapeType.INTERNAL) {
            throw new ValidationException("You can't delete INTERNAL Shape");
        }
        shapeRepository.delete(shape);
        targetClassesCache.computeCache();
        return true;
    }

    public Model getShaclFromShapes() {
        Model shacl = new LinkedHashModel();
        List<Shape> shapes = shapeRepository.findAll();
        shapes.stream()
                .map(s -> RdfIOUtil.read(s.getDefinition(), ""))
                .forEach(m -> shacl.addAll(new ArrayList<>(m)));
        return shacl;
    }

    public List<ShapeRemoteDTO> getRemoteShapes(String fdpUrl) {
        List<ShapeDTO> shapes = ShapeRetrievalUtils.retrievePublishedShapes(fdpUrl);
        return shapes
                .stream()
                .map(s -> shapeMapper.toRemoteDTO(fdpUrl, s))
                .collect(Collectors.toList());
    }

    private ShapeDTO importShape(ShapeChangeDTO reqDto) {
        shapeValidator.validate(reqDto);
        String uuid = UUID.randomUUID().toString();
        Shape shape = shapeMapper.fromChangeDTO(reqDto, uuid);
        shapeRepository.save(shape);
        return shapeMapper.toDTO(shape);
    }

    public List<ShapeDTO> importShapes(List<ShapeRemoteDTO> reqDtos) {
        List<ShapeDTO> result =
                reqDtos
                        .stream()
                        .map(s -> shapeMapper.fromRemoteDTO(s))
                        .map(this::importShape)
                        .collect(Collectors.toList());
        targetClassesCache.computeCache();
        return result;
    }
}
