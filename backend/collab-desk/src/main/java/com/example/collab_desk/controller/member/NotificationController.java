package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.responseDto.NotificationResponseDto;
import com.example.collab_desk.enums.NotificationType;
import com.example.collab_desk.service.NotificationService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsHandler() {
        List<NotificationResponseDto> response = notificationService.getNotifications();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        notificationService.markAllRead();
        return ResponseEntity.noContent().build();
    }
}
