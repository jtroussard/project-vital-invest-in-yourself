package com.devlife4me.projectvital.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_type_id")
    private MeasurementType measurementType;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "base_unit", nullable = false)
    private String baseUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    @Builder.Default
    private com.devlife4me.projectvital.model.enums.MetricDataType dataType = com.devlife4me.projectvital.model.enums.MetricDataType.NUMERIC;
}
