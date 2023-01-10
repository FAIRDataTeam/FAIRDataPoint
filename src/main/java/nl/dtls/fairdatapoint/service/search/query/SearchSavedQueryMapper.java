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
package nl.dtls.fairdatapoint.service.search.query;

import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQuery;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class SearchSavedQueryMapper {
    public SearchSavedQueryDTO toDTO(
            SearchSavedQuery query, UserDTO userDTO
    ) {
        return SearchSavedQueryDTO.builder()
                .uuid(query.getUuid())
                .name(query.getName())
                .description(query.getDescription())
                .variables(query.getVariables())
                .user(userDTO)
                .type(query.getType())
                .createdAt(query.getCreatedAt())
                .updatedAt(query.getUpdatedAt())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(
            SearchSavedQueryChangeDTO reqDto, UserDTO userDto
    ) {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .type(reqDto.getType())
                .variables(reqDto.getVariables())
                .userUuid(userDto == null ? null : userDto.getUuid())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(
            SearchSavedQuery query, SearchSavedQueryChangeDTO reqDto
    ) {
        return query.toBuilder()
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .variables(reqDto.getVariables())
                .userUuid(query.getUserUuid())
                .type(reqDto.getType())
                .updatedAt(Instant.now())
                .build();
    }
}
