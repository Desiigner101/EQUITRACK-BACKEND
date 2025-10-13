package io.equitrack.service;

import io.equitrack.dto.ProfileDTO;
import io.equitrack.entity.ProfileEntity;
import io.equitrack.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        //TRIGGER EMAIL/SEND ACTIVATION LINK
        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your EquiTrack Account!";
        String body = "Click on the following link to activate your account: " + activationLink;
        emailService.sendEmail(newProfile.getEmail(),  subject, body);
        return toDTO(newProfile);
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity){
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }
}
