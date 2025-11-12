package io.equitrack.controller;

import io.equitrack.dto.AuthDTO;
import io.equitrack.dto.ProfileDTO;
import io.equitrack.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService; // Handles user authentication and profile management

    // User registration endpoint - creates new user account
    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        // profileService.registerProfile() will validate email, hash password, save user to database
        // AND send activation email with verification token
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        // Returns HTTP 201 (Created) with user profile (excluding password)
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    // Email verification endpoint - activates account after user clicks link in email
    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        // profileService checks if token exists and is valid, then activates the user account
        boolean isActivated = profileService.activateProfile(token);

        if(isActivated){
            return ResponseEntity.ok("Profile activated successfully");
        }else{
            // Token might be expired, already used, or invalid
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }

    // User login endpoint - authenticates and returns JWT token
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO){
        try{
            // First check if account is activated (email verified)
            if(!profileService.isAccountActive(authDTO.getEmail())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "message", "Account is not active. Please activate your account first."
                ));
            }

            // If account is active, authenticate and generate JWT token
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);
        }catch(Exception e){
            // Invalid credentials or other authentication errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}