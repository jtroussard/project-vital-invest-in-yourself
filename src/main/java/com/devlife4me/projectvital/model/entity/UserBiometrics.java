package com.devlife4me.projectvital.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.devlife4me.projectvital.model.enums.Gender;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_biometrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBiometrics {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "height")
    private Float height;

    @Column(name = "current_weight")
    private Float currentWeight;

    @Column(name = "target_weight")
    private Float targetWeight;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
