package com.devlife4me.projectvital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricEntryRequest {
    private Long metricId;
    private float value;
    private String unit;
    private String notes;
}
