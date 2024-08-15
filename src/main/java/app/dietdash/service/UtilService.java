package app.dietdash.service;

import exceptions.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UtilService {

    public String getLoginFromPrincipal(final Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
            throw new AppException("Empty user", HttpStatus.BAD_REQUEST);
        }

        // Use regex to extract the login part
        final String login = principal.getName().replaceAll(".*login=(.*?),.*", "$1");

        if (login.isEmpty()) {
            throw new AppException("User without login", HttpStatus.BAD_REQUEST);
        }

        return login;
    }

}
