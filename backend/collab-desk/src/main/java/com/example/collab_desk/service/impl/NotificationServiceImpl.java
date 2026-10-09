package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.responseDto.NotificationResponseDto;
import com.example.collab_desk.entity.Notification;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.NotificationType;
import com.example.collab_desk.repository.NotificationRepository;
import com.example.collab_desk.service.NotificationService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void notify(User recipient, NotificationType type, String title, String message) {

        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);

        NotificationResponseDto response =
                mapToNotificationResponseDto(notificationRepository.save(notification));

        messagingTemplate.convertAndSendToUser(recipient.getEmail(),
                "/queue/notifications", response);
    }

    @Override
    public List<NotificationResponseDto> getNotifications() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.findByUser_IdAndReadAtIsNullOrderByIdDesc(currentUser.getId())
                .stream().map(this::mapToNotificationResponseDto).toList();
    }

    @Override
    @Transactional
    public void markAllRead() {
        User currentUser = userService.getCurrentUser();
        notificationRepository.markAllRead(currentUser.getId(), LocalDateTime.now());
    }

    private NotificationResponseDto mapToNotificationResponseDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                true,
                notification.getCreatedAt()
        );
    }
}
