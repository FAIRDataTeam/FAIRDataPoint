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
package nl.dtls.fairdatapoint.service.apikey;

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.apikey.ApiKeyDTO;
import nl.dtls.fairdatapoint.database.db.repository.ApiKeyRepository;
import nl.dtls.fairdatapoint.entity.apikey.ApiKey;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.exception.UnauthorizedException;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import nl.dtls.fairdatapoint.service.security.AuthenticationService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private static final int TOKEN_SIZE = 128;

    private static final String MSG_LOGIN_FIRST = "You have to log in";

    private final ApiKeyRepository apiKeyRepository;

    private final CurrentUserService currentUserService;

    private final AuthenticationService authenticationService;

    private final ApiKeyMapper apiKeyMapper;

    public List<ApiKeyDTO> getAll() {
        final Optional<UserAccount> user = currentUserService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException(MSG_LOGIN_FIRST);
        }
        return user.get()
                .getApiKeys()
                .stream()
                .map(apiKeyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ApiKeyDTO create() {
        final Optional<UserAccount> user = currentUserService.getCurrentUser();
        if (user.isEmpty()) {
            throw new UnauthorizedException(MSG_LOGIN_FIRST);
        }
        final String generatedString = RandomStringUtils.random(TOKEN_SIZE, true, true);
        final ApiKey apiKey = apiKeyMapper.createApiKey(user.get(), generatedString);
        apiKeyRepository.save(apiKey);
        return apiKeyMapper.toDTO(apiKey);
    }

    public boolean delete(UUID uuid) {
        final Optional<ApiKey> apiKey = apiKeyRepository.findByUuid(uuid);
        if (apiKey.isEmpty()) {
            return false;
        }
        final Optional<UserAccount> user = currentUserService.getCurrentUser();
        if (user.isEmpty()) {
            throw new ForbiddenException(MSG_LOGIN_FIRST);
        }
        if (user.get().getRole().equals(UserRole.ADMIN)
                || apiKey.get().getUserAccount().equals(user.get())) {
            apiKeyRepository.delete(apiKey.get());
            return true;
        }
        else {
            throw new ForbiddenException("You are not allow to delete the entry");
        }
    }

    public Authentication getAuthentication(String token) {
        final Optional<ApiKey> apiKey = apiKeyRepository.findByToken(token);
        if (apiKey.isEmpty()) {
            throw new UnauthorizedException("Invalid or non-existing API key");
        }
        return authenticationService.getAuthentication(apiKey.get().getUserAccount().getUuid().toString());
    }
}
