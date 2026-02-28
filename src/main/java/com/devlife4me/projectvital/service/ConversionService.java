package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.enums.QuantityCategory;
import com.devlife4me.projectvital.model.enums.UnitSystem;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConversionService {

    private static final Map<String, Float> CONVERSION_FACTORS = new HashMap<>();

    static {
        // MASS (Internal Standard: kg)
        CONVERSION_FACTORS.put("lb_to_kg", 0.453592f);
        CONVERSION_FACTORS.put("g_to_kg", 0.001f);
        CONVERSION_FACTORS.put("oz_to_kg", 0.0283495f);
    }

    public float convertToMetric(float value, QuantityCategory category, String fromUnit) {
        if (fromUnit == null || category == QuantityCategory.SCALAR || isStandardUnit(category, fromUnit)) {
            return value;
        }

        String key = fromUnit.toLowerCase() + "_to_" + getStandardUnit(category);
        Float factor = CONVERSION_FACTORS.get(key);

        if (factor != null) {
            return value * factor;
        }

        return value;
    }

    public float convertFromMetric(float value, QuantityCategory category, String toUnit) {
        if (toUnit == null || category == QuantityCategory.SCALAR || isStandardUnit(category, toUnit)) {
            return value;
        }

        String key = toUnit.toLowerCase() + "_to_" + getStandardUnit(category);
        Float factor = CONVERSION_FACTORS.get(key);

        if (factor != null) {
            return value / factor;
        }

        return value;
    }

    private boolean isStandardUnit(QuantityCategory category, String unit) {
        return getStandardUnit(category).equalsIgnoreCase(unit);
    }

    private String getStandardUnit(QuantityCategory category) {
        return switch (category) {
            case MASS -> "kg";
            default -> "";
        };
    }

    public String getDisplayUnit(QuantityCategory category, UnitSystem system) {
        if (category == null || category == QuantityCategory.SCALAR || system == null) {
            return null;
        }

        return switch (category) {
            case MASS -> system == UnitSystem.METRIC ? "kg" : "lb";
            default -> null;
        };
    }
}
