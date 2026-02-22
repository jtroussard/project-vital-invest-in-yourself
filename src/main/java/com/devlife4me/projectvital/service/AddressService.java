package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.Address;
import com.devlife4me.projectvital.model.enums.CountryCode;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    /**
     * Formats an Address object into a human-readable string.
     * Logic handles multiple lines and international country name lookup.
     */
    public String format(Address address) {
        if (address == null)
            return "";

        StringBuilder sb = new StringBuilder();

        if (address.getStreet1() != null && !address.getStreet1().isEmpty()) {
            sb.append(address.getStreet1()).append("\n");
        }

        if (address.getStreet2() != null && !address.getStreet2().isEmpty()) {
            sb.append(address.getStreet2()).append("\n");
        }

        StringBuilder line2 = new StringBuilder();
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            line2.append(address.getCity());
        }

        if (address.getStateProvince() != null && !address.getStateProvince().isEmpty()) {
            if (line2.length() > 0)
                line2.append(", ");
            line2.append(address.getStateProvince());
        }

        if (address.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
            if (line2.length() > 0)
                line2.append(" ");
            line2.append(address.getPostalCode());
        }

        if (line2.length() > 0) {
            sb.append(line2).append("\n");
        }

        if (address.getCountry() != null && !address.getCountry().isEmpty()) {
            CountryCode cc = CountryCode.fromCode(address.getCountry());
            sb.append(cc == CountryCode.UNKNOWN ? address.getCountry() : cc.getDisplayName());
        }

        return sb.toString().trim();
    }

    /**
     * One-line display version of the address.
     */
    public String formatSingleLine(Address address) {
        return format(address).replace("\n", ", ");
    }
}
