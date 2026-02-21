package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.VerifyEntity;
import com.devlife4me.projectvital.service.VerifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    private final VerifyService verifyService;

    // Spring Boot automatically injects the Service here
    public VerifyController(VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    // 1. Verify Create - Saves to DB
    @PostMapping("/create")
    public ResponseEntity<VerifyEntity> verifyCreate(@RequestBody String data) {
        VerifyEntity savedEntity = verifyService.verifyCreate(data);
        return ResponseEntity.ok(savedEntity);
    }

    // 2. Verify Read - Fetches from DB
    @GetMapping("/read/{id}")
    public ResponseEntity<VerifyEntity> verifyRead(@PathVariable Long id) {
        return verifyService.verifyRead(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Verify Update - Updates existing record
    @PutMapping("/update/{id}")
    public ResponseEntity<VerifyEntity> verifyUpdate(@PathVariable Long id, @RequestBody String data) {
        try {
            VerifyEntity updated = verifyService.verifyUpdate(id, data);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Verify Delete - Removes from DB
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> verifyDelete(@PathVariable Long id) {
        verifyService.verifyDelete(id);
        return ResponseEntity.noContent().build();
    }
}