package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.MeasurementType;
import com.devlife4me.projectvital.model.entity.Metric;
import com.devlife4me.projectvital.model.enums.MetricDataType;
import com.devlife4me.projectvital.service.MetricService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricController.class)
class MetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetricService metricService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getMetrics_ReturnsList() throws Exception {
        Metric metric = new Metric();
        metric.setId(1L);
        metric.setName("Weight");

        when(metricService.getAllMetrics()).thenReturn(Arrays.asList(metric));

        mockMvc.perform(get("/api/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Weight"));
    }

    @Test
    @WithMockUser
    void createMetric_ReturnsCreatedMetric() throws Exception {
        MetricController.MetricRequest request = new MetricController.MetricRequest();
        request.setName("Height");
        request.setBaseUnit("cm");
        request.setMeasurementTypeId(1L);
        request.setDataType(MetricDataType.NUMERIC);

        Metric metric = new Metric();
        metric.setId(2L);
        metric.setName("Height");

        when(metricService.createMetric(anyString(), anyString(), any(), any(), any()))
                .thenReturn(metric);

        mockMvc.perform(post("/api/metrics")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Height"));
    }

    @Test
    @WithMockUser
    void getMeasurementTypes_ReturnsList() throws Exception {
        MeasurementType type = new MeasurementType();
        type.setId(1L);
        type.setName("Body Composition");

        when(metricService.getAllMeasurementTypes()).thenReturn(Arrays.asList(type));

        mockMvc.perform(get("/api/metrics/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Body Composition"));
    }

    @Test
    @WithMockUser
    void createMeasurementType_ReturnsCreatedType() throws Exception {
        String name = "Cardiovascular";
        MeasurementType type = new MeasurementType();
        type.setId(2L);
        type.setName(name);

        when(metricService.createMeasurementType(anyString())).thenReturn(type);

        mockMvc.perform(post("/api/metrics/types")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value(name));
    }
}
