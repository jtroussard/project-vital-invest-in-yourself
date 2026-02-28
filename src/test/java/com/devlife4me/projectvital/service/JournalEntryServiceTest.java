package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.dto.request.MetricEntryRequest;
import com.devlife4me.projectvital.model.dto.response.JournalBatchResponse;
import com.devlife4me.projectvital.model.dto.response.JournalEntryResponse;
import com.devlife4me.projectvital.model.entity.*;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.model.enums.QuantityCategory;
import com.devlife4me.projectvital.model.enums.UnitSystem;
import com.devlife4me.projectvital.repo.JournalBatchRepo;
import com.devlife4me.projectvital.repo.JournalEntryRepo;
import com.devlife4me.projectvital.repo.MetricRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest {

        @Mock
        private JournalEntryRepo journalEntryRepo;

        @Mock
        private JournalBatchRepo journalBatchRepo;

        @Mock
        private MetricRepo metricRepo;

        @Mock
        private ConversionService conversionService;

        @Mock
        private NutritionService nutritionService;

        @Mock
        private UserSettingsService userSettingsService;

        @InjectMocks
        private JournalEntryService journalEntryService;

        @Test
        void createMetricEntry_CalculatesValueAndSaves() {
                UUID userId = UUID.randomUUID();
                Long metricId = 1L;
                Metric metric = Metric.builder()
                                .id(metricId)
                                .category(QuantityCategory.MASS)
                                .measurementType(MeasurementType.builder().name("Weight").build())
                                .name("Weight")
                                .build();

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(metricRepo.findById(metricId)).thenReturn(Optional.of(metric));
                when(conversionService.convertToMetric(200.0f, QuantityCategory.MASS, "lb")).thenReturn(90.71f);
                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(conversionService.getDisplayUnit(QuantityCategory.MASS, UnitSystem.METRIC)).thenReturn("kg");
                when(conversionService.convertFromMetric(90.71f, QuantityCategory.MASS, "kg")).thenReturn(90.71f);
                when(journalBatchRepo.save(any(JournalBatch.class))).thenAnswer(i -> {
                        JournalBatch b = i.getArgument(0);
                        b.setId(10L);
                        return b;
                });
                when(journalEntryRepo.save(any(JournalEntry.class))).thenAnswer(i -> {
                        JournalEntry e = i.getArgument(0);
                        e.setId(1L);
                        return e;
                });

                JournalEntryResponse result = journalEntryService.createMetricEntry(userId, metricId, 200.0f, "lb",
                                null,
                                "Weight log");

                assertNotNull(result);
                assertEquals(90.71f, result.getValue());
                assertEquals(90.71f, result.getDisplayValue());
                assertEquals("kg", result.getDisplayUnit());
                assertEquals(JournalEntryType.METRIC, result.getEntryType());
        }

        @Test
        void createBatchMetricEntries_SavesAll() {
                UUID userId = UUID.randomUUID();
                Long metricId = 1L;
                Metric metric = Metric.builder()
                                .id(metricId)
                                .category(QuantityCategory.MASS)
                                .measurementType(MeasurementType.builder().name("Weight").build())
                                .name("Weight")
                                .build();
                MetricEntryRequest req = new MetricEntryRequest();
                req.setMetricId(metricId);
                req.setValue(200.0f);
                req.setUnit("lb");

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(metricRepo.findById(metricId)).thenReturn(Optional.of(metric));
                when(conversionService.convertToMetric(200.0f, QuantityCategory.MASS, "lb")).thenReturn(90.71f);
                when(conversionService.getDisplayUnit(QuantityCategory.MASS, UnitSystem.METRIC)).thenReturn("kg");
                when(conversionService.convertFromMetric(90.71f, QuantityCategory.MASS, "kg")).thenReturn(90.71f);
                when(journalBatchRepo.save(any(JournalBatch.class))).thenAnswer(i -> {
                        JournalBatch b = i.getArgument(0);
                        b.setId(10L);
                        return b;
                });
                when(journalEntryRepo.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

                List<JournalEntryResponse> results = journalEntryService.createBatchMetricEntries(userId,
                                Collections.singletonList(req), null, null);

                assertNotNull(results);
                assertEquals(1, results.size());
                assertEquals(90.71f, results.get(0).getValue());
        }

        @Test
        void createMealEntry_CalculatesNutritionAndSaves() {
                UUID userId = UUID.randomUUID();
                Meal meal = Meal.builder()
                                .items(Collections.singletonList(new MealItem()))
                                .build();

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(journalBatchRepo.save(any(JournalBatch.class))).thenAnswer(i -> {
                        JournalBatch b = i.getArgument(0);
                        b.setId(10L);
                        return b;
                });
                when(journalEntryRepo.save(any(JournalEntry.class))).thenAnswer(i -> i.getArgument(0));

                JournalEntryResponse result = journalEntryService.createMealEntry(userId, meal, null, null);

                assertNotNull(result);
                assertEquals(JournalEntryType.MEAL, result.getEntryType());
                verify(nutritionService).calculateTotals(meal);
        }

        @Test
        void createNoteEntry_SavesAsNote() {
                UUID userId = UUID.randomUUID();
                String notes = "Important morning observation";

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(journalBatchRepo.save(any(JournalBatch.class))).thenAnswer(i -> {
                        JournalBatch b = i.getArgument(0);
                        b.setId(10L);
                        return b;
                });
                when(journalEntryRepo.save(any(JournalEntry.class))).thenAnswer(i -> i.getArgument(0));

                JournalEntryResponse result = journalEntryService.createNoteEntry(userId, notes, null);

                assertNotNull(result);
                assertEquals(notes, result.getNotes());
                assertEquals(JournalEntryType.NOTE, result.getEntryType());
        }

        @Test
        void getEntries_ReturnsActiveEntriesOrdered() {
                UUID userId = UUID.randomUUID();
                JournalEntry entry = JournalEntry.builder()
                                .userId(userId)
                                .entryType(JournalEntryType.NOTE)
                                .build();
                List<JournalEntry> entries = Arrays.asList(entry);

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(journalEntryRepo.findByUserIdAndIsActiveTrueOrderByEntryDateDesc(userId)).thenReturn(entries);

                List<JournalEntryResponse> result = journalEntryService.getEntries(userId);

                assertEquals(1, result.size());
        }

        @Test
        void getBatches_ReturnsBatches() {
                UUID userId = UUID.randomUUID();
                JournalBatch batch = JournalBatch.builder()
                                .id(10L)
                                .userId(userId)
                                .entries(Collections.emptyList())
                                .build();
                List<JournalBatch> batches = Collections.singletonList(batch);

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(journalBatchRepo.findByUserIdOrderByEntryDateDesc(userId)).thenReturn(batches);

                List<JournalBatchResponse> result = journalEntryService.getBatches(userId);

                assertEquals(1, result.size());
                assertEquals(10L, result.get(0).getId());
        }

        @Test
        void getBatches_Paginated_ReturnsPage() {
                UUID userId = UUID.randomUUID();
                JournalBatch batch = JournalBatch.builder()
                                .id(10L)
                                .userId(userId)
                                .entries(Collections.emptyList())
                                .build();
                Page<JournalBatch> batchPage = new PageImpl<>(Collections.singletonList(batch));

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);
                when(journalBatchRepo.findByUserId(eq(userId), any(Pageable.class))).thenReturn(batchPage);

                Page<JournalBatchResponse> result = journalEntryService.getBatches(userId, 0, 10);

                assertEquals(1, result.getContent().size());
                assertEquals(10L, result.getContent().get(0).getId());
        }

        @Test
        void getEntry_ReturnsOptionalEntry() {
                Long id = 1L;
                UUID userId = UUID.randomUUID();
                JournalEntry entry = JournalEntry.builder()
                                .id(id)
                                .userId(userId)
                                .entryType(JournalEntryType.NOTE)
                                .build();

                UserSettings settings = UserSettings.builder()
                                .preferredUnitSystem(UnitSystem.METRIC)
                                .build();

                when(journalEntryRepo.findById(id)).thenReturn(Optional.of(entry));
                when(userSettingsService.getSettings(userId)).thenReturn(settings);

                Optional<JournalEntryResponse> result = journalEntryService.getEntry(id);

                assertTrue(result.isPresent());
                assertEquals(id, result.get().getId());
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
