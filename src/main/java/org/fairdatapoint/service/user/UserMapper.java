/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.service.user;

import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.user.*;
import org.fairdatapoint.entity.user.UserAccount;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserDTO toDTO(UserAccount user) {
        return
                new UserDTO(
                        user.getUuid(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole());
    }

    public UserSimpleDTO toSimpleDTO(UserAccount user) {
        return
                new UserSimpleDTO(
                        user.getUuid(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail());
    }

    public UserAccount fromCreateDTO(UserCreateDTO dto) {
        return UserAccount.builder()
                .uuid(null)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
    }

    public UserAccount fromChangeDTO(UserChangeDTO dto, UserAccount user) {
        return
                user
                        .toBuilder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .role(dto.getRole())
                        .build();
    }

    public UserAccount fromProfileChangeDTO(UserProfileChangeDTO dto, UserAccount user) {
        return
                user
                        .toBuilder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .build();
    }

    public UserAccount fromPasswordDTO(UserPasswordDTO reqDto, UserAccount user) {
        return
                user
                        .toBuilder()
                        .passwordHash(passwordEncoder.encode(reqDto.getPassword()))
                        .build();
    }
}
