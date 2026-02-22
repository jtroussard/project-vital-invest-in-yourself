package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberService {

    /**
     * Formats a PhoneNumber object into a human-readable string.
     * Default US format: +{country} ({area}) {number} x{ext}
     */
    public String format(PhoneNumber phone) {
        if (phone == null)
            return "";

        StringBuilder sb = new StringBuilder();

        if (phone.getCountryCode() != null && !phone.getCountryCode().isEmpty()) {
            sb.append("+").append(phone.getCountryCode()).append(" ");
        }

        if (phone.getAreaCode() != null && !phone.getAreaCode().isEmpty()) {
            sb.append("(").append(phone.getAreaCode()).append(") ");
        }

        if (phone.getNumber() != null && !phone.getNumber().isEmpty()) {
            String raw = phone.getNumber();
            if (raw.length() == 7) {
                sb.append(raw.substring(0, 3)).append("-").append(raw.substring(3));
            } else {
                sb.append(raw);
            }
        }

        if (phone.getExtension() != null && !phone.getExtension().isEmpty()) {
            sb.append(" x").append(phone.getExtension());
        }

        return sb.toString().trim();
    }
}
