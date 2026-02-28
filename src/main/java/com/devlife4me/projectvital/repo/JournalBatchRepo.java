package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.entity.JournalBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface JournalBatchRepo extends JpaRepository<JournalBatch, Long> {
    List<JournalBatch> findByUserIdOrderByEntryDateDesc(UUID userId);

    Page<JournalBatch> findByUserId(UUID userId, Pageable pageable);
}
