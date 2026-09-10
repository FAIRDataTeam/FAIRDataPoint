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
package org.fairdatateam.fairdatapoint.security.token;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtServiceTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    public void validateSecretKey_missing(String secretKey) {
        assertThatThrownBy(() -> JwtService.validateSecretKey(secretKey))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(JwtService.MSG_SECRET_KEY_MISSING);
    }

    @ParameterizedTest
    @ValueSource(strings = {"short", "exactly-thirty-one-bytes-long!!"})
    public void validateSecretKey_tooShort(String secretKey) {
        assertThatThrownBy(() -> JwtService.validateSecretKey(secretKey))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(JwtService.MSG_SECRET_KEY_TOO_SHORT);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "exactly-thirty-two-bytes-long!!!",
            "testing-only-jwt-secret-with-at-least-32-bytes",
    })
    public void validateSecretKey_ok(String secretKey) {
        assertThatCode(() -> JwtService.validateSecretKey(secretKey)).doesNotThrowAnyException();
    }
}
