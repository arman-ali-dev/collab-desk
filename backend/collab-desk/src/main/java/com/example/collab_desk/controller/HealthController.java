package com.example.collab_desk.controller;

import com.example.collab_desk.dto.responseDto.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final String serviceName;
    private final String serviceDescription;

    public HealthController(@Value("${app.info.name}") String serviceName,
                            @Value("${app.info.description}") String serviceDescription) {
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> healthHandler() {
        HealthResponse response = new HealthResponse(serviceName, serviceDescription, "UP");
        return ResponseEntity.ok(response);
    }
}
