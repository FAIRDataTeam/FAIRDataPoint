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
package nl.dtls.fairdatapoint.acceptance.user;

import nl.dtls.fairdatapoint.api.dto.user.UserChangeDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserCreateDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserProfileChangeDTO;
import nl.dtls.fairdatapoint.entity.user.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class Common {

    public static void compare(UserCreateDTO entity, UserDTO dto) {
        assertThat(dto.getFirstName(), is(equalTo(entity.getFirstName())));
        assertThat(dto.getLastName(), is(equalTo(entity.getLastName())));
        assertThat(dto.getEmail(), is(equalTo(entity.getEmail())));
    }

    public static void compare(UserChangeDTO entity, UserDTO dto) {
        assertThat(dto.getFirstName(), is(equalTo(entity.getFirstName())));
        assertThat(dto.getLastName(), is(equalTo(entity.getLastName())));
        assertThat(dto.getEmail(), is(equalTo(entity.getEmail())));
    }

    public static void compare(UserProfileChangeDTO entity, UserDTO dto) {
        assertThat(dto.getFirstName(), is(equalTo(entity.getFirstName())));
        assertThat(dto.getLastName(), is(equalTo(entity.getLastName())));
        assertThat(dto.getEmail(), is(equalTo(entity.getEmail())));
    }

    public static void compare(User entity, UserDTO dto) {
        assertThat(dto.getUuid(), is(equalTo(entity.getUuid())));
        assertThat(dto.getFirstName(), is(equalTo(entity.getFirstName())));
        assertThat(dto.getLastName(), is(equalTo(entity.getLastName())));
        assertThat(dto.getEmail(), is(equalTo(entity.getEmail())));
    }
}
