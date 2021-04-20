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
import nl.dtls.fairdatapoint.api.dto.auth.AuthDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.UserRepository;
import nl.dtls.fairdatapoint.entity.exception.UnauthorizedException;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.security.MongoAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtService {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expiration:14}")
    private long expiration;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MongoAuthenticationService mongoAuthenticationService;

    private JwtParser parser;

    private Key key;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String createToken(AuthDTO authDTO) {
        Optional<User> oUser = userRepository.findByEmail(authDTO.getEmail());
        if (oUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = oUser.get();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUuid(),
                authDTO.getPassword()));
        return buildToken(user);
    }

    public Authentication getAuthentication(String token) {
        return mongoAuthenticationService.getAuthentication(getUserUuid(token));
    }

    public String getUserUuid(String token) {
        return parser.parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println(e);
            throw new UnauthorizedException("Expired or invalid JWT token");
        }
    }

    private String buildToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUuid());
        Date now = new Date();
        Date validity = new Date(now.getTime() + (expiration * 24 * 60 * 60 * 1000));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }
}
