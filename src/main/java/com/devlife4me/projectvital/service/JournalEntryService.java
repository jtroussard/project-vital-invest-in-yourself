package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.dto.request.MetricEntryRequest;
import com.devlife4me.projectvital.model.dto.response.JournalBatchResponse;
import com.devlife4me.projectvital.model.dto.response.JournalEntryResponse;
import com.devlife4me.projectvital.model.entity.*;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.model.enums.UnitSystem;
import com.devlife4me.projectvital.repo.JournalBatchRepo;
import com.devlife4me.projectvital.repo.JournalEntryRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import com.devlife4me.projectvital.util.SanitizationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalEntryService {

        private final JournalEntryRepo journalEntryRepo;
        private final JournalBatchRepo journalBatchRepo;
        private final MetricRepo metricRepo;
        private final ConversionService conversionService;
        private final NutritionService nutritionService;
        private final UserSettingsService userSettingsService;

        @Transactional
        public JournalEntryResponse createMetricEntry(UUID userId, Long metricId, float value, String inputUnit,
                        OffsetDateTime entryDate, String notes) {
                Metric metric = metricRepo.findById(metricId)
                                .orElseThrow(() -> new RuntimeException("Metric not found"));

                log.info("Conversion arguments: value-{} category-{} inputUnit-{}", value, metric.getCategory(),
                                inputUnit);
                float metricValue = conversionService.convertToMetric(value, metric.getCategory(), inputUnit);

                OffsetDateTime date = entryDate != null ? entryDate : OffsetDateTime.now();
                String sanitizedNotes = SanitizationUtils.sanitize(notes);
                JournalBatch batch = journalBatchRepo.save(JournalBatch.builder()
                                .userId(userId)
                                .entryDate(date)
                                .notes(sanitizedNotes)
                                .build());

                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .batch(batch)
                                .metric(metric)
                                .entryType(JournalEntryType.METRIC)
                                .value(metricValue)
                                .notes(sanitizedNotes)
                                .entryDate(date)
                                .isActive(true)
                                .build();

                JournalEntry saved = journalEntryRepo.save(entry);
                return mapToResponse(saved, userSettingsService.getSettings(userId).getPreferredUnitSystem());
        }

        @Transactional
        public List<JournalEntryResponse> createBatchMetricEntries(UUID userId, List<MetricEntryRequest> entries,
                        OffsetDateTime entryDate, String notes) {
                UnitSystem preferredSystem = userSettingsService.getSettings(userId).getPreferredUnitSystem();
                OffsetDateTime date = entryDate != null ? entryDate : OffsetDateTime.now();

                String sanitizedBatchNotes = SanitizationUtils.sanitize(notes);
                JournalBatch batch = journalBatchRepo.save(JournalBatch.builder()
                                .userId(userId)
                                .entryDate(date)
                                .notes(sanitizedBatchNotes)
                                .build());

                List<JournalEntry> journalEntries = entries.stream()
                                .map(req -> {
                                        Metric metric = metricRepo.findById(req.getMetricId())
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Metric not found: " + req.getMetricId()));

                                        float metricValue = conversionService.convertToMetric(req.getValue(),
                                                        metric.getCategory(), req.getUnit());

                                        return JournalEntry.builder()
                                                        .userId(userId)
                                                        .batch(batch)
                                                        .metric(metric)
                                                        .entryType(JournalEntryType.METRIC)
                                                        .value(metricValue)
                                                        .notes(SanitizationUtils.sanitize(req.getNotes()))
                                                        .entryDate(date)
                                                        .isActive(true)
                                                        .build();
                                })
                                .toList();

                return journalEntryRepo.saveAll(journalEntries).stream()
                                .map(entry -> mapToResponse(entry, preferredSystem))
                                .toList();
        }

        @Transactional
        public JournalEntryResponse createMealEntry(UUID userId, Meal meal, OffsetDateTime entryDate, String notes) {
                nutritionService.calculateTotals(meal);
                OffsetDateTime date = entryDate != null ? entryDate : OffsetDateTime.now();

                String sanitizedNotes = SanitizationUtils.sanitize(notes);
                JournalBatch batch = journalBatchRepo.save(JournalBatch.builder()
                                .userId(userId)
                                .entryDate(date)
                                .notes(sanitizedNotes)
                                .build());

                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .batch(batch)
                                .entryType(JournalEntryType.MEAL)
                                .meal(meal)
                                .notes(sanitizedNotes)
                                .entryDate(date)
                                .isActive(true)
                                .build();

                JournalEntry saved = journalEntryRepo.save(entry);
                return mapToResponse(saved, userSettingsService.getSettings(userId).getPreferredUnitSystem());
        }

        @Transactional
        public JournalEntryResponse createNoteEntry(UUID userId, String notes, OffsetDateTime entryDate) {
                OffsetDateTime date = entryDate != null ? entryDate : OffsetDateTime.now();

                String sanitizedNotes = SanitizationUtils.sanitize(notes);
                JournalBatch batch = journalBatchRepo.save(JournalBatch.builder()
                                .userId(userId)
                                .entryDate(date)
                                .notes(sanitizedNotes)
                                .build());

                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .batch(batch)
                                .entryType(JournalEntryType.NOTE)
                                .notes(sanitizedNotes)
                                .entryDate(date)
                                .isActive(true)
                                .build();

                JournalEntry saved = journalEntryRepo.save(entry);
                return mapToResponse(saved, userSettingsService.getSettings(userId).getPreferredUnitSystem());
        }

        public Optional<JournalBatchResponse> getBatch(UUID userId, Long batchId) {
                UnitSystem preferredSystem = userSettingsService.getSettings(userId).getPreferredUnitSystem();
                return journalBatchRepo.findById(batchId)
                                .filter(batch -> batch.getUserId().equals(userId))
                                .map(batch -> mapToBatchResponse(batch, preferredSystem));
        }

        public List<JournalBatchResponse> getBatches(UUID userId) {
                UnitSystem preferredSystem = userSettingsService.getSettings(userId).getPreferredUnitSystem();
                return journalBatchRepo.findByUserIdOrderByEntryDateDesc(userId).stream()
                                .map(batch -> mapToBatchResponse(batch, preferredSystem))
                                .toList();
        }

        public Page<JournalBatchResponse> getBatches(UUID userId, int page, int size) {
                UnitSystem preferredSystem = userSettingsService.getSettings(userId).getPreferredUnitSystem();
                Pageable pageable = PageRequest.of(page, size, Sort.by("entryDate").descending());

                return journalBatchRepo.findByUserId(userId, pageable)
                                .map(batch -> mapToBatchResponse(batch, preferredSystem));
        }

        public List<JournalEntryResponse> getEntries(UUID userId) {
                UnitSystem preferredSystem = userSettingsService.getSettings(userId).getPreferredUnitSystem();
                return journalEntryRepo.findByUserIdAndIsActiveTrueOrderByEntryDateDesc(userId).stream()
                                .map(entry -> mapToResponse(entry, preferredSystem))
                                .toList();
        }

        public Optional<JournalEntryResponse> getEntry(Long id) {
                return journalEntryRepo.findById(id)
                                .map(entry -> mapToResponse(entry,
                                                userSettingsService.getSettings(entry.getUserId())
                                                                .getPreferredUnitSystem()));
        }

        @Transactional
        public void deleteEntry(Long id) {
                journalEntryRepo.findById(id).ifPresent(entry -> {
                        entry.setIsActive(false);
                        journalEntryRepo.save(entry);
                });
        }

        private JournalBatchResponse mapToBatchResponse(JournalBatch batch, UnitSystem preferredSystem) {
                return JournalBatchResponse.builder()
                                .id(batch.getId())
                                .userId(batch.getUserId())
                                .entryDate(batch.getEntryDate())
                                .notes(batch.getNotes())
                                .entries(batch.getEntries().stream()
                                                .filter(JournalEntry::getIsActive)
                                                .map(entry -> mapToResponse(entry, preferredSystem))
                                                .toList())
                                .build();
        }

        private JournalEntryResponse mapToResponse(JournalEntry entry, UnitSystem preferredSystem) {
                JournalEntryResponse.JournalEntryResponseBuilder builder = JournalEntryResponse.builder()
                                .id(entry.getId())
                                .userId(entry.getUserId())
                                .batchId(entry.getBatch() != null ? entry.getBatch().getId() : null)
                                .entryType(entry.getEntryType())
                                .notes(entry.getNotes())
                                .entryDate(entry.getEntryDate())
                                .meal(entry.getMeal());

                if (entry.getMetric() != null) {
                        Metric metric = entry.getMetric();
                        builder.metricId(metric.getId())
                                        .metricName(metric.getName())
                                        .value(entry.getValue());

                        String displayUnit = conversionService.getDisplayUnit(metric.getCategory(), preferredSystem);
                        float displayValue = conversionService.convertFromMetric(entry.getValue(), metric.getCategory(),
                                        displayUnit);

                        builder.displayValue(displayValue)
                                        .displayUnit(displayUnit);
                }

                return builder.build();
        }
}
