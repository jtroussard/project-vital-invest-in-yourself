package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.MeasurementType;
import com.devlife4me.projectvital.model.entity.Metric;
import com.devlife4me.projectvital.model.enums.MetricDataType;
import com.devlife4me.projectvital.service.MetricService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    @GetMapping
    public ResponseEntity<List<Metric>> getMetrics() {
        return ResponseEntity.ok(metricService.getAllMetrics());
    }

    @PostMapping
    public ResponseEntity<Metric> createMetric(@RequestBody MetricRequest request) {
        Metric metric = metricService.createMetric(
                request.getName(),
                request.getBaseUnit(),
                request.getMeasurementTypeId(),
                request.getDataType());
        return ResponseEntity.ok(metric);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<Metric>> getMetricsByType(@PathVariable Long typeId) {
        return ResponseEntity.ok(metricService.getMetricsByMeasurementType(typeId));
    }

    @GetMapping("/types")
    public ResponseEntity<List<MeasurementType>> getMeasurementTypes() {
        return ResponseEntity.ok(metricService.getAllMeasurementTypes());
    }

    @PostMapping("/types")
    public ResponseEntity<MeasurementType> createMeasurementType(@RequestBody String name) {
        return ResponseEntity.ok(metricService.createMeasurementType(name));
    }

    @Data
    public static class MetricRequest {
        private String name;
        private String baseUnit;
        private Long measurementTypeId;
        private MetricDataType dataType;
    }
}
