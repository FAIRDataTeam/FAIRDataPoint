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

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.user.*;
import nl.dtls.fairdatapoint.database.db.repository.UserAccountRepository;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import nl.dtls.fairdatapoint.service.member.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;

    private final UserMapper userMapper;

    private final UserValidator userValidator;

    private final MemberService memberService;

    public List<UserDTO> getUsers() {
        return
                userAccountRepository
                        .findAll()
                        .stream()
                        .map(userMapper::toDTO)
                        .toList();
    }

    public Optional<UserAccount> getUserAccountByUuid(UUID uuid) {
        return userAccountRepository.findByUuid(uuid);
    }

    public Optional<UserDTO> getUserDtoByUuid(UUID uuid) {
        return getUserAccountByUuid(uuid).map(userMapper::toDTO);
    }

    public Optional<UUID> getCurrentUserUuid() {
        final Object principal =
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            return of(((org.springframework.security.core.userdetails.User) principal)
                    .getUsername()).map(UUID::fromString);
        }
        return empty();
    }

    public Optional<UserAccount> getCurrentUserAccount() {
        return getCurrentUserUuid().flatMap(this::getUserAccountByUuid);
    }

    public Optional<UserDTO> getCurrentUserDto() {
        return getCurrentUserUuid().flatMap(this::getUserDtoByUuid);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(UserCreateDTO reqDto) {
        userValidator.validateEmail(null, reqDto.getEmail());
        final UserAccount user = userMapper.fromCreateDTO(reqDto);
        userAccountRepository.save(user);
        return userMapper.toDTO(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<UserDTO> updateUser(UUID uuid, UserChangeDTO reqDto) {
        final Optional<UserAccount> user = userAccountRepository.findByUuid(uuid);
        if (user.isEmpty()) {
            return empty();
        }
        userValidator.validateEmail(uuid, reqDto.getEmail());
        final UserAccount updatedUser = userMapper.fromChangeDTO(reqDto, user.get());
        userAccountRepository.save(updatedUser);
        return of(userMapper.toDTO(updatedUser));
    }

    public Optional<UserDTO> updateCurrentUser(UserProfileChangeDTO reqDto) {
        final Optional<UserAccount> user =
                getCurrentUserUuid().flatMap(userAccountRepository::findByUuid);
        if (user.isEmpty()) {
            return empty();
        }
        userValidator.validateEmail(user.get().getUuid(), reqDto.getEmail());
        final UserAccount updatedUser = userMapper.fromProfileChangeDTO(reqDto, user.get());
        userAccountRepository.save(updatedUser);
        return of(userMapper.toDTO(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<UserDTO> updatePassword(UUID uuid, UserPasswordDTO reqDto) {
        final Optional<UserAccount> user = userAccountRepository.findByUuid(uuid);
        if (user.isEmpty()) {
            return empty();
        }
        final UserAccount updatedUser = userMapper.fromPasswordDTO(reqDto, user.get());
        userAccountRepository.save(updatedUser);
        return of(userMapper.toDTO(updatedUser));
    }

    public Optional<UserDTO> updatePasswordForCurrentUser(UserPasswordDTO reqDto) {
        final Optional<UserAccount> user =
                getCurrentUserUuid().flatMap(userAccountRepository::findByUuid);
        if (user.isEmpty()) {
            return empty();
        }
        final UserAccount updatedUser = userMapper.fromPasswordDTO(reqDto, user.get());
        userAccountRepository.save(updatedUser);
        return of(userMapper.toDTO(updatedUser));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteUser(UUID uuid) {
        final Optional<UserAccount> user = userAccountRepository.findByUuid(uuid);
        if (user.isEmpty()) {
            return false;
        }
        userAccountRepository.delete(user.get());
        memberService.deleteMembers(user.get());
        return true;
    }
}
