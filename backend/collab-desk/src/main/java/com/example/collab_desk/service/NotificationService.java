package com.example.collab_desk.service;

import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.NotificationType;

public interface NotificationService {
    void notify(User recipient, NotificationType type, String title, String message);

    void markAllRead();
}
