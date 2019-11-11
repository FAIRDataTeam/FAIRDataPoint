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
package nl.dtls.fairdatapoint;

import nl.dtls.fairdatapoint.database.mongo.fixtures.DummyDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(Profiles.TESTING)
@DirtiesContext
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public abstract class WebIntegrationTest {

    public static final String ALBERT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiI3ZTY0ODE4ZC02Mjc2LTQ2ZmItOGJiMS03MzJlNmUwOWY3ZTkiLCJpYXQiOjE1NzI0NDczNTksImV4cCI6MjQzNjM2MDk1OX0" +
            ".yGZthRlVezhbKk1gDymW6pZfbCoxxqJda6md9btp00w";

    public static final String NIKOLA_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJiNWI5MmM2OS01ZWQ5LTQwNTQtOTU0ZC0wMTIxYzI5YjY4MDAiLCJpYXQiOjE1NzI5NjU2NTksImV4cCI6MjQzNjg3OTI1OX0" +
            ".f-nAX35Ob392xzerVqN9j34kCorZ0Lu6I18OgflHROs";

    @Autowired
    protected TestRestTemplate client;

    @Autowired
    protected DummyDataLoader dummyDataLoader;

    @BeforeEach
    public void setup() {
        dummyDataLoader.init();
    }

}