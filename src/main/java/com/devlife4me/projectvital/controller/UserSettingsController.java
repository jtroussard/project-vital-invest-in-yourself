package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.UserSettings;
import com.devlife4me.projectvital.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<UserSettings> getSettings(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userSettingsService.getSettings(userId));
    }

    @PutMapping
    public ResponseEntity<UserSettings> updateSettings(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserSettings settings) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userSettingsService.updateSettings(userId, settings));
    }

    @PutMapping("/default-journal-metrics")
    public ResponseEntity<UserSettings> updateDefaultJournalMetrics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody List<Long> metricIds) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(userSettingsService.updateDefaultJournalMetrics(userId, metricIds));
    }
}