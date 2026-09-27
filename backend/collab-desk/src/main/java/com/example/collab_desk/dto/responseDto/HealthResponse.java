package com.example.collab_desk.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HealthResponse {
    private String serviceName;
    private String serviceDescription;
    private String appStatus;
}
