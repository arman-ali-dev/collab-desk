package com.example.collab_desk.service;

import com.example.collab_desk.dto.responseDto.NotificationResponseDto;
import com.example.collab_desk.entity.Notification;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.NotificationRepository;
import com.example.collab_desk.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    // get notifications tests

    @Test
    public void getAllNotifications_shouldReturnNotificationsOfUser() {
        // arrange
        User currentUser = new User();

        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setUser(currentUser);
        notification1.setTitle("notification 1");
        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setUser(currentUser);
        notification2.setTitle("notification 2");

        List<Notification> notifications = List.of(notification1, notification2);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findByUser_IdAndReadAtIsNullOrderByIdDesc(currentUser.getId()))
                .thenReturn(notifications);

        // act
        List<NotificationResponseDto> response = notificationService.getNotifications();

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1, response.getFirst().getId());
        assertEquals(2, response.get(1).getId());
        assertEquals("notification 1", response.get(0).getTitle());
        assertEquals("notification 2", response.get(1).getTitle());
    }

    @Test
    public void getAllNotifications_shouldReturnEmptyNotificationsListOfUser() {
        // arrange
        User currentUser = new User();

        List<Notification> notifications = List.of();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(notificationRepository.findByUser_IdAndReadAtIsNullOrderByIdDesc(currentUser.getId()))
                .thenReturn(notifications);

        // act
        List<NotificationResponseDto> response = notificationService.getNotifications();

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void getAllNotifications_shouldThrowExceptionWhenUserIsNotAuthenticated() {
        // arrange
        when(userService.getCurrentUser()).thenThrow(new UnauthorizedException("User is not authenticated"));

        // act
        assertThrows(UnauthorizedException.class, () -> notificationService.getNotifications());
        verify(notificationRepository, never()).findByUser_IdAndReadAtIsNullOrderByIdDesc(anyLong());
    }

    // mark all read tests

    @Test
    public void markAllRead_shouldMarkAllReadSuccessfully() {
        // arrange
        User currentUser = new User();
        currentUser.setId(1L);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // act
        notificationService.markAllRead();

        // assert
        verify(notificationRepository).markAllRead(anyLong(), any());
    }

    @Test
    public void markAllRead_shouldThrowExceptionWhenUserIsNotAuthenticated() {
        // arrange
        User currentUser = new User();
        currentUser.setId(1L);
        when(userService.getCurrentUser()).thenThrow(new UnauthorizedException("User is not authenticated"));

        // act
        assertThrows(UnauthorizedException.class, () -> notificationService.markAllRead());

        // assert
        verify(notificationRepository, never()).markAllRead(anyLong(), any());
    }

}
