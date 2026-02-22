package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.PhoneNumber;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberServiceTest {

    private final PhoneNumberService phoneNumberService = new PhoneNumberService();

    @Test
    void format_FullPhoneNumber_ReturnsFormattedString() {
        PhoneNumber phone = new PhoneNumber();
        phone.setCountryCode("1");
        phone.setAreaCode("555");
        phone.setNumber("1234567");
        phone.setExtension("99");

        String formatted = phoneNumberService.format(phone);
        assertEquals("+1 (555) 123-4567 x99", formatted);
    }

    @Test
    void format_MinimalPhoneNumber_ReturnsFormattedString() {
        PhoneNumber phone = new PhoneNumber();
        phone.setNumber("1234567");

        String formatted = phoneNumberService.format(phone);
        assertEquals("123-4567", formatted);
    }

    @Test
    void format_LongNumber_ReturnsAsIs() {
        PhoneNumber phone = new PhoneNumber();
        phone.setNumber("1234567890");

        String formatted = phoneNumberService.format(phone);
        assertEquals("1234567890", formatted);
    }

    @Test
    void format_NullPhone_ReturnsEmptyString() {
        assertEquals("", phoneNumberService.format(null));
    }

    @Test
    void format_PartialPhone_ReturnsFormattedString() {
        PhoneNumber phone = new PhoneNumber();
        phone.setAreaCode("555");
        phone.setNumber("1234567");

        String formatted = phoneNumberService.format(phone);
        assertEquals("(555) 123-4567", formatted);
    }
}
