package app.dietdash.controller;

import app.dietdash.dto.ProfileDto;
import app.dietdash.dto.UserDto;
import app.dietdash.model.Profile;
import app.dietdash.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{term}")
    public ResponseEntity<List<ProfileDto>> getAllProfiles(@PathVariable String term, @AuthenticationPrincipal UserDto principal) {
        if (term.equals(" ")) {
            return ResponseEntity.ok(profileService.getAllProfiles(principal));
        }
        return ResponseEntity.ok(profileService.getProfilesByName(term, principal));
    }

    @PostMapping
    public ResponseEntity<UUID> addProfile(@RequestBody Profile profile, @AuthenticationPrincipal UserDto principal) {
        return ResponseEntity.ok(profileService.addProfile(profile, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable UUID id, @RequestBody Profile profile) {
        profileService.updateProfile(id, profile);
        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable UUID id) {
        profileService.deleteProfile(id);
        return ResponseEntity.ok("Deleted");
    }
}

