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
package org.fairdatateam.fairdatapoint.security.apikey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    /**
     * Lists the API keys of a single user. The owning user is not fetched along with the keys, so
     * it must not be navigated to after the transaction has ended.
     *
     * @param userUuid identifier of the owning user
     * @return the API keys of that user, possibly empty
     */
    List<ApiKey> findByUserUuid(UUID userUuid);

    Optional<ApiKey> findByUuid(UUID uuid);

    /**
     * Looks an API key up by the string form of its identifier, as it arrives from the REST API.
     * A malformed identifier is treated as "no such API key" instead of an error.
     *
     * @param uuid identifier in string form, possibly malformed
     * @return the API key, or empty if the identifier is malformed or unknown
     */
    default Optional<ApiKey> findByUuid(String uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        try {
            return findByUuid(UUID.fromString(uuid));
        }
        catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    /**
     * Resolves an API key token. The owning user is not fetched along with the key.
     *
     * @param token the raw API key token
     * @return the API key, or empty if the token is unknown
     */
    Optional<ApiKey> findByToken(String token);

    /**
     * Resolves the identifier of the user owning an API key token, without loading the key or the
     * user entity itself. This is what request authentication uses, since it only needs the
     * identifier to build the {@link org.springframework.security.core.Authentication}.
     *
     * @param token the raw API key token
     * @return the identifier of the owning user, or empty if the token is unknown
     */
    @Query("select key.user.uuid from ApiKey key where key.token = :token")
    Optional<UUID> findUserUuidByToken(@Param("token") String token);

}
