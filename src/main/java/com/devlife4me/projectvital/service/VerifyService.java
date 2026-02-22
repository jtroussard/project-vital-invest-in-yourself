package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.VerifyEntity;
import com.devlife4me.projectvital.repo.VerifyRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerifyService {

    private final VerifyRepo verifyRepository;

    public VerifyService(VerifyRepo verifyRepository) {
        this.verifyRepository = verifyRepository;
    }

    public String testVerification() {
        return "Verification service is working!";
    }

    public VerifyEntity checkEnv(String details) {
        Optional<VerifyEntity> existing = verifyRepository.findByDetails(details);
        if (existing.isPresent()) {
            return existing.get();
        }
        VerifyEntity newVerify = new VerifyEntity();
        newVerify.setDetails(details);
        newVerify.setStatus("CHECKED");
        return verifyRepository.save(newVerify);
    }
}