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
    public UserSettings updateDefaultMetrics(UUID userId, List<Long> defaultMetricIds) {
        UserSettings settings = getSettings(userId);
        settings.setDefaultMetricIds(defaultMetricIds);
        return userSettingsRepo.save(settings);
    }
}
