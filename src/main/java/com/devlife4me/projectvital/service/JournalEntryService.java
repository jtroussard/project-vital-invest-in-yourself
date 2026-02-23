package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.dto.request.MetricEntryRequest;
import com.devlife4me.projectvital.model.entity.JournalEntry;
import com.devlife4me.projectvital.model.entity.Meal;
import com.devlife4me.projectvital.model.entity.MealItem;
import com.devlife4me.projectvital.model.entity.Metric;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.repo.JournalEntryRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepo journalEntryRepo;
    private final MetricRepo metricRepo;
    private final ConversionService conversionService;
    private final NutritionService nutritionService;

    @Transactional
    public JournalEntry createMetricEntry(UUID userId, Long metricId, float value, String inputUnit,
            OffsetDateTime entryDate, String notes) {
        Metric metric = metricRepo.findById(metricId)
                .orElseThrow(() -> new RuntimeException("Metric not found"));

        float metricValue = conversionService.convertToMetric(value, metric.getMeasurementType().getName(), inputUnit);

        JournalEntry entry = JournalEntry.builder()
                .userId(userId)
                .metric(metric)
                .value(metricValue)
                .entryType(JournalEntryType.METRIC)
                .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                .notes(notes)
                .isActive(true)
                .build();

        return journalEntryRepo.save(entry);
    }

    @Transactional
    public List<JournalEntry> createBatchMetricEntries(UUID userId, List<MetricEntryRequest> requests,
            OffsetDateTime entryDate) {
        OffsetDateTime date = entryDate != null ? entryDate : OffsetDateTime.now();
        List<JournalEntry> entries = requests.stream().map(req -> {
            Metric metric = metricRepo.findById(req.getMetricId())
                    .orElseThrow(() -> new RuntimeException("Metric not found: " + req.getMetricId()));

            float metricValue = conversionService.convertToMetric(req.getValue(),
                    metric.getMeasurementType().getName(), req.getUnit());

            return JournalEntry.builder()
                    .userId(userId)
                    .metric(metric)
                    .value(metricValue)
                    .entryType(JournalEntryType.METRIC)
                    .entryDate(date)
                    .notes(req.getNotes())
                    .isActive(true)
                    .build();
        }).collect(Collectors.toList());

        return journalEntryRepo.saveAll(entries);
    }

    @Transactional
    public JournalEntry createMealEntry(UUID userId, Meal mealData, OffsetDateTime entryDate) {
        // Calculate totals for the meal
        for (MealItem item : mealData.getItems()) {
            item.setMeal(mealData);
        }
        nutritionService.calculateTotals(mealData);

        JournalEntry entry = JournalEntry.builder()
                .userId(userId)
                .meal(mealData)
                .entryType(JournalEntryType.MEAL)
                .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                .isActive(true)
                .build();

        return journalEntryRepo.save(entry);
    }

    @Transactional
    public JournalEntry createNoteEntry(UUID userId, String notes, OffsetDateTime entryDate) {
        JournalEntry entry = JournalEntry.builder()
                .userId(userId)
                .notes(notes)
                .entryType(JournalEntryType.NOTE)
                .entryDate(entryDate != null ? entryDate : OffsetDateTime.now())
                .isActive(true)
                .build();

        return journalEntryRepo.save(entry);
    }

    public List<JournalEntry> getEntries(UUID userId) {
        return journalEntryRepo.findByUserIdAndIsActiveTrueOrderByEntryDateDesc(userId);
    }

    public Optional<JournalEntry> getEntry(Long id) {
        return journalEntryRepo.findById(id);
    }

    @Transactional
    public void deleteEntry(Long id) {
        journalEntryRepo.findById(id).ifPresent(entry -> {
            entry.setIsActive(false);
            journalEntryRepo.save(entry);
        });
    }
}
