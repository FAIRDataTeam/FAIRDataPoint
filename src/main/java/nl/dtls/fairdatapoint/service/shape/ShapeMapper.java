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
import nl.dtls.fairdatapoint.entity.shape.Shape;
import nl.dtls.fairdatapoint.entity.shape.ShapeType;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ShapeMapper {

    public ShapeDTO toDTO(Shape shape) {
        return
                new ShapeDTO(
                        shape.getUuid(),
                        shape.getName(),
                        shape.getDescription(),
                        shape.isPublished(),
                        shape.getType(),
                        shape.getDefinition(),
                        shape.getTargetClasses().stream().sorted().toList(),
                        shape.getCreatedAt(),
                        shape.getUpdatedAt()
                );
    }

    public Shape fromChangeDTO(ShapeChangeDTO dto, String uuid) {
        return
                new Shape(
                        null,
                        uuid,
                        dto.getName(),
                        dto.getDescription(),
                        dto.isPublished(),
                        ShapeType.CUSTOM,
                        dto.getDefinition(),
                        ShapeShaclUtils.extractTargetClasses(dto.getDefinition()),
                        Instant.now(),
                        Instant.now()
                );

    }

    public Shape fromChangeDTO(ShapeChangeDTO dto, Shape shape) {
        return
                shape
                        .toBuilder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .published(dto.isPublished())
                        .definition(dto.getDefinition())
                        .targetClasses(ShapeShaclUtils.extractTargetClasses(dto.getDefinition()))
                        .updatedAt(Instant.now())
                        .build();
    }

    public ShapeRemoteDTO toRemoteDTO(String fdpUrl, ShapeDTO shape) {
        return
                new ShapeRemoteDTO(
                        fdpUrl,
                        shape.getUuid(),
                        shape.getName(),
                        shape.getDescription(),
                        shape.getDefinition(),
                        shape.getCreatedAt(),
                        shape.getUpdatedAt()
                );
    }

    public ShapeChangeDTO fromRemoteDTO(ShapeRemoteDTO shape) {
        return
                new ShapeChangeDTO(
                        shape.getName(),
                        shape.getDescription(),
                        false,
                        shape.getDefinition()
                );
    }
}
