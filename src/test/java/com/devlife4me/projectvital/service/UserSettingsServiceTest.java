package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.UserSettings;
import com.devlife4me.projectvital.repo.UserSettingsRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepo userSettingsRepo;

    @InjectMocks
    private UserSettingsService userSettingsService;

    @Test
    void getSettings_ReturnsExistingSettings() {
        UUID userId = UUID.randomUUID();
        UserSettings settings = UserSettings.builder().userId(userId).build();
        when(userSettingsRepo.findById(userId)).thenReturn(Optional.of(settings));

        UserSettings result = userSettingsService.getSettings(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(userSettingsRepo, never()).save(any());
    }

    @Test
    void getSettings_CreatesNewSettingsIfNotFound() {
        UUID userId = UUID.randomUUID();
        when(userSettingsRepo.findById(userId)).thenReturn(Optional.empty());
        when(userSettingsRepo.save(any(UserSettings.class))).thenAnswer(i -> i.getArguments()[0]);

        UserSettings result = userSettingsService.getSettings(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(userSettingsRepo).save(any(UserSettings.class));
    }

    @Test
    void updateDefaultJournalMetrics_UpdatesAndSaves() {
        UUID userId = UUID.randomUUID();
        List<Long> metricIds = Arrays.asList(4L, 5L, 6L);
        UserSettings settings = UserSettings.builder().userId(userId).build();

        when(userSettingsRepo.findById(userId)).thenReturn(Optional.of(settings));
        when(userSettingsRepo.save(any(UserSettings.class))).thenAnswer(i -> i.getArguments()[0]);

        UserSettings result = userSettingsService.updateDefaultJournalMetrics(userId, metricIds);

        assertNotNull(result);
        assertEquals(metricIds, result.getDefaultJournalMetricIds());
        verify(userSettingsRepo).save(settings);
    }
}
