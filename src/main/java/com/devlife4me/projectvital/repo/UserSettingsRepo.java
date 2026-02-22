package com.devlife4me.projectvital.repo;

import com.devlife4me.projectvital.model.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSettingsRepo extends JpaRepository<UserSettings, UUID> {
}
