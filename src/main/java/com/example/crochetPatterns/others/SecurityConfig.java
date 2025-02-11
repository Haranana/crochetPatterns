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
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /*
                .authorizeHttpRequests(authorize -> authorize
                        // Publiczne strony: główna, logowanie, zasoby statyczne
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        // Tylko zalogowani użytkownicy mogą dodawać posty lub komentarze
                        .requestMatchers("/addPost", "/addComment").authenticated()
                        // Pozostałe endpointy – dostęp według Twojej logiki
                        .anyRequest().permitAll()
                )*/
                .authorizeHttpRequests(authorize -> authorize
                        // Zasoby statyczne – dostęp publiczny
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Publiczne endpointy – dostęp ogólny
                        .requestMatchers(
                                "/", "/main",
                                "/login", "/register", "/confirm", "/afterRegister",
                                "/allPosts", "/showPost", "/userProfile", "/userPosts", "/userComments",
                                "/posts/{postId}/pdf"  // Używamy path variable zamiast **/pdf
                        ).permitAll()

                        // Prywatne endpointy – dostęp tylko dla zalogowanych
                        .requestMatchers(
                                "/addPost", "/addingPost",
                                "/editPost", "/confirmEditPost",
                                "/deletePost", "/deletePostConfirmed",
                                "/editComment", "/confirmEditComment",
                                "/writeComment", "/addingComment",
                                "/myProfile", "/editProfile", "/confirmEditProfile",
                                "/deleteAccount", "/deleteAccountConfirmed",
                                "/editPassword", "/confirmEditPassword",
                                "/post/{postId}/like", "/post/{postId}/unlike"
                        ).authenticated()

                        // Pozostałe endpointy – domyślnie dostęp publiczny
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
                // Konfiguracja mechanizmu "zapamiętaj mnie"
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")                     // klucz wykorzystywany do generowania tokena
                        .rememberMeParameter("remember-me")         // nazwa parametru z formularza (domyślnie "remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)       // czas ważności tokena – 7 dni (w sekundach)
                )
                // Konfiguracja wylogowania:
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me") // usuwamy również ciasteczko "remember-me"
                )
                // Domyślna ochrona CSRF – pozostawiamy włączoną:
                .csrf(Customizer.withDefaults());

        return http.build();
    }

    // Definicja encodera haseł
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}