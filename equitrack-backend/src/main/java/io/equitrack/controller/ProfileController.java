package io.equitrack.controller;

import io.equitrack.dto.ProfileDTO;
import io.equitrack.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);

        if(isActivated){
            return ResponseEntity.ok("Profile activated successfully");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }
}
