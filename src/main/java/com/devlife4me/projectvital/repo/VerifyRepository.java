package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.VerifyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifyRepository extends JpaRepository<VerifyEntity, Long> {
}