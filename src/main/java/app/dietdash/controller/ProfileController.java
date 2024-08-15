package app.dietdash.controller;

import app.dietdash.model.Profile;
import app.dietdash.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/all/{term}")
    public ResponseEntity<List<Profile>> getAllProfiles(@PathVariable String term) {
        if (term.equals(" ")) return ResponseEntity.ok(profileService.getAllProfiles());
        return ResponseEntity.ok(profileService.getProfilesByName(term));
    }

    @PostMapping("/add")
    public ResponseEntity<UUID> addProfile(@RequestBody Profile profile, Principal principal) {
        return ResponseEntity.ok(profileService.addProfile(profile, principal));
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody Profile profile) {
        Profile added = profileService.updateProfile(profile);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }
}

