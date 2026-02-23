package com.devlife4me.projectvital.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEntryRequest {
    private List<MetricEntryRequest> entries;
    private OffsetDateTime entryDate;
}
