package com.devlife4me.projectvital.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.devlife4me.projectvital.model.enums.MetricDataType;
import com.devlife4me.projectvital.model.enums.QuantityCategory;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "measurement_type_id")
    private MeasurementType measurementType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @Builder.Default
    private QuantityCategory category = QuantityCategory.SCALAR;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "base_unit", nullable = false)
    private String baseUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false)
    @Builder.Default
    private MetricDataType dataType = MetricDataType.NUMERIC;
}
