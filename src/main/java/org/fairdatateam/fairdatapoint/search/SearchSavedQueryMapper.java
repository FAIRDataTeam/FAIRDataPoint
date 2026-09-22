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
package org.fairdatateam.fairdatapoint.search;

import org.fairdatateam.fairdatapoint.search.dto.SearchQueryVariablesDTO;
import org.fairdatateam.fairdatapoint.search.dto.SearchSavedQueryChangeDTO;
import org.fairdatateam.fairdatapoint.search.dto.SearchSavedQueryDTO;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.dto.UserDTO;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SearchSavedQueryMapper {

    public SearchSavedQueryDTO toDTO(SearchSavedQuery query) {
        // anonymize the userDTO object as much as possible without breaking backward compatibility
        // TODO: replace UserDTO object by simple userUuid string (breaking change, postpone until next major release)
        final String hidden = "***";
        final String userUuid = query.getUser().getUuid().toString();
        final UserDTO userDTO = new UserDTO();
        userDTO.setUuid(userUuid);
        userDTO.setFirstName(String.format("%.8s", userUuid));
        userDTO.setLastName(hidden);
        userDTO.setEmail(hidden);
        userDTO.setRole(UserRole.USER);
        return SearchSavedQueryDTO.builder()
                .uuid(query.getUuid().toString())
                .name(query.getName())
                .description(query.getDescription())
                .variables(toVariablesDTO(query.getVariables()))
                .user(userDTO)
                .type(query.getType())
                .createdAt(query.getCreatedAt())
                .updatedAt(query.getUpdatedAt())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(
            SearchSavedQueryChangeDTO reqDto, User user
    ) {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID())
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .type(reqDto.getType())
                .variables(fromVariablesDTO(reqDto.getVariables()))
                .user(user)
                .build();
    }

    // The query passed in is the managed entity loaded by the service, so the requested changes
    // are applied to it in place; building a detached copy instead would make the caller's save
    // a merge of a second instance carrying the same identifier. The owner and the timestamps are
    // left alone: ownership does not change, and Hibernate maintains updatedAt on flush.
    public void applyChangeDTO(
            SearchSavedQuery query, SearchSavedQueryChangeDTO reqDto
    ) {
        query.setName(reqDto.getName());
        query.setDescription(reqDto.getDescription());
        query.setType(reqDto.getType());
        query.setVariables(fromVariablesDTO(reqDto.getVariables()));
    }

    private SearchQueryVariablesDTO toVariablesDTO(SearchSavedQueryVariables variables) {
        return SearchQueryVariablesDTO.builder()
                .prefixes(variables.getPrefixes())
                .graphPattern(variables.getGraphPattern())
                .ordering(variables.getOrdering())
                .build();
    }

    private SearchSavedQueryVariables fromVariablesDTO(SearchQueryVariablesDTO reqDto) {
        return SearchSavedQueryVariables.builder()
                .prefixes(reqDto.getPrefixes())
                .graphPattern(reqDto.getGraphPattern())
                .ordering(reqDto.getOrdering())
                .build();
    }
}
