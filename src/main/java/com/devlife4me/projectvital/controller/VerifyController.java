package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.VerifyEntity;
import com.devlife4me.projectvital.repo.VerifyRepo;
import com.devlife4me.projectvital.service.VerifyService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verify")
@Profile("!prod")
public class VerifyController {

    private final VerifyRepo verifyRepository;
    private final VerifyService verifyService;

    public VerifyController(VerifyRepo verifyRepository, VerifyService verifyService) {
        this.verifyRepository = verifyRepository;
        this.verifyService = verifyService;
    }

    @GetMapping
    public List<VerifyEntity> getVerifications() {
        return verifyRepository.findAll();
    }

    @PostMapping
    public VerifyEntity createVerification(@RequestBody VerifyEntity verification) {
        return verifyRepository.save(verification);
    }

    @DeleteMapping("/{id}")
    public void deleteVerification(@PathVariable Long id) {
        verifyRepository.deleteById(id);
    }

    @GetMapping("/test")
    public String testVerification() {
        return verifyService.testVerification();
    }

    @PostMapping("/env")
    public VerifyEntity checkEnv(@RequestBody String details) {
        return verifyService.checkEnv(details);
    }
}