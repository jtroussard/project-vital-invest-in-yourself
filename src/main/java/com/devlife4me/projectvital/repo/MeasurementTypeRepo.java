package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.entity.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeasurementTypeRepo extends JpaRepository<MeasurementType, Long> {
    Optional<MeasurementType> findByName(String name);
}
