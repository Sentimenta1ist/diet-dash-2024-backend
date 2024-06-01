package app.dietdash.controller;

import app.dietdash.model.Meal;
import app.dietdash.model.Profile;
import app.dietdash.service.MealService;
import app.dietdash.service.ProfileService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    private final MealService mealService;

    public ProfileController(ProfileService profileService, MealService mealService) {
        this.profileService = profileService;
        this.mealService = mealService;
    }

    @GetMapping("/all/{term}")
    public ResponseEntity<List<Profile>> getAllProfiles(@PathVariable String term) {
        if (term.equals(" ")) return ResponseEntity.ok(profileService.getAllProfiles());
        return ResponseEntity.ok(profileService.getProfilesByName(term));
    }

    @PostMapping("/add")
    public ResponseEntity<UUID> addProfile(@RequestBody Profile profile) {
        Profile added = profileService.createProfile(profile);
        return new ResponseEntity<>(added.getId(), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody Profile profile) {
        Profile added = profileService.updateProfile(profile);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @GetMapping("/meals/{id}")
    public ResponseEntity<List<Meal>> getAllMeals(
            @PathVariable UUID id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealService.getMealsByDate(date, id));
    }

    @PostMapping("/meals/add")
    public ResponseEntity<String> addMeal(@RequestBody List<Meal> meals) {
        meals.forEach(mealService::addMeal);
        return ResponseEntity.ok("Meals added successfully.");
    }

}

