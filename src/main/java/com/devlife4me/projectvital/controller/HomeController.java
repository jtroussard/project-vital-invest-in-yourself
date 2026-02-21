package com.devlife4me.projectvital.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "status", "Project Vital is UP",
                "environment",
                System.getenv("SPRING_PROFILES_ACTIVE") != null ? System.getenv("SPRING_PROFILES_ACTIVE")
                        : System.getProperty("spring.profiles.active", "prod"),
                "message", "Welcome to the Project Vital API",
                "endpoints", Map.of(
                        "health", "/actuator/health",
                        "verify_api", "/api/verify/read/{id}"));
    }
}
