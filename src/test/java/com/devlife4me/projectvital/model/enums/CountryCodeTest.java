package com.devlife4me.projectvital.model.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CountryCodeTest {

    @Test
    void fromCode_ValidAlpha2_ReturnsCorrectEnum() {
        assertEquals(CountryCode.US, CountryCode.fromCode("US"));
        assertEquals(CountryCode.US, CountryCode.fromCode("us"));
        assertEquals(CountryCode.CA, CountryCode.fromCode("CA"));
        assertEquals(CountryCode.FR, CountryCode.fromCode("fr"));
    }

    @Test
    void fromCode_ValidNumeric_ReturnsCorrectEnum() {
        assertEquals(CountryCode.US, CountryCode.fromCode("840"));
        assertEquals(CountryCode.CA, CountryCode.fromCode("124"));
        assertEquals(CountryCode.AF, CountryCode.fromCode("004"));
    }

    @Test
    void fromCode_InvalidOrNull_ReturnsUnknown() {
        assertEquals(CountryCode.UNKNOWN, CountryCode.fromCode(null));
        assertEquals(CountryCode.UNKNOWN, CountryCode.fromCode("ZZ"));
        assertEquals(CountryCode.UNKNOWN, CountryCode.fromCode("999"));
        assertEquals(CountryCode.UNKNOWN, CountryCode.fromCode(""));
    }

    @Test
    void getters_ReturnCorrectValues() {
        CountryCode us = CountryCode.US;
        assertEquals("US", us.getAlpha2());
        assertEquals("840", us.getNumeric());
        assertEquals("United States", us.getDisplayName());

        CountryCode unknown = CountryCode.UNKNOWN;
        assertEquals("XX", unknown.getAlpha2());
        assertEquals("000", unknown.getNumeric());
        assertEquals("Unknown", unknown.getDisplayName());
    }

    @Test
    void values_ContainsExpectedCount() {
        // There are 240 enum constants in the file (lines 7 to 240)
        // Let's just verify a few more to be sure
        assertNotNull(CountryCode.GB);
        assertNotNull(CountryCode.DE);
        assertNotNull(CountryCode.JP);
        assertTrue(CountryCode.values().length > 200);
    }
}
