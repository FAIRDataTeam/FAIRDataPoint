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
package nl.dtls.fairdatapoint.service.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import nl.dtls.fairdatapoint.api.dto.auth.AuthDTO;
import nl.dtls.fairdatapoint.database.db.repository.UserAccountRepository;
import nl.dtls.fairdatapoint.entity.exception.UnauthorizedException;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import nl.dtls.fairdatapoint.service.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private static final Long DAY_MS = 24 * 60 * 60 * 1000L;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expiration:14}")
    private long expiration;

    private final UserAccountRepository userAccountRepository;

    @Lazy
    private final AuthenticationManager authenticationManager;

    private final AuthenticationService authenticationService;

    private JwtParser parser;

    private Key key;

    public JwtService(UserAccountRepository userAccountRepository, AuthenticationManager authenticationManager,
                      AuthenticationService authenticationService) {
        this.userAccountRepository = userAccountRepository;
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        parser = Jwts.parser().setSigningKey(key).build();
    }

    public String createToken(AuthDTO authDTO) {
        final Optional<UserAccount> user = userAccountRepository.findByEmail(authDTO.getEmail());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        final Authentication auth = new UsernamePasswordAuthenticationToken(
                user.get().getUuid(),
                authDTO.getPassword()
        );
        authenticationManager.authenticate(auth);
        return buildToken(user.get());
    }

    public Authentication getAuthentication(String token) {
        return authenticationService.getAuthentication(getUserUuid(token));
    }

    public String getUserUuid(String token) {
        return parser.parseClaimsJws(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            final Jws<Claims> claims = parser.parseClaimsJws(token);
            return !claims.getPayload().getExpiration().before(new Date());
        }
        catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("Expired or invalid JWT token");
        }
    }

    private String buildToken(UserAccount user) {
        final Claims claims = Jwts.claims().subject(user.getUuid().toString()).build();
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + (expiration * DAY_MS));
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }
}
