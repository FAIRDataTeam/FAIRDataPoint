package nl.dtls.fairdatapoint.service.user;

import nl.dtls.fairdatapoint.database.mongo.repository.UserRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

@Service
public class UserValidator {

    @Autowired
    private UserRepository userRepository;

    public void validateEmail(String uuid, String email) {
        Optional<User> oUserEmail = userRepository.findByEmail(email);
        if (oUserEmail.isPresent() && !oUserEmail.get().getUuid().equals(uuid)) {
            throw new ValidationException(format("Email '%s' is already taken", email));
        }
    }

}
