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
package nl.dtls.fairdatapoint.service.security;

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.database.db.repository.UserAccountRepository;
import nl.dtls.fairdatapoint.entity.exception.UnauthorizedException;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String uuid) {
        final Optional<UserAccount> oUser = repository.findByUuid(UUID.fromString(uuid));
        if (oUser.isEmpty()) {
            throw new UnauthorizedException("User not found");
        }
        final UserAccount user = oUser.get();
        final List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(format("ROLE_%s", user.getRole().name()))
        );
        return new org.springframework.security.core.userdetails.User(
                user.getUuid().toString(), user.getPasswordHash(), authorities
        );
    }
}
