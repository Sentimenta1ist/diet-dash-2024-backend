package app.dietdash.service;

import app.dietdash.appuser.UserRole;
import app.dietdash.dto.ContactRequest;
import app.dietdash.dto.CredentialsDto;
import app.dietdash.dto.SignUpDto;
import app.dietdash.dto.UserDto;
import app.dietdash.model.User;
import app.dietdash.repository.UserRepository;
import app.dietdash.service.mapper.UserMapper;
import exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final MailTrapClient emailService;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(final CredentialsDto credentialsDto) {
        final User user = findUser(credentialsDto.getLogin());

        if (!user.isEnabled()) {
            throw new AppException("Email " + user.getLogin() + " not activated.", HttpStatus.NOT_ACCEPTABLE);
        }

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(final SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByLogin(userDto.getLogin());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        final User user = userMapper.signUpToUser(userDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));
        user.setConfirmationToken(UUID.randomUUID());
        user.setUserRole(UserRole.USER);

        final User savedUser = userRepository.save(user);

        sendEmail(user, "confirm", "VERIFY EMAIL");

        return userMapper.toUserDto(savedUser);
    }

    public void sendForgotEmail(final String email) {
        sendEmail(findUser(email), "forgot-password", "CHANGE PASSWORD");
    }


    public UserDto findByLogin(String login) {
        final User user = findUser(login);
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void confirmUser(final UUID confirmationToken) {
        final User user = userRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new AppException("Unknown user with this confirmation token", HttpStatus.NOT_FOUND));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(final UUID confirmationToken, final String password) {
        final User user = userRepository.findByConfirmationToken(confirmationToken)
                .orElseThrow(() -> new AppException("Unknown user with this confirmation token", HttpStatus.NOT_FOUND));

        final String newPassword = passwordEncoder.encode(CharBuffer.wrap(password));

        if (newPassword.matches(user.getPassword())) {
            throw new AppException("Do not use old password, please.", HttpStatus.BAD_REQUEST);
        }

        if (!user.isEnabled()) {
            throw new AppException("Email is not activated", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(newPassword);
        userRepository.save(user);
    }


    private User findUser(final String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    }

    private void sendEmail(final User user, final String type, final String messageButton) {
        try {
            emailService.sendEmail(
                    user.getLogin(),
                    "Diet dash (" + type + ").",
                    createEmailContent(user.getConfirmationToken(), type, messageButton),
                    true);
        } catch (IOException e) {
            throw new AppException("Cannot send email", HttpStatus.BAD_REQUEST);
        }
    }

    private String createEmailContent(final UUID confirmationToken, final String type, final String messageButton) {
        return "<html>"
                + "<body style='font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;'>"
                + "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>"
                + "<h2 style='color: #333333; text-align: center;'>Diet dash instructions. </h2>"
                + "<p style='font-size: 16px; color: #666666;'>Dear customer,</p>"
                + "<p style='font-size: 16px; color: #666666;'>Please click the button below and follow instructions: </p>"
                + "<div style='text-align: center; margin: 20px;'>"
                + "<a href='https://diet-dash.com/" + type + "?token=" + confirmationToken
                + "' style='font-size: 18px; color: #ffffff; background-color: #4CAF50; text-decoration: none; padding: 10px 20px; border-radius: 5px; display: inline-block;'>" + messageButton + "</a>"
                + "</div>"
                + "<p style='font-size: 14px; color: #666666;'>Thank you,<br>The Diet Dash Team</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    public void sendContactEmail(ContactRequest contact) {
        try {
            emailService.sendEmail(
                    "ivanenko.dev@gmail.com",
                    "DIET-DASH: New Contact Form Submission From " + contact.getName(),
                    "Name: " + contact.getName() + "\nEmail: " + contact.getEmail() + "\nNumber: " + contact.getNumber(),
                    false);
        } catch (IOException e) {
            throw new AppException("Cannot send email", HttpStatus.BAD_REQUEST);
        }

    }

    public String getPlan(UserDto userDto) {
        User user = userRepository.findByLogin(userDto.getLogin()).orElseThrow();

        if (user.getUserRole().equals(UserRole.PRO)) {
            return "Premium";
        } else if (user.getUserRole().equals(UserRole.USER)) {
            return "Free plan";
        }

        return "Free plan";
    }
}