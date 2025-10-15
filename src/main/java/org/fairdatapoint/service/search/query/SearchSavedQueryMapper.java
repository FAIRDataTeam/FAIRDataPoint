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
package org.fairdatapoint.service.search.query;

import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.search.SearchQueryVariablesDTO;
import org.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import org.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import org.fairdatapoint.entity.search.SearchSavedQuery;
import org.fairdatapoint.entity.user.UserAccount;
import org.fairdatapoint.service.user.UserMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SearchSavedQueryMapper {

    private final UserMapper userMapper;

    public SearchSavedQueryDTO toDTO(
            SearchSavedQuery query
    ) {
        return SearchSavedQueryDTO.builder()
                .uuid(query.getUuid())
                .name(query.getName())
                .description(query.getDescription())
                .variables(toVariablesDTO(query))
                .user(
                        Optional.ofNullable(query.getUserAccount())
                                .map(userMapper::toDTO)
                                .orElse(null)
                )
                .type(query.getType())
                .createdAt(query.getCreatedAt())
                .updatedAt(query.getUpdatedAt())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(
            SearchSavedQueryChangeDTO reqDto, UserAccount userAccount
    ) {
        return SearchSavedQuery.builder()
                .uuid(null)
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .type(reqDto.getType())
                .varPrefixes(reqDto.getVariables().getPrefixes())
                .varGraphPattern(reqDto.getVariables().getGraphPattern())
                .varOrdering(reqDto.getVariables().getOrdering())
                .userAccount(userAccount)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(
            SearchSavedQuery query, SearchSavedQueryChangeDTO reqDto,
            UserAccount userAccount
    ) {
        return query.toBuilder()
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .varPrefixes(reqDto.getVariables().getPrefixes())
                .varGraphPattern(reqDto.getVariables().getGraphPattern())
                .varOrdering(reqDto.getVariables().getOrdering())
                .userAccount(userAccount)
                .type(reqDto.getType())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchQueryVariablesDTO toVariablesDTO(
            SearchSavedQuery query
    ) {
        return SearchQueryVariablesDTO.builder()
                .prefixes(query.getVarPrefixes())
                .graphPattern(query.getVarGraphPattern())
                .ordering(query.getVarOrdering())
                .build();
    }
}
