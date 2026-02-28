package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.enums.QuantityCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversionServiceTest {

    private final ConversionService conversionService = new ConversionService();

    @Test
    void convertToMetric_Mass_LbToKg() {
        float result = conversionService.convertToMetric(200.0f, QuantityCategory.MASS, "lb");
        assertEquals(90.7184f, result, 0.001f);
    }

    @Test
    void convertFromMetric_Mass_KgToLb() {
        float result = conversionService.convertFromMetric(90.7184f, QuantityCategory.MASS, "lb");
        assertEquals(200.0f, result, 0.001f);
    }

    @Test
    void convertToMetric_Scalar_NoChange() {
        float result = conversionService.convertToMetric(5.5f, QuantityCategory.SCALAR, "mmol/L");
        assertEquals(5.5f, result);
    }

    @Test
    void convertFromMetric_Scalar_NoChange() {
        float result = conversionService.convertFromMetric(5.5f, QuantityCategory.SCALAR, "mmol/L");
        assertEquals(5.5f, result);
    }
}
