package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricRepo extends JpaRepository<Metric, Long> {
    Optional<Metric> findByName(String name);

    java.util.List<Metric> findByMeasurementTypeId(Long measurementTypeId);
}
