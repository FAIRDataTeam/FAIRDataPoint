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

import org.fairdatateam.fairdatapoint.search.dto.SearchSavedQueryChangeDTO;
import org.fairdatateam.fairdatapoint.search.dto.SearchSavedQueryDTO;
import org.fairdatateam.fairdatapoint.common.error.ForbiddenException;
import org.fairdatateam.fairdatapoint.common.error.UnauthorizedException;
import org.fairdatateam.fairdatapoint.user.User;
import org.fairdatateam.fairdatapoint.user.UserRole;
import org.fairdatateam.fairdatapoint.security.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SearchSavedQueryService {

    private static final String MSG_CANNOT_UPDATE = "User is not allowed to update the query";

    private static final String MSG_LOGIN_FIRST = "You have to log in";

    @Autowired
    private SearchSavedQueryRepository repository;

    @Autowired
    private SearchSavedQueryMapper mapper;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    public List<SearchSavedQueryDTO> getAll() {
        final Optional<User> optionalUser = currentUserProvider.getCurrentUser();
        return repository
                .findAllByOrderByCreatedAtAscUuidAsc()
                .stream()
                .filter(savedQuery -> canSeeQuery(optionalUser, savedQuery))
                .map(savedQuery -> mapper.toDTO(savedQuery))
                .toList();
    }

    public Optional<SearchSavedQueryDTO> getSingle(String uuid) {
        final Optional<User> optionalUser = currentUserProvider.getCurrentUser();
        return repository.findByUuid(uuid)
                .filter(savedQuery -> canSeeQuery(optionalUser, savedQuery))
                .map(savedQuery -> mapper.toDTO(savedQuery));
    }

    @Transactional
    public boolean delete(String uuid) {
        final Optional<SearchSavedQuery> optionalSearchQuery = repository.findByUuid(uuid);
        if (optionalSearchQuery.isEmpty()) {
            return false;
        }
        final SearchSavedQuery searchSavedQuery = optionalSearchQuery.get();
        final Optional<User> optionalUser = currentUserProvider.getCurrentUser();
        if (!canManageQuery(optionalUser, searchSavedQuery)) {
            throw new ForbiddenException(MSG_CANNOT_UPDATE);
        }
        repository.delete(searchSavedQuery);
        return true;
    }

    @Transactional
    public SearchSavedQueryDTO create(SearchSavedQueryChangeDTO reqDto) {
        // the current user is loaded inside this transaction, so it is a managed entity that can
        // be attached to the new saved query straight away
        final Optional<User> optionalUser = currentUserProvider.getCurrentUser();
        if (optionalUser.isEmpty()) {
            throw new UnauthorizedException(MSG_LOGIN_FIRST);
        }
        final SearchSavedQuery searchSavedQuery = repository.save(
                mapper.fromChangeDTO(reqDto, optionalUser.get())
        );
        return mapper.toDTO(searchSavedQuery);
    }

    @Transactional
    public Optional<SearchSavedQueryDTO> update(String uuid, SearchSavedQueryChangeDTO reqDto) {
        final Optional<SearchSavedQuery> optionalSearchQuery = repository.findByUuid(uuid);
        if (optionalSearchQuery.isEmpty()) {
            return Optional.empty();
        }
        final Optional<User> optionalUser = currentUserProvider.getCurrentUser();
        final SearchSavedQuery searchSavedQuery = optionalSearchQuery.get();
        if (!canManageQuery(optionalUser, searchSavedQuery)) {
            throw new ForbiddenException(MSG_CANNOT_UPDATE);
        }
        mapper.applyChangeDTO(searchSavedQuery, reqDto);
        // saveAndFlush so that @UpdateTimestamp is applied before the entity is mapped to the
        // response: a plain save on a managed entity does not flush until commit, which returned
        // a stale updatedAt.
        final SearchSavedQuery updatedQuery = repository.saveAndFlush(searchSavedQuery);
        return Optional.of(mapper.toDTO(updatedQuery));
    }

    private boolean canSeeQuery(Optional<User> optionalUser, SearchSavedQuery query) {
        return query.getType().equals(SearchSavedQueryType.PUBLIC)
                || optionalUser
                    .filter(user -> user.getRole().equals(UserRole.ADMIN))
                    .isPresent()
                || optionalUser
                    .filter(user -> isOwnOrInternal(user, query))
                    .isPresent();
    }

    private boolean isOwnOrInternal(User user, SearchSavedQuery query) {
        return query.getType().equals(SearchSavedQueryType.INTERNAL)
                || isSameUser(user, query);
    }

    private boolean canManageQuery(Optional<User> optionalUser, SearchSavedQuery query) {
        if (optionalUser.isEmpty()) {
            return false;
        }
        return optionalUser
                .filter(user -> isOwner(user, query))
                .isPresent();
    }

    private boolean isOwner(User user, SearchSavedQuery query) {
        return user.getRole().equals(UserRole.ADMIN)
                || isSameUser(user, query);
    }

    private boolean isSameUser(User user, SearchSavedQuery query) {
        return Objects.equals(query.getUser().getUuid(), user.getUuid());
    }
}
