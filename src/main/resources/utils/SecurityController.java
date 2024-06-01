package app.dietdash.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {

    private final AppUserService appUserService;

    public SecurityController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/registration")
    public String register() {
        return "registration";
    }
//
//    @PostMapping("/registration")
//    public String registerNewUser(User user, Model model) {
//        // Call the service method to register the new user
//
//        if(appUserService.existsByUsername(user.getUsername())) {
//            model.addAttribute("message", "User already exists");
//            return "registration";
//        } else {
//            user.setActive(true);
//            user.setRoles(Collections.singleton(USER));
//            appUserService.save(user);
//        }
//
//        return "redirect:/login";
//    }
}
