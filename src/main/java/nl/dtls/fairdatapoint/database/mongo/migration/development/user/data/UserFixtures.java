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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserFixtures {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User albert() {
        return new User(
                "7e64818d-6276-46fb-8bb1-732e6e09f7e9",
                "Albert",
                "Einstein",
                "albert.einstein@example.com",
                passwordEncoder.encode("password"),
                UserRole.ADMIN
        );
    }

    public User nikola() {
        return new User(
                "b5b92c69-5ed9-4054-954d-0121c29b6800",
                "Nikola",
                "Tesla",
                "nikola.tesla@example.com",
                passwordEncoder.encode("password"),
                UserRole.USER
        );
    }

    public User isaac() {
        return new User(
                "8d1a4c06-bb0e-4d03-a01f-14fa49bbc152",
                "Isaac",
                "Newton",
                "isaac.newton@example.com",
                passwordEncoder.encode("password"),
                UserRole.USER
        );
    }

}
