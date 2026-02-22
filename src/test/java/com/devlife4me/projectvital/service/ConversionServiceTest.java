package com.devlife4me.projectvital.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConversionServiceTest {

    private final ConversionService conversionService = new ConversionService();

    @Test
    void convertToMetric_WeightLbToKg_ReturnsCorrectValue() {
        float result = conversionService.convertToMetric(220.462f, "weight", "lb");
        assertEquals(100.0f, result, 0.001f);
    }

    @Test
    void convertToMetric_LengthInToCm_ReturnsCorrectValue() {
        float result = conversionService.convertToMetric(1.0f, "length", "in");
        assertEquals(1.0f / 0.393701f, result, 0.001f);
    }

    @Test
    void convertToMetric_NullUnit_ReturnsSameValue() {
        float result = conversionService.convertToMetric(10.0f, "weight", null);
        assertEquals(10.0f, result);
    }

    @Test
    void convertFromMetric_WeightKgToLb_ReturnsCorrectValue() {
        float result = conversionService.convertFromMetric(100.0f, "weight", "lb");
        assertEquals(220.462f, result, 0.001f);
    }

    @Test
    void convertFromMetric_LengthCmToIn_ReturnsCorrectValue() {
        float result = conversionService.convertFromMetric(100.0f, "length", "in");
        assertEquals(39.3701f, result, 0.001f);
    }

    @Test
    void convertFromMetric_EmptyUnit_ReturnsSameValue() {
        float result = conversionService.convertFromMetric(10.0f, "length", "");
        assertEquals(10.0f, result);
    }

    @Test
    void convertToMetric_UnsupportedType_ReturnsSameValue() {
        float result = conversionService.convertToMetric(10.0f, "volume", "gal");
        assertEquals(10.0f, result);
    }
}
