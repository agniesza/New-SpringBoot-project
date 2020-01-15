package com.agacorporation.demo.component;

import com.agacorporation.demo.component.commands.UserFilter;
import com.agacorporation.demo.domain.Room;
import com.agacorporation.demo.domain.User;
import com.agacorporation.demo.service.EmailService;
import com.agacorporation.demo.service.SecurityService;
import com.agacorporation.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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
    @Secured("ROLE_ADMIN")
    @GetMapping("/addUser")
    public String addUser(Model model) {
        model.addAttribute("userForm", new User());
        return "addUser.html";
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/addUser")
    public String addUser(@ModelAttribute("userForm") User userForm, BindingResult bindingResult){
        userForm.setPassword(passwordEncoder.encode("testowe"));
        userService.save(userForm);
        emailService.send(userForm.getEmail(), "Witaj w hotelu woods!",
                "Rejesracja Twojego konta przebiegła pomyślnie. Twój login to:" + userForm.getLogin()+" Hasło do logowania to: testowe. Hasło zostało wygenerowane automatycznie. Proszę zmień hasło na stronie: www.woods.com");
        return "welcome.html";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userService.save(userForm);
        emailService.send(userForm.getEmail(), "Witaj w hotelu woods!",
                "Rejesracja Twojego konta przebiegła pomyślnie. Twój login to:" + userForm.getLogin()+" Zapraszamy na: www.woods.com");

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

    @GetMapping(value="/userList.html", params = {"all"})
    public String resetUserList(@ModelAttribute("searchCommand") UserFilter search){
        search.clear();
        return "redirect:userList.html";
    }
    @RequestMapping(value="/userList.html", method = {RequestMethod.GET})
    public String showUserList(Model model, Pageable pageable, @Valid @ModelAttribute("searchCommand") UserFilter search, BindingResult result){

        model.addAttribute("userListPage", userService.getAllUsers(search, pageable));

        return "userList";
    }

    @RequestMapping(value="/userList.html", method = {RequestMethod.POST})
    public String showUserList2(Model model, Pageable pageable, @Valid @ModelAttribute("searchCommand") UserFilter search, BindingResult result){

        model.addAttribute("userListPage", userService.getAllUsers(search, pageable));
        System.out.println("fraza "+search.getPhrase());
        return "userList";
        // return "redirect:reservationList";
    }
    @RequestMapping(value="/userDetails.html", method = RequestMethod.GET)
    public String showUserDetails(Model model, Principal principal){

        model.addAttribute("user", userService.getUserByLogin(principal.getName()));
        return "userDetails";
    }
}
