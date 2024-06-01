package app.dietdash.controller;


import app.dietdash.service.MealService;
import app.dietdash.service.ProductService;
import app.dietdash.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class DietController {

    private final MealService mealService;

    private final AppUserService userService;
    private final ProductService productService;

    private final ProfileService profileService;

    @Autowired
    public DietController(MealService mealService, AppUserService appUserService, ProductService productService, ProfileService profileService) {
        this.mealService = mealService;
        this.userService = appUserService;
        this.productService = productService;
        this.profileService = profileService;
    }

    @GetMapping("/profile/diets")
    public String diets(Model model, @RequestParam("p") String profileId) {
        model.addAttribute("profileId", profileId);
        return "diet";
    }

    @GetMapping("/profiles")
    public String selectProfiles(Model model, HttpSession session) {
        return "profiles";
    }

    @GetMapping("/profile")
    public String getProfile(Model model,  @RequestParam("p") UUID profileId) {
        model.addAttribute("profile", profileService.getProfileById(profileId));
        return "profile";
    }

}
