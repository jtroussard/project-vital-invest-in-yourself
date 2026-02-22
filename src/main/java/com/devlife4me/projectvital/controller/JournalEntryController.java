package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.dto.BatchEntryRequest;
import com.devlife4me.projectvital.dto.EntryRequest;
import com.devlife4me.projectvital.model.entity.JournalEntry;
import com.devlife4me.projectvital.model.entity.Meal;
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
    public ResponseEntity<JournalEntry> createEntry(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody EntryRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        JournalEntry entry;

        if (request.getEntryType() == JournalEntryType.MEAL) {
            entry = journalEntryService.createMealEntry(userId, request.getMeal(), request.getEntryDate());
        } else if (request.getEntryType() == JournalEntryType.NOTE) {
            entry = journalEntryService.createNoteEntry(userId, request.getNotes(), request.getEntryDate());
        } else {
            entry = journalEntryService.createMetricEntry(
                    userId,
                    request.getMetricId(),
                    request.getValue(),
                    request.getUnit(),
                    request.getEntryDate(),
                    request.getNotes());
        }

        return ResponseEntity.ok(entry);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<JournalEntry>> createBatchEntries(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody BatchEntryRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(journalEntryService.createBatchMetricEntries(
                userId,
                request.getEntries(),
                request.getEntryDate()));
    }

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getEntries(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(journalEntryService.getEntries(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getEntry(@PathVariable Long id) {
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
