package app.dietdash.service;

import app.dietdash.dto.UserDto;
import app.dietdash.model.User;
import app.dietdash.repository.UserRepository;
import exceptions.AppException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UtilService {

    private final UserRepository userRepository;

    public User getUserFromPrincipal(final UserDto principal) {
        final String login = principal == null ? null : principal.getLogin();

        return userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user with login: " + login, HttpStatus.NOT_FOUND));
    }
}
