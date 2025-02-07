package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthControllers {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthControllers(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO registrationDTO,
            BindingResult bindingResult, Model model) {

        // Sprawdź czy email lub username są już zajęte
        if (userService.existsByUsername(registrationDTO.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Ta nazwa użytkownika jest już zajęta");
        }
        if (userService.existsByEmail(registrationDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Ten email jest już używany");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.addNewUser(registrationDTO , passwordEncoder.encode(registrationDTO.getPassword()));
        // Utwórz nowego użytkownika
        //User user = new User();
        //user.setUsername(registrationDTO.getUsername());
        //user.setEmail(registrationDTO.getEmail());
        // Zakoduj hasło
        //user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        // Wersja podstawowa – użytkownik od razu aktywny
        // Możesz ustawić domyślnie enabled = true, jeżeli taka kolumna istnieje

       // user.setEnabled(true);

        //userService.saveUser(user);

        // Po udanej rejestracji możesz przekierować do strony logowania lub automatycznie zalogować użytkownika
        return "mainMenu";
    }


}
