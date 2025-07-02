package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuration ultra-permissive pour les tests
        http
                .csrf(csrf -> csrf.disable())  // Désactive CSRF
                .cors(cors -> cors.configurationSource(permissiveCorsSource()))  // CORS très ouvert
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // Autorise tout sans auth
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())  // Pour H2 console
                );

        return http.build();
    }

    // Configuration CORS ultra-permissive (à modifier en production)
    @Bean
    public CorsConfigurationSource permissiveCorsSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));  // Autorise toutes les origines
        configuration.setAllowedMethods(Arrays.asList("*"));  // Autorise toutes les méthodes
        configuration.setAllowedHeaders(Arrays.asList("*"));  // Autorise tous les headers
        configuration.setAllowCredentials(false);  // Désactive les credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}