package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.UserProfile;
import com.devlife4me.projectvital.repo.UserProfileRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileRepo profileRepository;

    public UserProfileController(UserProfileRepo profileRepository) {
        this.profileRepository = profileRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return profileRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/me")
    public ResponseEntity<UserProfile> createOrUpdateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserProfile profileData) {

        UUID userId = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElse(UserProfile.builder()
                        .userId(userId)
                        .email(email)
                        .build());

        if (profileData.getDisplayName() != null) {
            profile.setDisplayName(profileData.getDisplayName());
        }
        if (profileData.getFirstName() != null) {
            profile.setFirstName(profileData.getFirstName());
        }
        if (profileData.getMiddleName() != null) {
            profile.setMiddleName(profileData.getMiddleName());
        }
        if (profileData.getLastName() != null) {
            profile.setLastName(profileData.getLastName());
        }
        if (profileData.getGender() != null) {
            profile.setGender(profileData.getGender());
        }
        if (profileData.getStatus() != null) {
            profile.setStatus(profileData.getStatus());
        }
        if (profileData.getAddress() != null) {
            profile.setAddress(profileData.getAddress());
        }
        if (profileData.getPhoneNumber() != null) {
            profile.setPhoneNumber(profileData.getPhoneNumber());
        }

        return ResponseEntity.ok(profileRepository.save(profile));
    }
}