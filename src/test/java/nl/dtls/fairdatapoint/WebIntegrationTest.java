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

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(Profiles.TESTING)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@DirtiesContext
public abstract class WebIntegrationTest {

    public static final String ADMIN_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiI5NTU4OWU1MC1kMjYxLTQ5MmItODg1Mi05MzI0ZTlhNjZhNDIiLCJpYXQiOjE3MTAwNjIxMjEsImV4cCI6ODgxMTAwNjIxMjF9" +
            ".yJJ8nnIWjF0OjwreTQz6s6nEgSICOE8VSV1Cjq70RmE";

    public static final String ALBERT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiI3ZTY0ODE4ZC02Mjc2LTQ2ZmItOGJiMS03MzJlNmUwOWY3ZTkiLCJpYXQiOjE3MTAwNjIwODIsImV4cCI6ODgxMTAwNjIwODJ9" +
            ".xBT99pCVytFfg7qybVFsuPhtOTVG8tqgHVGidZXw-lA";

    public static final String NIKOLA_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJiNWI5MmM2OS01ZWQ5LTQwNTQtOTU0ZC0wMTIxYzI5YjY4MDAiLCJpYXQiOjE3MTAwNjIxNjcsImV4cCI6ODgxMTAwNjIxNjd9" +
            ".Jgnb1NauGNab7KX3Ym6qG5jRGnAWAISTxNASU9cnjN0";

    @Autowired
    protected TestRestTemplate client;

    @Autowired
    protected Flyway flyway;

    @BeforeEach
    public void setup() {
        flyway.clean();
        flyway.migrate();
    }

}