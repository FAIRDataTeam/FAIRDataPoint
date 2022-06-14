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
package nl.dtls.fairdatapoint.database.mongo.migration.development.apikey.data;

import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.entity.apikey.ApiKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyFixtures {

    public static final String ALBERT_API_KEY =
            "a274793046e34a219fd0ea6362fcca61a001500b71724f4c973a017031653c20";

    public static final String NIKOLA_API_KEY =
            "dd5dc3b53b6145cfa9f6c58b72ebad21cd2f860ace62451ba4e3c74a0e63540a";

    @Autowired
    private UserFixtures userFixtures;

    public ApiKey albertApiKey() {
        return new ApiKey(
                null,
                "a1c00673-24c5-4e0a-bdbe-22e961ee7548",
                userFixtures.albert().getUuid(),
                ALBERT_API_KEY
        );
    }

    public ApiKey nikolaApiKey() {
        return new ApiKey(
                null,
                "62657760-21fe-488c-a0ea-f612a70493da",
                userFixtures.nikola().getUuid(),
                NIKOLA_API_KEY
        );
    }

}
