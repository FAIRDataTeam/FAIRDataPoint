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
package nl.dtls.fairdatapoint.database.mongo.migration.development.user.data;

import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserFixtures {

    public static final String ADMIN_EMAIL = "admin@example.com";

    public static final String ALBERT_EMAIL = "albert.einstein@example.com";

    public static final String NIKOLA_EMAIL = "nikola.tesla@example.com";

    public static final String ISAAC_EMAIL = "isaac.newton@example.com";

    private static final String PASSWORD = "password";

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User admin() {
        return new User(
                KnownUUIDs.USER_ADMIN_UUID,
                "Admin",
                "von Universe",
                ADMIN_EMAIL,
                passwordEncoder.encode(PASSWORD),
                UserRole.ADMIN
        );
    }

    public User albert() {
        return new User(
                KnownUUIDs.USER_ALBERT_UUID,
                "Albert",
                "Einstein",
                ALBERT_EMAIL,
                passwordEncoder.encode(PASSWORD),
                UserRole.USER
        );
    }

    public User nikola() {
        return new User(
                KnownUUIDs.USER_NIKOLA_UUID,
                "Nikola",
                "Tesla",
                NIKOLA_EMAIL,
                passwordEncoder.encode(PASSWORD),
                UserRole.USER
        );
    }

    public User isaac() {
        return new User(
                KnownUUIDs.USER_ISAAC_UUID,
                "Isaac",
                "Newton",
                ISAAC_EMAIL,
                passwordEncoder.encode(PASSWORD),
                UserRole.USER
        );
    }

}
