package com.devlife4me.projectvital.model.dto.response;

import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.model.entity.Meal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryResponse {
    private Long id;
    private UUID userId;
    private Long metricId;
    private String metricName;
    private JournalEntryType entryType;
    private Float value;
    private Float displayValue;
    private String displayUnit;
    private String notes;
    private OffsetDateTime entryDate;
    private Meal meal;
}
