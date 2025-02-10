package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.UserPasswordChangeDTO;
import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.entities.VerificationToken;
import com.example.crochetPatterns.others.LoggedUserDetails;
import com.example.crochetPatterns.repositories.UserRepository;
import com.example.crochetPatterns.repositories.VerificationTokenRepository;
import com.example.crochetPatterns.services.EmailService;
import com.example.crochetPatterns.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public AuthControllers(UserService userService, PasswordEncoder passwordEncoder,
                           VerificationTokenRepository verificationTokenRepository,
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

        // Sprawdź, czy nazwa użytkownika lub email są już zajęte
        if (userService.existsByUsername(registrationDTO.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Ta nazwa użytkownika jest już zajęta");
        }
        if (userService.existsByEmail(registrationDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Ten email jest już używany");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Zakoduj hasło i utwórz użytkownika
        User user = userService.addNewUser(registrationDTO, passwordEncoder.encode(registrationDTO.getPassword()));

        // Generuj token weryfikacyjny (ważny np. 1 dzień)
        String token = UUID.randomUUID().toString();
        Timestamp expiryDate = Timestamp.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));
        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        verificationTokenRepository.save(verificationToken);

        // Wyślij email z linkiem weryfikacyjnym
        String confirmationUrl = "http://localhost:8080/confirm?token=" + token;
        emailService.sendConfirmationEmail(user.getEmail(), confirmationUrl);

        // Zamiast powrotu do mainMenu zwracamy nowy widok:
        return "afterRegister";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            model.addAttribute("message", "Niepoprawny token");
            return "error";
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("message", "Token wygasł");
            return "error";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        model.addAttribute("message", "Konto aktywowane. Możesz się zalogować.");
        return "login";
    }

    // Endpoint wyświetlający formularz zmiany hasła
    @GetMapping("/editPassword")
    public String editPassword(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof LoggedUserDetails)) {
            return "login";
        }
        LoggedUserDetails userDetails = (LoggedUserDetails) auth.getPrincipal();
        UserPasswordChangeDTO dto = new UserPasswordChangeDTO();
        dto.setId(userDetails.getId());
        model.addAttribute("userPasswordChangeDTO", dto);
        return "editPassword"; // nazwa szablonu Thymeleaf, np. editPassword.html
    }

    // Endpoint przetwarzający formularz zmiany hasła
    @PostMapping("/confirmEditPassword")
    public String confirmEditPassword(@Valid @ModelAttribute("userPasswordChangeDTO") UserPasswordChangeDTO dto,
                                      BindingResult bindingResult,
                                      HttpServletRequest request,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            return "editPassword";
        }
        // Próba zmiany hasła
        if (!userService.changeUserPassword(dto, passwordEncoder)) {
            bindingResult.rejectValue("currentPassword", "error.currentPassword", "Aktualne hasło jest nieprawidłowe");
            return "editPassword";
        }
        // Opcjonalnie – unieważniamy sesję po zmianie hasła
        request.getSession().invalidate();
        // Czyscimy kontekst bezpieczeństwa
        SecurityContextHolder.clearContext();
        // Przekierowujemy użytkownika do strony logowania z informacją, że hasło zostało zmienione
        return "redirect:/login?passwordChanged";
    }

    @GetMapping("/afterRegister")
    public String afterRegister() {
        // Zwracamy widok (szablon) informujący o wysłaniu maila weryfikacyjnego
        return "afterRegister";
    }
}