package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.dto.MetricEntryRequest;
import com.devlife4me.projectvital.model.entity.*;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.repo.JournalEntryRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest {

    @Mock
    private JournalEntryRepo journalEntryRepo;

    @Mock
    private MetricRepo metricRepo;

    @Mock
    private ConversionService conversionService;

    @Mock
    private NutritionService nutritionService;

    @InjectMocks
    private JournalEntryService journalEntryService;

    @Test
    void createMetricEntry_CalculatesValueAndSaves() {
        UUID userId = UUID.randomUUID();
        Long metricId = 1L;
        Metric metric = Metric.builder()
                .id(metricId)
                .measurementType(MeasurementType.builder().name("Weight").build())
                .build();

        when(metricRepo.findById(metricId)).thenReturn(Optional.of(metric));
        when(conversionService.convertToMetric(200.0f, "Weight", "lb")).thenReturn(90.71f);
        when(journalEntryRepo.save(any(JournalEntry.class))).thenAnswer(i -> i.getArguments()[0]);

        JournalEntry result = journalEntryService.createMetricEntry(userId, metricId, 200.0f, "lb", null, "Weight log");

        assertNotNull(result);
        assertEquals(90.71f, result.getValue());
        assertEquals(JournalEntryType.METRIC, result.getEntryType());
        verify(journalEntryRepo).save(any(JournalEntry.class));
    }

    @Test
    void createBatchMetricEntries_SavesAll() {
        UUID userId = UUID.randomUUID();
        Long metricId = 1L;
        Metric metric = Metric.builder()
                .id(metricId)
                .measurementType(MeasurementType.builder().name("Weight").build())
                .build();
        MetricEntryRequest req = new MetricEntryRequest();
        req.setMetricId(metricId);
        req.setValue(200.0f);
        req.setUnit("lb");

        when(metricRepo.findById(metricId)).thenReturn(Optional.of(metric));
        when(conversionService.convertToMetric(200.0f, "Weight", "lb")).thenReturn(90.71f);
        when(journalEntryRepo.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

        List<JournalEntry> results = journalEntryService.createBatchMetricEntries(userId,
                Collections.singletonList(req), null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(90.71f, results.get(0).getValue());
        verify(journalEntryRepo).saveAll(anyList());
    }

    @Test
    void createMealEntry_CalculatesNutritionAndSaves() {
        UUID userId = UUID.randomUUID();
        Meal meal = Meal.builder()
                .items(Collections.singletonList(new MealItem()))
                .build();

        when(journalEntryRepo.save(any(JournalEntry.class))).thenAnswer(i -> i.getArguments()[0]);

        JournalEntry result = journalEntryService.createMealEntry(userId, meal, null);

        assertNotNull(result);
        assertEquals(JournalEntryType.MEAL, result.getEntryType());
        verify(nutritionService).calculateTotals(meal);
        verify(journalEntryRepo).save(any(JournalEntry.class));
    }

    @Test
    void createNoteEntry_SavesAsNote() {
        UUID userId = UUID.randomUUID();
        String notes = "Important morning observation";
        when(journalEntryRepo.save(any(JournalEntry.class))).thenAnswer(i -> i.getArguments()[0]);

        JournalEntry result = journalEntryService.createNoteEntry(userId, notes, null);

        assertNotNull(result);
        assertEquals(notes, result.getNotes());
        assertEquals(JournalEntryType.NOTE, result.getEntryType());
        assertTrue(result.getIsActive());
        verify(journalEntryRepo).save(any(JournalEntry.class));
    }

    @Test
    void getEntries_ReturnsActiveEntriesOrdered() {
        UUID userId = UUID.randomUUID();
        List<JournalEntry> entries = Arrays.asList(new JournalEntry(), new JournalEntry());
        when(journalEntryRepo.findByUserIdAndIsActiveTrueOrderByEntryDateDesc(userId)).thenReturn(entries);

        List<JournalEntry> result = journalEntryService.getEntries(userId);

        assertEquals(2, result.size());
        verify(journalEntryRepo).findByUserIdAndIsActiveTrueOrderByEntryDateDesc(userId);
    }

    @Test
    void getEntry_ReturnsOptionalEntry() {
        Long id = 1L;
        JournalEntry entry = new JournalEntry();
        when(journalEntryRepo.findById(id)).thenReturn(Optional.of(entry));

        Optional<JournalEntry> result = journalEntryService.getEntry(id);

        assertTrue(result.isPresent());
        assertEquals(entry, result.get());
    }

    @Test
    void deleteEntry_UpdatesIsActiveToFalse() {
        Long id = 1L;
        JournalEntry entry = JournalEntry.builder().isActive(true).build();
        when(journalEntryRepo.findById(id)).thenReturn(Optional.of(entry));

        journalEntryService.deleteEntry(id);

        assertFalse(entry.getIsActive());
        verify(journalEntryRepo).save(entry);
    }
}
