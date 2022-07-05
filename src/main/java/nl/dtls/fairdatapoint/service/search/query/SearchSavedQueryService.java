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
import nl.dtls.fairdatapoint.database.mongo.repository.SearchSavedQueryRepository;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQuery;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQueryType;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import nl.dtls.fairdatapoint.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SearchSavedQueryService {

    @Autowired
    private SearchSavedQueryRepository repository;

    @Autowired
    private SearchSavedQueryMapper mapper;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserService userService;

    public List<SearchSavedQueryDTO> getAll() {
        Optional<User> optionalUser = currentUserService.getCurrentUser();
        Map<String, UserDTO> userMap = userService.getUsers().stream().collect(Collectors.toMap(UserDTO::getUuid, Function.identity()));
        return repository.findAll()
                .parallelStream()
                .filter(searchSavedQuery -> canSeeQuery(optionalUser, searchSavedQuery))
                .map(searchSavedQuery -> mapper.toDTO(searchSavedQuery, userMap.get(searchSavedQuery.getUserUuid())))
                .toList();
    }

    public Optional<SearchSavedQueryDTO> getSingle(String uuid) {
        Optional<User> optionalUser = currentUserService.getCurrentUser();
        return repository.findByUuid(uuid)
                .filter(searchSavedQuery -> canSeeQuery(optionalUser, searchSavedQuery))
                .map(searchSavedQuery -> {
                    UserDTO userDto = userService.getUserByUuid(searchSavedQuery.getUserUuid()).orElse(null);
                    return mapper.toDTO(searchSavedQuery, userDto);
                });
    }

    public boolean delete(String uuid) {
        Optional<SearchSavedQuery> optionalSearchQuery = repository.findByUuid(uuid);
        if (optionalSearchQuery.isEmpty()) {
            return false;
        }
        SearchSavedQuery searchSavedQuery = optionalSearchQuery.get();
        Optional<User> optionalUser = currentUserService.getCurrentUser();
        if (!canManageQuery(optionalUser, searchSavedQuery)) {
            throw new ForbiddenException("User is not allowed to update the query");
        }
        repository.delete(searchSavedQuery);
        return true;
    }

    public SearchSavedQueryDTO create(SearchSavedQueryChangeDTO reqDto) {
        UserDTO userDto = userService.getCurrentUser().orElse(null);
        SearchSavedQuery searchSavedQuery = repository.save(
                mapper.fromChangeDTO(reqDto, userDto)
        );
        return mapper.toDTO(searchSavedQuery, userDto);
    }

    public Optional<SearchSavedQueryDTO> update(String uuid, SearchSavedQueryChangeDTO reqDto) {
        Optional<SearchSavedQuery> optionalSearchQuery = repository.findByUuid(uuid);
        if (optionalSearchQuery.isEmpty()) {
            return Optional.empty();
        }
        Optional<User> optionalUser = currentUserService.getCurrentUser();
        SearchSavedQuery searchSavedQuery = optionalSearchQuery.get();
        if (!canManageQuery(optionalUser, searchSavedQuery)) {
            throw new ForbiddenException("User is not allowed to update the query");
        }
        SearchSavedQuery updatedQuery = repository.save(
                mapper.fromChangeDTO(searchSavedQuery, reqDto)
        );
        UserDTO userDto = updatedQuery.getUserUuid() == null ? null : userService.getUserByUuid(updatedQuery.getUserUuid()).orElse(null);
        return Optional.of(mapper.toDTO(updatedQuery, userDto));
    }

    private boolean canSeeQuery(Optional<User> optionalUser, SearchSavedQuery query) {
        return query.getType().equals(SearchSavedQueryType.PUBLIC) ||
                optionalUser.filter(user -> user.getRole().equals(UserRole.ADMIN)).isPresent() ||
                optionalUser.filter(user -> query.getType().equals(SearchSavedQueryType.INTERNAL) || Objects.equals(query.getUserUuid(), user.getUuid())).isPresent();
    }

    private boolean canManageQuery(Optional<User> optionalUser, SearchSavedQuery query) {
        if (optionalUser.isEmpty()) {
            return false;
        }
        return optionalUser.filter(user -> user.getRole().equals(UserRole.ADMIN) ||
                Objects.equals(query.getUserUuid(), user.getUuid())).isPresent();
    }
}
