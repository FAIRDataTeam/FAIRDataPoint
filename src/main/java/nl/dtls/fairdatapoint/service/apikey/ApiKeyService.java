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

import nl.dtls.fairdatapoint.api.dto.apikey.ApiKeyDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.ApiKeyRepository;
import nl.dtls.fairdatapoint.entity.apikey.ApiKey;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.exception.UnauthorizedException;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApiKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    @Autowired
    private ApiKeyMapper apiKeyMapper;

    public List<ApiKeyDTO> getAll() {
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            throw new UnauthorizedException("You have to be log in");
        }
        User user = oUser.get();
        List<ApiKey> apiKeys = apiKeyRepository.findByUserUuid(user.getUuid());
        return apiKeys
                .stream()
                .map(apiKeyMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ApiKeyDTO create() {
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            throw new UnauthorizedException("You have to be log in");
        }
        User user = oUser.get();
        String generatedString = RandomStringUtils.random(128, true, true);
        String uuid = UUID.randomUUID().toString();
        ApiKey apiKey = new ApiKey(null, uuid, user.getUuid(), generatedString);
        apiKeyRepository.save(apiKey);
        return apiKeyMapper.toDTO(apiKey);
    }

    public boolean delete(String uuid) {
        Optional<ApiKey> oApiKey = apiKeyRepository.findByUuid(uuid);
        if (oApiKey.isEmpty()) {
            return false;
        }
        ApiKey apiKey = oApiKey.get();
        Optional<User> oCurrentUser = currentUserService.getCurrentUser();
        if (oCurrentUser.isEmpty()) {
            throw new ForbiddenException("You have to be log in");
        }
        User currentUser = oCurrentUser.get();
        if (currentUser.getRole().equals(UserRole.ADMIN) || apiKey.getUserUuid().equals(currentUser.getUuid())) {
            apiKeyRepository.delete(apiKey);
            return true;
        } else {
            throw new ForbiddenException("You are not allow to delete the entry");
        }
    }

    public Authentication getAuthentication(String token) {
        Optional<ApiKey> oApiKey = apiKeyRepository.findByToken(token);
        if (oApiKey.isEmpty()) {
            throw new UnauthorizedException("Invalid or non-existing API key");
        }
        ApiKey apiKey = oApiKey.get();
        return mongoAuthenticationService.getAuthentication(apiKey.getUserUuid());
    }

}
