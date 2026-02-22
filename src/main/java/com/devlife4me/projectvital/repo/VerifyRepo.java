package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.entity.VerifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyRepo extends JpaRepository<VerifyEntity, Long> {
    Optional<VerifyEntity> findByDetails(String details);
}