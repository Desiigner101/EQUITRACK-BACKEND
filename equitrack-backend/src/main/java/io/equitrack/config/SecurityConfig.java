package io.equitrack.config;

import io.equitrack.security.JwtRequestFilter;
import io.equitrack.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration // Marks this class as a Spring configuration class that defines beans
@RequiredArgsConstructor // Lombok annotation to generate constructor for final fields
public class SecurityConfig {

    private final AppUserDetailsService appUserDetailsService; // Custom user details service for loading user-specific data
    private final JwtRequestFilter jwtRequestFilter; // Custom JWT filter for validating JWT tokens

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Configure CORS with default settings (uses the corsConfigurationSource bean below)
                .cors(Customizer.withDefaults())
                // Disable CSRF protection - typically safe for stateless REST APIs that use tokens
                .csrf(AbstractHttpConfigurer::disable)
                // Configure authorization rules for HTTP requests
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints that don't require authentication
                        .requestMatchers("/status", "/health", "/register", "/activate", "/login").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated())
                // Set session management to stateless - no HTTP sessions will be created
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add our custom JWT filter before the default username/password authentication filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // BCrypt is a strong password hashing algorithm that automatically handles salting
        // Important for securely storing user passwords
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins (use carefully in production - should be restricted to specific domains)
        configuration.setAllowedOriginPatterns(List.of("*"));
        // Allow common HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Allow specific headers - Authorization for JWT tokens, Content-Type for request body type, Accept for response type
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // Allow credentials (cookies, authorization headers) to be sent with cross-origin requests
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all endpoints in the application
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        // DaoAuthenticationProvider is the standard authentication provider that uses UserDetailsService
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        // Set our custom user details service for loading user information from database
        authenticationProvider.setUserDetailsService(appUserDetailsService);
        // Set the password encoder to verify passwords during authentication
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        // Create authentication manager with our configured authentication provider
        return new ProviderManager(authenticationProvider);
    }
}