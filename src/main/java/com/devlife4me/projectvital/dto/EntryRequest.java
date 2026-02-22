package com.devlife4me.projectvital.dto;

import com.devlife4me.projectvital.model.entity.Meal;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryRequest {
    @Builder.Default
    private JournalEntryType entryType = JournalEntryType.METRIC;
    private Long metricId;
    private Float value;
    private String unit;
    private String notes;
    private OffsetDateTime entryDate;
    private Meal meal;
}
