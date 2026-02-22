package com.devlife4me.projectvital.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumber {
    private String countryCode;
    private String areaCode;
    private String number;
    private String extension;

    public String getDisplayValue() {
        StringBuilder sb = new StringBuilder();
        if (countryCode != null && !countryCode.isEmpty()) {
            sb.append("+").append(countryCode).append(" ");
        }
        if (areaCode != null && !areaCode.isEmpty()) {
            sb.append("(").append(areaCode).append(") ");
        }
        if (number != null && !number.isEmpty()) {
            if (number.length() == 7) {
                sb.append(number.substring(0, 3)).append("-").append(number.substring(3));
            } else {
                sb.append(number);
            }
        }
        if (extension != null && !extension.isEmpty()) {
            sb.append(" x").append(extension);
        }
        return sb.toString().trim();
    }
}
