package com.psp.todo.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/login", "/register", "/css/**", "/js/**", "/img/**", "/error").permitAll()
                .anyRequest().authenticated())
            .formLogin(f -> f
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")   // returns people to the page they wanted
                .failureHandler((req, res, ex) -> res.sendRedirect(req.getContextPath() +
                    // a database problem is NOT the user's fault, so say so
                    (ex instanceof AuthenticationServiceException ? "/login?unavailable" : "/login?error"))))
            .logout(l -> l.logoutSuccessUrl("/login?logout").deleteCookies("SESSION"))
            .sessionManagement(s -> s.invalidSessionUrl("/login?expired"));
        return http.build();
    }
}
