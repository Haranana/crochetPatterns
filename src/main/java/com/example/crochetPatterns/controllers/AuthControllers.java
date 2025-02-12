package com.example.crochetPatterns.controllers;

import com.example.crochetPatterns.dtos.UserPasswordChangeDTO;
import com.example.crochetPatterns.dtos.UserRegistrationDTO;
import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.entities.VerificationToken;
import com.example.crochetPatterns.mappers.UserConverter;
import com.example.crochetPatterns.repositories.UserRepository;
import com.example.crochetPatterns.repositories.VerificationTokenRepository;
import com.example.crochetPatterns.services.AuthService;
import com.example.crochetPatterns.services.EmailService;
import com.example.crochetPatterns.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserConverter userConverter;
    private final AuthService authService;

    @Autowired
    public AuthControllers(UserService userService, PasswordEncoder passwordEncoder,
                           VerificationTokenRepository verificationTokenRepository,
                           UserRepository userRepository, AuthService authService,
                           EmailService emailService, UserConverter userConverter) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userConverter = userConverter;
        this.authService = authService;
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

        if (userService.existsByUsername(registrationDTO.getUsername())) {
            // Komunikat pobierany z pliku validation/validationMessages.properties lub validation/validationMessages_pl.properties
            bindingResult.rejectValue("username", "error.username");
        }
        if (userService.existsByEmail(registrationDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.email");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = userService.addNewUser(
                userConverter.createUser(registrationDTO, passwordEncoder.encode(registrationDTO.getPassword()))
        );

        // Generacja tokenu weryfikacji – domyślnie 1 dzień
        String token = UUID.randomUUID().toString();
        Timestamp expiryDate = Timestamp.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));
        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        verificationTokenRepository.save(verificationToken);

        // Wysłanie linka aktywacyjnego
        String confirmationUrl = "http://localhost:8080/confirm?token=" + token;
        emailService.sendConfirmationEmail(user.getEmail(), confirmationUrl);

        return "afterRegister";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            // Komunikat pobierany z pliku lang/messages.properties lub lang/messages_pl.properties
            model.addAttribute("message", "error.token.invalid");
            return "error";
        }

        VerificationToken verificationToken = optionalToken.get();
        if (verificationToken.getExpiryDate().before(new Timestamp(System.currentTimeMillis()))) {
            model.addAttribute("message", "error.token.expired");
            return "error";
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        model.addAttribute("message", "info.account.activated");
        return "login";
    }

    @GetMapping("/editPassword")
    public String editPassword(Model model) {
        if (!authService.isLogged()) {
            return "login";
        }
        UserPasswordChangeDTO dto = new UserPasswordChangeDTO();
        dto.setId(authService.getLoggedUserDetails().getId());
        model.addAttribute("userPasswordChangeDTO", dto);
        return "editPassword";
    }

    @PostMapping("/confirmEditPassword")
    public String confirmEditPassword(@Valid @ModelAttribute("userPasswordChangeDTO") UserPasswordChangeDTO dto,
                                      BindingResult bindingResult, HttpServletRequest request, Model model) {
        if (bindingResult.hasErrors()) {
            return "editPassword";
        }

        if (!userService.changeUserPassword(dto, passwordEncoder)) {
            bindingResult.rejectValue("currentPassword", "error.currentPassword");
            return "editPassword";
        }

        // Unieważnienie sesji i czyszczenie kontekstu bezpieczeństwa
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        return "redirect:/login?passwordChanged";
    }

    @GetMapping("/afterRegister")
    public String afterRegister() {
        return "afterRegister";
    }
}