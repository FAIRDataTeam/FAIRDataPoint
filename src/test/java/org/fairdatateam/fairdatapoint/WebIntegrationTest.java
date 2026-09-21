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
package org.fairdatateam.fairdatapoint;

import org.fairdatateam.fairdatapoint.migration.mongodb.development.DevelopmentMigrationRunner;
import org.fairdatateam.fairdatapoint.migration.triplestore.development.RdfDevelopmentMigrationRunner;
import org.fairdatateam.fairdatapoint.rdf.StandaloneShaclValidator;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles(Profiles.TESTING)
// todo: in most cases we can probably use WebEnvironment.MOCK with MockMvc instead of TestRestTemplate (see #862)
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class WebIntegrationTest {

    public static final String ADMIN_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiI5NTU4OWU1MC1kMjYxLTQ5MmItODg1Mi05MzI0ZTlhNjZhNDIiLCJpYXQiOjE2MjA4Mzg3NjUsImV4cCI6MjUzMzcwNzY4NDYxfQ" +
            ".jEAq32oExPjKJAx0iEhuBjJlDZDxFmUF8L3Eu9lclB8";

    public static final String ALBERT_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiI3ZTY0ODE4ZC02Mjc2LTQ2ZmItOGJiMS03MzJlNmUwOWY3ZTkiLCJpYXQiOjE2MjA4Mzg3NDUsImV4cCI6MjUzMzcwNzY4NDYxfQ" +
            ".Am97T78sUkSi9riv0dWh7YoCaY_606y-RI0_XBrIPpM";

    public static final String NIKOLA_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiJiNWI5MmM2OS01ZWQ5LTQwNTQtOTU0ZC0wMTIxYzI5YjY4MDAiLCJpYXQiOjE2MjA4Mzg3MDgsImV4cCI6MjUzMzcwNzY4NDYxfQ" +
            ".pEIMdmGlHbdb28vRPZMGPBAP_u41ovmE0jw4c34QjQ8";

    @Autowired
    protected TestRestTemplate client;

    @Autowired
    protected DevelopmentMigrationRunner developmentMigrationRunner;

    @Autowired
    protected RdfDevelopmentMigrationRunner rdfDevelopmentMigrationRunner;

    @MockitoBean
    StandaloneShaclValidator shaclValidator;

    @BeforeEach
    public void setup() {
        developmentMigrationRunner.run();
        rdfDevelopmentMigrationRunner.run();
    }

}