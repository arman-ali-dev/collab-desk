package com.example.collab_desk.service;

import com.example.collab_desk.dto.responseDto.NotificationResponseDto;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    void notify(User recipient, NotificationType type, String title, String message);

    List<NotificationResponseDto> getNotifications();

    void markAllRead();
}
