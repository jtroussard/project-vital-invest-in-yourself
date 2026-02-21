package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.VerifyEntity;
import com.devlife4me.projectvital.repo.VerifyRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class VerifyService {

    private final VerifyRepository repo;

    public VerifyService(VerifyRepository repo) {
        this.repo = repo;
    }

    public VerifyEntity verifyCreate(String data) {
        VerifyEntity entity = new VerifyEntity();
        entity.setStatus("CREATED");
        entity.setDetails(data);
        return repo.save(entity);
    }

    public Optional<VerifyEntity> verifyRead(Long id) {
        return repo.findById(id);
    }

    public VerifyEntity verifyUpdate(Long id, String data) {
        return repo.findById(id)
                .map(entity -> {
                    entity.setDetails(data);
                    entity.setStatus("UPDATED");
                    return repo.save(entity);
                }).orElseThrow(() -> new RuntimeException("Record not found"));
    }

    public void verifyDelete(Long id) {
        repo.deleteById(id);
    }
}