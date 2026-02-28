package com.devlife4me.projectvital.service;

import com.devlife4me.projectvital.model.entity.UserSettings;
import com.devlife4me.projectvital.repo.UserSettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepo userSettingsRepo;

    public UserSettings getSettings(UUID userId) {
        return userSettingsRepo.findById(userId)
                .orElseGet(() -> {
                    UserSettings settings = UserSettings.builder()
                            .userId(userId)
                            .build();
                    return userSettingsRepo.save(settings);
                });
    }

    @Transactional
    public UserSettings updateSettings(UUID userId, UserSettings settingsData) {
        UserSettings settings = getSettings(userId);
        if (settingsData.getPreferredUnitSystem() != null) {
            settings.setPreferredUnitSystem(settingsData.getPreferredUnitSystem());
        }
        if (settingsData.getDefaultJournalMetricIds() != null) {
            settings.setDefaultJournalMetricIds(settingsData.getDefaultJournalMetricIds());
        }
        return userSettingsRepo.save(settings);
    }

    @Transactional
    public UserSettings updateDefaultJournalMetrics(UUID userId, List<Long> defaultJournalMetricIds) {
        UserSettings settings = getSettings(userId);
        settings.setDefaultJournalMetricIds(defaultJournalMetricIds);
        return userSettingsRepo.save(settings);
    }
}
