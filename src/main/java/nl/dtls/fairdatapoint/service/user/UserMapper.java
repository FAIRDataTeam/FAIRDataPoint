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
package nl.dtls.fairdatapoint.service.user;

import nl.dtls.fairdatapoint.api.dto.user.*;
import nl.dtls.fairdatapoint.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO toDTO(User user) {
        return
                new UserDTO(
                        user.getUuid(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole());
    }

    public UserSimpleDTO toSimpleDTO(User user) {
        return
                new UserSimpleDTO(
                        user.getUuid(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail());
    }

    public User fromCreateDTO(UserCreateDTO dto, String uuid) {
        return
                new User(
                        uuid,
                        dto.getFirstName(),
                        dto.getLastName(),
                        dto.getEmail(),
                        passwordEncoder.encode(dto.getPassword()),
                        dto.getRole());
    }

    public User fromChangeDTO(UserChangeDTO dto, User user) {
        return
                user
                        .toBuilder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .role(dto.getRole())
                        .build();
    }

    public User fromProfileChangeDTO(UserProfileChangeDTO dto, User user) {
        return
                user
                        .toBuilder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .build();
    }

    public User fromPasswordDTO(UserPasswordDTO reqDto, User user) {
        return
                user
                        .toBuilder()
                        .passwordHash(passwordEncoder.encode(reqDto.getPassword()))
                        .build();
    }
}
