package com.ci.ClientNotification.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // NEW IMPORT: Required for stateless configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults; // NEW IMPORT: Used for httpBasic

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // 1. ADD: Configure Session Management to be Stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 2. ADD: Enable HTTP Basic Authentication
                .httpBasic(withDefaults()) // Enables sending username/password in the header

                .authorizeHttpRequests(auth->auth
                        // The /api/auth/login endpoint is now redundant but kept permitted for clarity.
                        .requestMatchers("/", "/index.html", "/client.js", "/ws/**", "/api/auth/login", "/api/auth/register").permitAll()
                        .anyRequest().authenticated() // Protects /api/admin/broadcast
                )

                // 3. REMOVE: Disable the default form login mechanism (not needed with Basic Auth)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}