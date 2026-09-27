package com.example.collab_desk.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ValidationExceptionResponseDto {
    private LocalDateTime timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
    Map<String, String> fieldsErrors;
}
