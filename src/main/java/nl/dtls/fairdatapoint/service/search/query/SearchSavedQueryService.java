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

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import nl.dtls.fairdatapoint.database.db.repository.SearchSavedQueryRepository;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQuery;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQueryType;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import nl.dtls.fairdatapoint.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchSavedQueryService {

    private static final String MSG_CANNOT_UPDATE = "User is not allowed to update the query";

    private final SearchSavedQueryRepository repository;

    private final SearchSavedQueryMapper mapper;

    private final CurrentUserService currentUserService;

    private final UserService userService;

    public List<SearchSavedQueryDTO> getAll() {
        final Optional<UserAccount> optionalUser = currentUserService.getCurrentUser();
        return repository
                .findAll()
                .parallelStream()
                .filter(query -> canSeeQuery(optionalUser, query))
                .map(mapper::toDTO)
                .toList();
    }

    public Optional<SearchSavedQueryDTO> getSingle(UUID uuid) {
        final Optional<UserAccount> optionalUser = currentUserService.getCurrentUser();
        return repository.findByUuid(uuid)
                .filter(searchSavedQuery -> canSeeQuery(optionalUser, searchSavedQuery))
                .map(mapper::toDTO);
    }

    public boolean delete(UUID uuid) {
        final Optional<SearchSavedQuery> optionalSearchQuery = repository.findByUuid(uuid);
        if (optionalSearchQuery.isEmpty()) {
            return false;
        }
        final SearchSavedQuery searchSavedQuery = optionalSearchQuery.get();
        final Optional<UserAccount> optionalUser = currentUserService.getCurrentUser();
        if (!canManageQuery(optionalUser, searchSavedQuery)) {
            throw new ForbiddenException(MSG_CANNOT_UPDATE);
        }
        repository.delete(searchSavedQuery);
        return true;
    }

    public SearchSavedQueryDTO create(SearchSavedQueryChangeDTO reqDto) {
        final UserAccount userAccount = userService.getCurrentUserAccount().orElse(null);
        final SearchSavedQuery searchSavedQuery = repository.save(
                mapper.fromChangeDTO(reqDto, userAccount)
        );
        return mapper.toDTO(searchSavedQuery);
    }

    public Optional<SearchSavedQueryDTO> update(UUID uuid, SearchSavedQueryChangeDTO reqDto) {
        final Optional<SearchSavedQuery> optionalSearchQuery = repository.findByUuid(uuid);
        if (optionalSearchQuery.isEmpty()) {
            return Optional.empty();
        }
        final Optional<UserAccount> optionalUser = currentUserService.getCurrentUser();
        final SearchSavedQuery searchSavedQuery = optionalSearchQuery.get();
        if (!canManageQuery(optionalUser, searchSavedQuery)) {
            throw new ForbiddenException(MSG_CANNOT_UPDATE);
        }
        final SearchSavedQuery updatedQuery = repository.save(
                mapper.fromChangeDTO(searchSavedQuery, reqDto, searchSavedQuery.getUserAccount())
        );
        return Optional.of(mapper.toDTO(updatedQuery));
    }

    private boolean canSeeQuery(Optional<UserAccount> optionalUser, SearchSavedQuery query) {
        return query.getType().equals(SearchSavedQueryType.PUBLIC)
                || optionalUser
                    .filter(user -> user.getRole().equals(UserRole.ADMIN))
                    .isPresent()
                || optionalUser
                    .filter(user -> isOwnOrInternal(user, query))
                    .isPresent();
    }

    private boolean isOwnOrInternal(UserAccount user, SearchSavedQuery query) {
        return query.getType().equals(SearchSavedQueryType.INTERNAL)
                || Objects.equals(query.getUserAccount().getUuid(), user.getUuid());
    }

    private boolean canManageQuery(Optional<UserAccount> optionalUser, SearchSavedQuery query) {
        if (optionalUser.isEmpty()) {
            return false;
        }
        return optionalUser
                .filter(user -> isOwner(user, query))
                .isPresent();
    }

    private boolean isOwner(UserAccount user, SearchSavedQuery query) {
        return user.getRole().equals(UserRole.ADMIN)
                || Objects.equals(query.getUserAccount().getUuid(), user.getUuid());
    }
}
