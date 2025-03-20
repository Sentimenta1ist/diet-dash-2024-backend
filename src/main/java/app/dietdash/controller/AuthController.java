package app.dietdash.controller;

import app.dietdash.config.UserAuthenticationProvider;
import app.dietdash.dto.ContactRequest;
import app.dietdash.dto.CredentialsDto;
import app.dietdash.dto.SignUpDto;
import app.dietdash.dto.UserDto;
import app.dietdash.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/test2")
    public String test2() {
        return "test2";
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitContactForm(@RequestBody ContactRequest request) {
        userService.sendContactEmail(request);
        return ResponseEntity.ok("Message sent successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {

        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getLogin()));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = userService.register(user);
        createdUser.setToken(userAuthenticationProvider.createToken(user.getLogin()));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @PostMapping("/send-forgot-email")
    public ResponseEntity<?> sendEmail(@RequestParam String email) {
        userService.sendForgotEmail(email);
        return ResponseEntity.ok("Ok");
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestParam UUID token, @RequestBody String password) {
        userService.updatePassword(token, password);
        return ResponseEntity.ok("Ok");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam UUID token) {
        userService.confirmUser(token);
        return ResponseEntity.ok("Ok");
    }

    @PostMapping("/checkNew")
    public ResponseEntity<String> checkNew() {
        return ResponseEntity.ok("Check new");
    }
}