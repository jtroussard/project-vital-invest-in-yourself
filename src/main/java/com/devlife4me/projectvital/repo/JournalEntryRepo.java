package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.entity.JournalEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JournalEntryRepo extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByUserIdAndIsActiveTrueOrderByEntryDateDesc(UUID userId);

    Page<JournalEntry> findByUserIdAndIsActiveTrue(UUID userId, Pageable pageable);

    List<JournalEntry> findByUserIdAndMetricIdAndIsActiveTrueOrderByEntryDateDesc(UUID userId, Long metricId);

    List<JournalEntry> findByUserIdAndEntryDateBetweenAndIsActiveTrue(UUID userId, OffsetDateTime start,
            OffsetDateTime end);
}
