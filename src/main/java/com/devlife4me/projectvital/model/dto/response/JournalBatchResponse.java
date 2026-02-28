package com.devlife4me.projectvital.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalBatchResponse {
    private Long id;
    private UUID userId;
    private OffsetDateTime entryDate;
    private String notes;
    private List<JournalEntryResponse> entries;
}
