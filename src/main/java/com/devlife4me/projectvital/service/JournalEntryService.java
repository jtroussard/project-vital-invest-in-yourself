package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.dto.request.MetricEntryRequest;
import com.devlife4me.projectvital.model.dto.response.JournalEntryResponse;
import com.devlife4me.projectvital.model.entity.*;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.model.enums.UnitSystem;
import com.devlife4me.projectvital.repo.JournalEntryRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

        private final JournalEntryRepo journalEntryRepo;
        private final MetricRepo metricRepo;
        private final ConversionService conversionService;
        private final NutritionService nutritionService;
        private final UserSettingsService userSettingsService;

        @Transactional
        public JournalEntryResponse createMetricEntry(UUID userId, Long metricId, float value, String inputUnit,
                        OffsetDateTime entryDate, String notes) {
                Metric metric = metricRepo.findById(metricId)
                                .orElseThrow(() -> new RuntimeException("Metric not found"));

                float metricValue = conversionService.convertToMetric(value, metric.getCategory(), inputUnit);

                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .metric(metric)
                                .entryType(JournalEntryType.METRIC)
                                .value(metricValue)
                                .notes(notes)
                                .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                                .isActive(true)
                                .build();

                JournalEntry saved = journalEntryRepo.save(entry);
                return mapToResponse(saved, userSettingsService.getSettings(userId).getPreferredUnitSystem());
        }

        @Transactional
        public List<JournalEntryResponse> createBatchMetricEntries(UUID userId, List<MetricEntryRequest> entries,
                        OffsetDateTime entryDate) {
                UnitSystem preferredSystem = userSettingsService.getSettings(userId).getPreferredUnitSystem();
                List<JournalEntry> journalEntries = entries.stream()
                                .map(req -> {
                                        Metric metric = metricRepo.findById(req.getMetricId())
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Metric not found: " + req.getMetricId()));

                                        float metricValue = conversionService.convertToMetric(req.getValue(),
                                                        metric.getCategory(), req.getUnit());

                                        return JournalEntry.builder()
                                                        .userId(userId)
                                                        .metric(metric)
                                                        .entryType(JournalEntryType.METRIC)
                                                        .value(metricValue)
                                                        .notes(req.getNotes())
                                                        .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                                                        .isActive(true)
                                                        .build();
                                })
                                .toList();

                return journalEntryRepo.saveAll(journalEntries).stream()
                                .map(entry -> mapToResponse(entry, preferredSystem))
                                .toList();
        }

        @Transactional
        public JournalEntryResponse createMealEntry(UUID userId, Meal meal, OffsetDateTime entryDate) {
                nutritionService.calculateTotals(meal);
                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .entryType(JournalEntryType.MEAL)
                                .meal(meal)
                                .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                                .isActive(true)
                                .build();

                JournalEntry saved = journalEntryRepo.save(entry);
                return mapToResponse(saved, userSettingsService.getSettings(userId).getPreferredUnitSystem());
        }

        @Transactional
        public JournalEntryResponse createNoteEntry(UUID userId, String notes, OffsetDateTime entryDate) {
                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .entryType(JournalEntryType.NOTE)
                                .notes(notes)
                                .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                                .isActive(true)
                                .build();

                JournalEntry saved = journalEntryRepo.save(entry);
                return mapToResponse(saved, userSettingsService.getSettings(userId).getPreferredUnitSystem());
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

        private JournalEntryResponse mapToResponse(JournalEntry entry, UnitSystem preferredSystem) {
                JournalEntryResponse.JournalEntryResponseBuilder builder = JournalEntryResponse.builder()
                                .id(entry.getId())
                                .userId(entry.getUserId())
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
