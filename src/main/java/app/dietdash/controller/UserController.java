package app.dietdash.controller;

import app.dietdash.dto.UserDto;
import app.dietdash.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/plan")
    public ResponseEntity<String> getPlan(@AuthenticationPrincipal UserDto principal) {
        return ResponseEntity.ok(userService.getPlan(principal));
    }
}
