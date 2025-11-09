package io.equitrack.service;

import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * BRIDGE BETWEEN YOUR USER DATABASE AND SPRING SECURITY
 *
 * This service tells Spring Security how to find and validate users during authentication
 * It's the translator that converts your ProfileEntity into Spring's UserDetails
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    // Database access to find users
    private final ProfileRepository profileRepository;

    /**
     * THE CORE AUTHENTICATION METHOD - Called by Spring Security during login
     *
     * This method:
     * 1. Takes an email (username) from login attempt
     * 2. Looks up user in database
     * 3. Returns user details for Spring Security to validate password
     * 4. If user not found, authentication fails automatically
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Step 1: Find user in database by email
        ProfileEntity existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));

        // Step 2: Convert our ProfileEntity to Spring Security's UserDetails
        return User.builder()
                .username(existingProfile.getEmail())      // Login identifier
                .password(existingProfile.getPassword())   // BCrypt encrypted password
                .authorities(Collections.emptyList())      // User roles/permissions (empty for now)
                .build();
    }
}