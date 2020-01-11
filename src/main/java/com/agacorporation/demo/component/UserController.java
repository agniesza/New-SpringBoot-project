package com.agacorporation.demo.component;

import com.agacorporation.demo.domain.Room;
import com.agacorporation.demo.domain.User;
import com.agacorporation.demo.service.SecurityService;
import com.agacorporation.demo.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        return "registration.html";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userService.save(userForm);
       // securityService.autoLogin(userForm.getLogin(),userForm.getPassword());
        return "welcome.html";
    }

    @PostMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
       return "welcome.html";
      //  return "login.html";
    }
    @GetMapping("/login")
    public String login() {
         //  return "redirect:/welcome.html";
        return "login.html";
    }

    @GetMapping({"/", "/welcome"})
    public String welcome(Model model, User user) {

        return "welcome.html";
    }

    @GetMapping({"/contact"})
    public String contact(Model model) {
        return "contact.html";
    }

    @GetMapping({"/userDetails"})
    public String showUserDetails(Model model) {
        return "userDetails.html";
    }

    @RequestMapping(value="/reservationList.html", params = {"uid"}, method = RequestMethod.GET)
    public String showUserDetails(Model model, long uid){
        User u = userService.getUser(uid);
        model.addAttribute("user", u);
        return "userDetails";
    }
}
