package com.devlife4me.projectvital.controller;

import com.devlife4me.projectvital.model.entity.UserProfile;
import com.devlife4me.projectvital.repo.UserProfileRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserProfileRepo profileRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser
        void getMyProfile_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                UserProfile profile = UserProfile.builder()
                                .userId(userId)
                                .displayName("John Doe")
                                .build();

                when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

                mockMvc.perform(get("/api/profile/me")
                                .with(jwt().jwt(builder -> builder.subject(userId.toString()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.displayName").value("John Doe"));
        }

        @Test
        @WithMockUser
        void getMyProfile_ReturnsNotFound() throws Exception {
                UUID userId = UUID.randomUUID();
                when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/profile/me")
                                .with(jwt().jwt(builder -> builder.subject(userId.toString()))))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        void createOrUpdateProfile_UpdateExisting_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                UserProfile profileData = UserProfile.builder()
                                .displayName("Jane Smith")
                                .firstName("Jane")
                                .lastName("Smith")
                                .build();

                UserProfile existingProfile = UserProfile.builder()
                                .userId(userId)
                                .email("jane@example.com")
                                .displayName("Jane Doe")
                                .build();

                when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(existingProfile));
                when(profileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArguments()[0]);

                mockMvc.perform(post("/api/profile/me")
                                .with(csrf())
                                .with(jwt().jwt(builder -> {
                                        builder.subject(userId.toString());
                                        builder.claim("email", "jane@example.com");
                                }))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(profileData)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.displayName").value("Jane Smith"))
                                .andExpect(jsonPath("$.firstName").value("Jane"))
                                .andExpect(jsonPath("$.lastName").value("Smith"))
                                .andExpect(jsonPath("$.email").value("jane@example.com"));
        }

        @Test
        @WithMockUser
        void createOrUpdateProfile_InitialCreation_ReturnsOk() throws Exception {
                UUID userId = UUID.randomUUID();
                UserProfile profileData = UserProfile.builder()
                                .displayName("New User")
                                .build();

                when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());
                when(profileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArguments()[0]);

                mockMvc.perform(post("/api/profile/me")
                                .with(csrf())
                                .with(jwt().jwt(builder -> {
                                        builder.subject(userId.toString());
                                        builder.claim("email", "newuser@example.com");
                                }))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(profileData)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.displayName").value("New User"))
                                .andExpect(jsonPath("$.userId").value(userId.toString()))
                                .andExpect(jsonPath("$.email").value("newuser@example.com"));
        }
}
