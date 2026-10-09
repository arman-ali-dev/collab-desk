package com.example.collab_desk.dto.responseDto;

import com.example.collab_desk.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
