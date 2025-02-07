package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.entities.VerificationToken;
import com.example.crochetPatterns.repositories.UserRepository;
import com.example.crochetPatterns.repositories.VerificationTokenRepository;
import com.example.crochetPatterns.services.EmailService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthControllers {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    @Autowired
    public AuthControllers(UserService userService, PasswordEncoder passwordEncoder , VerificationTokenRepository verificationTokenRepository,
                           UserRepository userRepository,
                           EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
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
        User user = userService.addNewUser(registrationDTO , passwordEncoder.encode(registrationDTO.getPassword()));
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

        // 2) Generujesz token aktywacyjny (zwykle do linku)
        String token = UUID.randomUUID().toString();
        Timestamp expiryDate = Timestamp.from(
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC) // token ważny np. 1 dzień
        );
        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        verificationTokenRepository.save(verificationToken);

        // 3) Wyślij mail z linkiem
        String confirmationUrl = "http://localhost:8080/confirm?token=" + token;
        emailService.sendConfirmationEmail(user.getEmail(), confirmationUrl);

        return "mainMenu";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        // 1) Szukamy tokenu
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            model.addAttribute("message", "Niepoprawny token");
            return "error"; // lub inny widok
        }

        VerificationToken verificationToken = optionalToken.get();

        // 2) Sprawdzamy datę ważności
        if (verificationToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("message", "Token wygasł");
            return "error";
        }

        // 3) Aktywujemy użytkownika
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // 4) Usuwamy token, żeby nie można było go użyć ponownie (opcjonalnie)
        verificationTokenRepository.delete(verificationToken);

        model.addAttribute("message", "Konto aktywowane. Możesz się zalogować.");
        // Przekierowanie lub widok z potwierdzeniem
        return "login";
    }
}
