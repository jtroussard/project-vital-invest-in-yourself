package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.MeasurementType;
import com.devlife4me.projectvital.model.entity.Metric;
import com.devlife4me.projectvital.model.enums.MetricDataType;
import com.devlife4me.projectvital.repo.MeasurementTypeRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

    @Mock
    private MetricRepo metricRepo;

    @Mock
    private MeasurementTypeRepo measurementTypeRepo;

    @InjectMocks
    private MetricService metricService;

    @Test
    void createMetric_ReturnsSavedMetric() {
        Long typeId = 1L;
        MeasurementType type = MeasurementType.builder().id(typeId).name("Weight").build();

        when(measurementTypeRepo.findById(typeId)).thenReturn(Optional.of(type));
        when(metricRepo.save(any(Metric.class))).thenAnswer(i -> i.getArguments()[0]);

        Metric result = metricService.createMetric("Body Weight", "kg", typeId, MetricDataType.NUMERIC);

        assertNotNull(result);
        assertEquals("Body Weight", result.getName());
        assertEquals("kg", result.getBaseUnit());
        assertEquals(type, result.getMeasurementType());
        verify(metricRepo).save(any(Metric.class));
    }

    @Test
    void createMeasurementType_ReturnsNewTypeIfNotFound() {
        String name = "New Type";
        when(measurementTypeRepo.findByName(name)).thenReturn(Optional.empty());
        when(measurementTypeRepo.save(any(MeasurementType.class))).thenAnswer(i -> i.getArguments()[0]);

        MeasurementType result = metricService.createMeasurementType(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(measurementTypeRepo).save(any(MeasurementType.class));
    }

    @Test
    void createMeasurementType_ReturnsExistingTypeIfFound() {
        String name = "Existing Type";
        MeasurementType existing = MeasurementType.builder().name(name).build();
        when(measurementTypeRepo.findByName(name)).thenReturn(Optional.of(existing));

        MeasurementType result = metricService.createMeasurementType(name);

        assertNotNull(result);
        assertEquals(existing, result);
        verify(measurementTypeRepo, never()).save(any());
    }
}
