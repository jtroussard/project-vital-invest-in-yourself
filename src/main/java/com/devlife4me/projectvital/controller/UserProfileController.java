package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.UserProfile;
import com.devlife4me.projectvital.repo.UserProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileRepository profileRepository;

    public UserProfileController(UserProfileRepository profileRepository) {
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

        return ResponseEntity.ok(profileRepository.save(profile));
    }
}
