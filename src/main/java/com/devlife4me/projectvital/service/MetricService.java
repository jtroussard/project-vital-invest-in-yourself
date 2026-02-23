package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.MeasurementType;
import com.devlife4me.projectvital.model.entity.Metric;
import com.devlife4me.projectvital.repo.MeasurementTypeRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import com.devlife4me.projectvital.model.enums.MetricDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepo metricRepo;
    private final MeasurementTypeRepo measurementTypeRepo;

    public List<MeasurementType> getAllMeasurementTypes() {
        return measurementTypeRepo.findAll();
    }

    public List<Metric> getAllMetrics() {
        return metricRepo.findAll();
    }

    public List<Metric> getMetricsByMeasurementType(Long typeId) {
        return metricRepo.findByMeasurementTypeId(typeId);
    }

    @Transactional
    public MeasurementType createMeasurementType(String name) {
        return measurementTypeRepo.findByName(name)
                .orElseGet(() -> measurementTypeRepo.save(MeasurementType.builder().name(name).build()));
    }

    @Transactional
    public Metric createMetric(String name, String baseUnit, Long measurementTypeId,
            MetricDataType dataType) {
        MeasurementType type = measurementTypeRepo.findById(measurementTypeId)
                .orElseThrow(() -> new RuntimeException("Measurement type not found"));

        Metric metric = Metric.builder()
                .name(name)
                .baseUnit(baseUnit)
                .measurementType(type)
                .dataType(dataType != null ? dataType : MetricDataType.NUMERIC)
                .build();

        return metricRepo.save(metric);
    }
}
