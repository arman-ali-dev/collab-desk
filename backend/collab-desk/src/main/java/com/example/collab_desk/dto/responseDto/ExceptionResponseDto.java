package com.example.collab_desk.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseDto {

    private LocalDateTime timestamp;
    private int statusCode;
    private String error;
    private String message;
    private String path;
}
