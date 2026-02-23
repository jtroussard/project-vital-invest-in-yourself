package com.devlife4me.projectvital.model.dto.request;

import com.devlife4me.projectvital.model.entity.Metric;
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
    private Metric metric;
    private float value;
    private String unit;
    private String notes;

    public Long getMetricId() {
        if (metricId != null)
            return metricId;
        return metric != null ? metric.getId() : null;
    }
}
