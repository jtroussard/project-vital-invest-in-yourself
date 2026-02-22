package com.devlife4me.projectvital.service;

import org.springframework.stereotype.Service;

@Service
public class ConversionService {

    private static final float KG_TO_LB = 2.20462f;
    private static final float CM_TO_IN = 0.393701f;

    public float convertToMetric(float value, String measurementType, String fromUnit) {
        if (fromUnit == null || fromUnit.isEmpty())
            return value;

        switch (measurementType.toLowerCase()) {
            case "weight":
                if ("lb".equalsIgnoreCase(fromUnit))
                    return value / KG_TO_LB;
                break;
            case "length":
                if ("in".equalsIgnoreCase(fromUnit))
                    return value / CM_TO_IN;
                break;
        }
        return value;
    }

    public float convertFromMetric(float value, String measurementType, String toUnit) {
        if (toUnit == null || toUnit.isEmpty())
            return value;

        switch (measurementType.toLowerCase()) {
            case "weight":
                if ("lb".equalsIgnoreCase(toUnit))
                    return value * KG_TO_LB;
                break;
            case "length":
                if ("in".equalsIgnoreCase(toUnit))
                    return value * CM_TO_IN;
                break;
        }
        return value;
    }
}
