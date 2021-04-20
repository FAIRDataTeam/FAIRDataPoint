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

import nl.dtls.fairdatapoint.database.mongo.migration.development.MigrationRunner;
import nl.dtls.fairdatapoint.database.rdf.migration.development.RdfDevelopmentMigrationRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(Profiles.TESTING)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
public abstract class WebIntegrationTest {

    public static final String ADMIN_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9" +
            ".eyJzdWIiOiI5NTU4OWU1MC1kMjYxLTQ5MmItODg1Mi05MzI0ZTlhNjZhNDIiLCJpYXQiOjE2MTg5MDkxOTMsImV4cCI6MTYyMDExODc5M30" +
            ".0DOeuM1pUmMFfmW6L5hpOGLCHcW5_Zi4THtguF0UtWISeKO5nqrEvSYbH3cuWvq3AeyZ7hpKd9umzaopYCQIzw";

    public static final String ALBERT_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9" +
            ".eyJzdWIiOiI3ZTY0ODE4ZC02Mjc2LTQ2ZmItOGJiMS03MzJlNmUwOWY3ZTkiLCJpYXQiOjE2MTg5MDkxMzYsImV4cCI6MTYyMDExODczNn0" +
            ".gnJlJZxdSAHlqdQebCUPqCK9LSF1hzPufNTuQO4U7TuiSLdSnY1xnv4EPIFxkyQXPVHnwNodwC5nZqGEXO_pBg";

    public static final String NIKOLA_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9" +
            ".eyJzdWIiOiJiNWI5MmM2OS01ZWQ5LTQwNTQtOTU0ZC0wMTIxYzI5YjY4MDAiLCJpYXQiOjE2MTg5MDkyNDQsImV4cCI6MTYyMDExODg0NH0" +
            ".fC35q2Jj_2j7R_66WT4F0xLQY8gbVns7Hm8j7lbasEvAHPrucpsI3LdpxOILNY-GMTH1HpSp4divsGnbbfLLfA";

    @Autowired
    protected TestRestTemplate client;

    @Autowired
    protected MigrationRunner migrationRunner;

    @Autowired
    protected RdfDevelopmentMigrationRunner rdfDevelopmentMigrationRunner;

    @BeforeEach
    public void setup() {
        migrationRunner.run();
        rdfDevelopmentMigrationRunner.run();
    }

}