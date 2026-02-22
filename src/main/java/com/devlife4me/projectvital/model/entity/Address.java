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
public class Address {
    private String street1;
    private String street2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;

    public String getDisplayValue() {
        StringBuilder sb = new StringBuilder();
        if (street1 != null && !street1.isEmpty())
            sb.append(street1).append(", ");
        if (street2 != null && !street2.isEmpty())
            sb.append(street2).append(", ");
        if (city != null && !city.isEmpty())
            sb.append(city);
        if (stateProvince != null && !stateProvince.isEmpty()) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ')
                sb.append(", ");
            sb.append(stateProvince);
        }
        if (postalCode != null && !postalCode.isEmpty())
            sb.append(" ").append(postalCode);
        if (country != null && !country.isEmpty()) {
            com.devlife4me.projectvital.model.enums.CountryCode cc = com.devlife4me.projectvital.model.enums.CountryCode
                    .fromCode(country);
            sb.append(", ").append(
                    cc == com.devlife4me.projectvital.model.enums.CountryCode.UNKNOWN ? country : cc.getDisplayName());
        }
        return sb.toString().trim();
    }
}
