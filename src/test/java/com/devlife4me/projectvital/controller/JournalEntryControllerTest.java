package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.dto.request.EntryRequest;
import com.devlife4me.projectvital.model.dto.response.JournalBatchResponse;
import com.devlife4me.projectvital.model.dto.response.JournalEntryResponse;
import com.devlife4me.projectvital.model.enums.JournalEntryType;
import com.devlife4me.projectvital.service.JournalEntryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JournalEntryController.class)
class JournalEntryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JournalEntryService journalEntryService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser
        void createMetricEntry_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                EntryRequest request = new EntryRequest();
                request.setEntryType(JournalEntryType.METRIC);
                request.setMetricId(1L);
                request.setValue(75.5f);
                request.setUnit("kg");

                JournalEntryResponse response = JournalEntryResponse.builder()
                                .id(1L)
                                .userId(userId)
                                .entryType(JournalEntryType.METRIC)
                                .value(75.5f)
                                .build();

                when(journalEntryService.createMetricEntry(eq(userId), eq(1L), eq(75.5f), eq("kg"), any(), any()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/journal")
                                .with(jwt().jwt(builder -> builder.subject(userId.toString())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.value").value(75.5));
        }

        @Test
        @WithMockUser
        void getEntries_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                when(journalEntryService.getEntries(userId)).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/journal")
                                .with(jwt().jwt(builder -> builder.subject(userId.toString()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        @Test
        @WithMockUser
        void getBatches_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                Page<JournalBatchResponse> pageContent = new PageImpl<>(Collections.emptyList());
                when(journalEntryService.getBatches(eq(userId), eq(0), eq(10))).thenReturn(pageContent);

                mockMvc.perform(get("/api/journal/batches")
                                .with(jwt().jwt(builder -> builder.subject(userId.toString()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @WithMockUser
        void getEntry_ReturnsOk() throws Exception {
                Long entryId = 1L;
                JournalEntryResponse response = JournalEntryResponse.builder().id(entryId).build();
                when(journalEntryService.getEntry(entryId)).thenReturn(Optional.of(response));

                mockMvc.perform(get("/api/journal/{id}", entryId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(entryId));
        }

        @Test
        @WithMockUser
        void getEntry_NotFound() throws Exception {
                Long entryId = 99L;
                when(journalEntryService.getEntry(entryId)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/journal/{id}", entryId))
                                .andExpect(status().isNotFound());
        }
}
