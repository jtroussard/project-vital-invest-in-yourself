package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.UserSettings;
import com.devlife4me.projectvital.service.UserSettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSettingsController.class)
class UserSettingsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserSettingsService userSettingsService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser
        void getSettings_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                UserSettings settings = UserSettings.builder()
                                .userId(userId)
                                .build();

                when(userSettingsService.getSettings(userId)).thenReturn(settings);

                mockMvc.perform(get("/api/settings")
                                .with(jwt().jwt(builder -> builder.subject(userId.toString()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(userId.toString()));
        }

        @Test
        @WithMockUser
        void updateDefaultJournalMetrics_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                List<Long> metricIds = Arrays.asList(1L, 2L, 3L);
                UserSettings settings = UserSettings.builder()
                                .userId(userId)
                                .defaultJournalMetricIds(metricIds)
                                .build();

                when(userSettingsService.updateDefaultJournalMetrics(eq(userId), eq(metricIds))).thenReturn(settings);

                mockMvc.perform(put("/api/settings/default-journal-metrics")
                                .with(csrf())
                                .with(jwt().jwt(builder -> builder.subject(userId.toString())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(metricIds)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.defaultJournalMetricIds[0]").value(1L))
                                .andExpect(jsonPath("$.defaultJournalMetricIds[1]").value(2L))
                                .andExpect(jsonPath("$.defaultJournalMetricIds[2]").value(3L));
        }
}
