package com.example.crochetPatterns.others;

import com.example.crochetPatterns.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public SecurityConfig(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Konfiguracja dostępu do endpointów:
                .authorizeHttpRequests(authorize -> authorize
                        // Publiczne strony: główna, logowanie, zasoby statyczne
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        // Tylko zalogowani mogą dodawać posty lub komentarze
                        //.requestMatchers("/addPost", "/addingPost", "/addComment").authenticated()
                        .requestMatchers( "/addPost" , "/addComment").authenticated()
                        // Pozostałe endpointy – dostęp według Twojej logiki (tutaj np. dostęp publiczny)
                        .anyRequest().permitAll()
                )
                // Konfiguracja własnego formularza logowania:
                .formLogin(form -> form
                        .loginPage("/login")                      // własna strona logowania
                        .loginProcessingUrl("/login/process")       // adres, na który trafiają dane logowania
                        .usernameParameter("username")              // nazwa pola z loginem
                        .passwordParameter("password")              // nazwa pola z hasłem
                        .defaultSuccessUrl("/main", true)           // przekierowanie po udanym logowaniu
                        .failureUrl("/login?error=true")            // przekierowanie przy błędzie logowania
                        .permitAll()
                )
                // Konfiguracja wylogowania:
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                // Domyślna ochrona CSRF – możesz ją zmodyfikować, tutaj pozostawiamy domyślnie włączoną:
                .csrf(Customizer.withDefaults());

        return http.build();
    }


    // Definicja encodera haseł
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
