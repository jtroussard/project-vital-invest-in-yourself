package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.Address;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressServiceTest {

    private final AddressService addressService = new AddressService();

    @Test
    void format_FullAddress_ReturnsFormattedString() {
        Address address = new Address();
        address.setStreet1("123 Main St");
        address.setStreet2("Suite 100");
        address.setCity("Metropolis");
        address.setStateProvince("NY");
        address.setPostalCode("12345");
        address.setCountry("US");

        String formatted = addressService.format(address);
        assertEquals("123 Main St\nSuite 100\nMetropolis, NY 12345\nUnited States", formatted);
    }

    @Test
    void format_MinimalAddress_ReturnsFormattedString() {
        Address address = new Address();
        address.setStreet1("123 Main St");
        address.setCity("Metropolis");
        address.setCountry("US");

        String formatted = addressService.format(address);
        assertEquals("123 Main St\nMetropolis\nUnited States", formatted);
    }

    @Test
    void format_NullAddress_ReturnsEmptyString() {
        assertEquals("", addressService.format(null));
    }

    @Test
    void format_UnknownCountry_ReturnsRawCode() {
        Address address = new Address();
        address.setStreet1("123 Main St");
        address.setCountry("ZZ");

        String formatted = addressService.format(address);
        assertEquals("123 Main St\nZZ", formatted);
    }

    @Test
    void formatSingleLine_FullAddress_ReturnsSingleLineString() {
        Address address = new Address();
        address.setStreet1("123 Main St");
        address.setStreet2("Suite 100");
        address.setCity("Metropolis");
        address.setStateProvince("NY");
        address.setPostalCode("12345");
        address.setCountry("US");

        String formatted = addressService.formatSingleLine(address);
        assertEquals("123 Main St, Suite 100, Metropolis, NY 12345, United States", formatted);
    }
}
