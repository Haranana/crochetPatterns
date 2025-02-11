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
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(
                                "/", "/main",
                                "/login", "/register", "/confirm", "/afterRegister",
                                "/allPosts", "/showPost", "/userProfile", "/userPosts", "/userComments",
                                "/posts/{postId}/pdf"
                        ).permitAll()
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
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login/process")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/main", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                )
                .csrf(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}