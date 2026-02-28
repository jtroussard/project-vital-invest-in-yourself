package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.dto.request.BatchEntryRequest;
import com.devlife4me.projectvital.model.dto.request.EntryRequest;
import com.devlife4me.projectvital.model.dto.response.JournalEntryResponse;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping
    public ResponseEntity<JournalEntryResponse> createEntry(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody EntryRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        JournalEntryResponse response;

        if (request.getEntryType() == JournalEntryType.MEAL) {
            response = journalEntryService.createMealEntry(userId, request.getMeal(), request.getEntryDate());
        } else if (request.getEntryType() == JournalEntryType.NOTE) {
            response = journalEntryService.createNoteEntry(userId, request.getNotes(), request.getEntryDate());
        } else {
            response = journalEntryService.createMetricEntry(
                    userId,
                    request.getMetricId(),
                    request.getValue(),
                    request.getUnit(),
                    request.getEntryDate(),
                    request.getNotes());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<JournalEntryResponse>> createBatchEntries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody BatchEntryRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(journalEntryService.createBatchMetricEntries(
                userId,
                request.getEntries(),
                request.getEntryDate()));
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryResponse>> getEntries(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(journalEntryService.getEntries(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntryResponse> getEntry(@PathVariable Long id) {
        return journalEntryService.getEntry(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        journalEntryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}