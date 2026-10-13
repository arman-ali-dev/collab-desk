package com.example.collab_desk.controller.member;


import com.example.collab_desk.dto.responseDto.CommentResponseDto;
import com.example.collab_desk.dto.responseDto.NotificationResponseDto;
import com.example.collab_desk.enums.NotificationType;
import com.example.collab_desk.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    private NotificationResponseDto buildResponse(Long id) {
        return new NotificationResponseDto(id, NotificationType.MESSAGE
                , "test notification", "test message",
                false, LocalDateTime.now().minusHours(4));
    }

    // get notifications tests

    @Test
    @WithMockUser
    public void getNotifications_shouldReturn200WithNotifications() throws Exception {
        // arrange
        NotificationResponseDto notification1 = buildResponse(1L);
        notification1.setTitle("Notification 1");
        NotificationResponseDto notification2 = buildResponse(2L);
        notification2.setTitle("Notification 2");
        List<NotificationResponseDto> notifications = List.of(notification1, notification2);

        when(notificationService.getNotifications()).thenReturn(notifications);

        // act & assert
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Notification 1"))
                .andExpect(jsonPath("$[1].title").value("Notification 2"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser
    public void getNotifications_shouldReturn200WithNotificationEmptyList() throws Exception {
        // arrange
        List<NotificationResponseDto> notifications = List.of();

        when(notificationService.getNotifications()).thenReturn(notifications);

        // act & assert
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getNotifications_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());

        verify(notificationService, never()).getNotifications();
    }

    // mark all read tests

    @Test
    @WithMockUser
    public void markAllRead_shouldReturn200AndMarkAllRead() throws Exception {
        // act & assert
        mockMvc.perform(patch("/api/notifications/read-all")
                        .with(csrf()))
                .andExpect(status().isNoContent());
        verify(notificationService).markAllRead();
    }

    @Test
    public void markAllRead_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(patch("/api/notifications/read-all")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
        verify(notificationService, never()).markAllRead();
    }
}
