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
package org.fairdatateam.fairdatapoint.service.search.query;

import org.fairdatateam.fairdatapoint.api.dto.search.SearchQueryVariablesDTO;
import org.fairdatateam.fairdatapoint.api.dto.search.SparqlQueryVariablesChangeDTO;
import org.fairdatateam.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import org.fairdatateam.fairdatapoint.api.dto.user.UserDTO;
import org.fairdatateam.fairdatapoint.entity.search.SearchSavedQuery;
import org.fairdatateam.fairdatapoint.entity.search.SparqlQueryVariables;
import org.fairdatateam.fairdatapoint.entity.user.UserRole;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class SearchSavedQueryMapper {
    public SearchSavedQueryDTO toDTO(SearchSavedQuery query) {
        // anonymize the userDTO object as much as possible without breaking backward compatibility
        // TODO: replace UserDTO object by simple userUuid string (breaking change, postpone until next major release)
        final String hidden = "***";
        final UserDTO userDTO = new UserDTO();
        userDTO.setUuid(query.getUserUuid());
        userDTO.setFirstName(String.format("%.8s", query.getUserUuid()));
        userDTO.setLastName(hidden);
        userDTO.setEmail(hidden);
        userDTO.setRole(UserRole.USER);
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
            SparqlQueryVariablesChangeDTO reqDto, String userUuid
    ) {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .type(reqDto.getType())
                .variables(fromSearchQueryVariablesDTO(reqDto.getVariables()))
                .userUuid(userUuid)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(
            SearchSavedQuery query, SparqlQueryVariablesChangeDTO reqDto
    ) {
        return query.toBuilder()
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .variables(fromSearchQueryVariablesDTO(reqDto.getVariables()))
                .userUuid(query.getUserUuid())
                .type(reqDto.getType())
                .updatedAt(Instant.now())
                .build();
    }

    private SparqlQueryVariables fromSearchQueryVariablesDTO(SearchQueryVariablesDTO variablesDTO) {
        // 1-to-1 mapping is redundant, but we do it anyway to be consistent with the existing codebase
        return new SparqlQueryVariables(
                variablesDTO.getPrefixes(), variablesDTO.getGraphPattern(), variablesDTO.getOrdering()
        );
    }

}
