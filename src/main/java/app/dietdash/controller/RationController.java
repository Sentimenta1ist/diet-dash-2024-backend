package app.dietdash.controller;

import app.dietdash.dto.MealDto;
import app.dietdash.model.Ration;
import app.dietdash.service.RationService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/ration")
@AllArgsConstructor
public class RationController {

    private final RationService rationService;

    @GetMapping("/{profileId}")
    public ResponseEntity<Ration> getRation(
            @PathVariable UUID profileId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(rationService.getRationByProfileAndDate(profileId, date));
    }

    @PostMapping("/add/{profileId}")
    public ResponseEntity<String> addMealToRation(
            @PathVariable UUID profileId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody MealDto meal) {

        return ResponseEntity.ok(rationService.addMealToRation(meal, profileId, date));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMeal(@RequestBody MealDto meal) {

        rationService.addMeal(meal);

        return ResponseEntity.ok("fuckf");
    }

    @GetMapping("/current-user")
    public String getCurrentUser(Principal principal) {
        return "Current user: " + principal.getName();
    }

}
