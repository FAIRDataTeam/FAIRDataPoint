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
package nl.dtls.fairdatapoint.api.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.user.*;
import nl.dtls.fairdatapoint.entity.exception.ForbiddenException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Tag(name = "User Management")
@RestController
@RequestMapping("/users")
public class UserController {

    private static final String LOGIN_FIRST_MSG = "You have to be login at first";

    private static final String NOT_FOUND_MSG = "User '%s' doesn't exist";

    @Autowired
    private UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getUsers() {
        final List<UserDTO> dto = userService.getUsers();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserCreateDTO reqDto) {
        final UserDTO dto = userService.createUser(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Tag(name = "Authentication and Authorization")
    @GetMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserCurrent() throws ResourceNotFoundException {
        final Optional<UserDTO> oDto = userService.getCurrentUser();
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ForbiddenException(LOGIN_FIRST_MSG);
        }
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUser(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final Optional<UserDTO> oDto = userService.getUserByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PutMapping(path = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> putUserCurrent(
            @RequestBody @Valid UserProfileChangeDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<UserDTO> oDto = userService.updateCurrentUser(reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ForbiddenException(LOGIN_FIRST_MSG);
        }
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> putUser(
            @PathVariable final String uuid,
            @RequestBody @Valid UserChangeDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<UserDTO> oDto = userService.updateUser(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PutMapping(path = "/current/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> putUserCurrentPassword(
            @RequestBody @Valid UserPasswordDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<UserDTO> oDto = userService.updatePasswordForCurrentUser(reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ForbiddenException(LOGIN_FIRST_MSG);
        }
    }

    @PutMapping(path = "/{uuid}/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> putUserPassword(
            @PathVariable final String uuid,
            @RequestBody @Valid UserPasswordDTO reqDto
    ) throws ResourceNotFoundException {
        final Optional<UserDTO> oDto = userService.updatePassword(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(
            @PathVariable final String uuid
    ) throws ResourceNotFoundException {
        final boolean result = userService.deleteUser(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

}
