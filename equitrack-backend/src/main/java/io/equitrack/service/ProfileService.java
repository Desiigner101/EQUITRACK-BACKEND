package io.equitrack.service;

import io.equitrack.dto.AuthDTO;
import io.equitrack.dto.ProfileDTO;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.ProfileRepository;
import io.equitrack.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    // Database operations for user profiles
    private final ProfileRepository profileRepository;
    // Email service for sending activation emails
    private final EmailService emailService;
    // Password encryption service (BCrypt)
    private final PasswordEncoder passwordEncoder;
    // Spring Security authentication manager
    private final AuthenticationManager authenticationManager;
    // JWT token utility for creating and validating tokens
    private final JwtUtil jwtUtil;

    // Activation URL from application.properties for email links
    @Value("${app.activation.url}")
    private String activationURL;

    /**
     * USER REGISTRATION - Complete account creation workflow
     * 1. Creates user entity with encrypted password
     * 2. Generates unique activation token
     * 3. Saves user to database (initially inactive)
     * 4. Sends activation email with unique link
     * 5. Returns user DTO without sensitive data
     */
    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        // Convert DTO to entity with encrypted password
        ProfileEntity newProfile = toEntity(profileDTO);

        // Generate unique activation token for email verification
        newProfile.setActivationToken(UUID.randomUUID().toString());

        // Save user to database (isActive=false by default from entity @PrePersist)
        newProfile = profileRepository.save(newProfile);

        // Build activation link and send email (async - won't block response)
        String activationLink = activationURL + "/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your EquiTrack Account!";
        String body = "Click on the following link to activate your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);

        // Return user data without password
        return toDTO(newProfile);
    }

    /**
     * CONVERT PROFILE DTO TO ENTITY - For database storage
     * Includes password encryption for security
     * Used when creating new users or updating existing ones
     */
    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                // CRITICAL: Encrypt password before storing in database
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    /**
     * CONVERT ENTITY TO DTO - For API responses
     * Excludes sensitive data like passwords and activation tokens
     * Used when returning user data to frontend
     */
    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .phone(profileEntity.getPhone())
                .bio(profileEntity.getBio())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    /**
     * ACCOUNT ACTIVATION - Verify email and activate user account
     * 1. Finds user by activation token
     * 2. If found, activates account and clears token
     * 3. Returns true if successful, false if token invalid
     */
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    // Activate the user account
                    profile.setIsActive(true);
                    // Clear the activation token (one-time use)
                    profile.setActivationToken(null);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false); // Return false if token not found
    }

    /**
     * CHECK ACCOUNT ACTIVATION STATUS
     * Used during login to ensure only activated accounts can log in
     */
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false); // Return false if user not found
    }

    /**
     * GET CURRENT AUTHENTICATED USER'S PROFILE ENTITY
     * Uses Spring Security context to get currently logged-in user
     * Used by other services to ensure data isolation
     */
    public ProfileEntity getCurrentProfile(){
        // Get authentication from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Find user by email (which is stored as username in authentication)
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
    }

    /**
     * GET PUBLIC PROFILE DATA - Safe for API responses
     * Can fetch by specific email or current logged-in user
     * Excludes sensitive information like passwords
     */
    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if(email == null){
            // No email provided - get current user's profile
            currentUser = getCurrentProfile();
        }else{
            // Specific email provided - find that user
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }

        // Return safe DTO without sensitive data
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .phone(currentUser.getPhone())
                .bio(currentUser.getBio())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    /**
     * USER AUTHENTICATION AND JWT TOKEN GENERATION
     * Complete login workflow:
     * 1. Validate credentials with Spring Security
     * 2. Generate JWT token for authenticated user
     * 3. Return token and user data for frontend
     */
    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try{
            // Step 1: Authenticate user credentials with Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

            // Step 2: Generate JWT token for authenticated user
            String token = jwtUtil.generateToken(authDTO.getEmail());

            // Step 3: Return token and user data
            return Map.of(
                    "token", token,           // JWT token for future requests
                    "user", getPublicProfile(authDTO.getEmail())  // User data for frontend
            );
        }catch(Exception e){
            // Authentication failed - invalid credentials
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     *    Updates profile information
     * 1. Gets current authenticated user
     * 2. Updates only allowed fields (NOT email or password)
     * 3. Saves changes to database
     * 4. Returns updated profile data
     */
    public ProfileDTO updateProfile(ProfileDTO profileDTO) {
        ProfileEntity currentProfile = getCurrentProfile();

        // Update fullName
        if (profileDTO.getFullName() != null && !profileDTO.getFullName().isEmpty()) {
            currentProfile.setFullName(profileDTO.getFullName());
        }
        // Update profileImageUrl
        if (profileDTO.getProfileImageUrl() != null) {
            currentProfile.setProfileImageUrl(profileDTO.getProfileImageUrl());
        }
        // Update phone
        if (profileDTO.getPhone() != null) {
            currentProfile.setPhone(profileDTO.getPhone());
        }
        // Update bio
        if (profileDTO.getBio() != null) {
            currentProfile.setBio(profileDTO.getBio());
        }
        // Save and return
        ProfileEntity updatedProfile = profileRepository.save(currentProfile);
        return toDTO(updatedProfile);
    }
}