package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.requestDto.SendMessageRequest;
import com.example.collab_desk.dto.responseDto.MessageResponseDto;
import com.example.collab_desk.dto.responseDto.SenderResponseDto;
import com.example.collab_desk.enums.MessageType;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.service.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatController chatController;

    private final Principal principal = () -> "sender@gmail.com";

    @Test
    void send_shouldSaveMessageAndBroadcastToRoomTopic() {
        // arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");
        request.setType(MessageType.TEXT);
        request.setRoomId(5L);

        MessageResponseDto response = new MessageResponseDto(1L, 5L,
                "Hello", MessageType.TEXT,
                new SenderResponseDto(1L, "Sender", "https://api.com/profile.png"),
                null, null, null);

        when(chatService.saveMessage(request, "sender@gmail.com")).thenReturn(response);

        // act
        chatController.send(request, principal);

        // verify
        verify(chatService).saveMessage(request, "sender@gmail.com");
        verify(messagingTemplate).convertAndSend("/topic/room.5", response);
    }

    @Test
    void send_shouldNotBroadcastWhenSaveFails() {
        // arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setRoomId(5L);

        when(chatService.saveMessage(request, "sender@gmail.com"))
                .thenThrow(new UnauthorizedException("You are not a member of this room"));

        // act
        assertThrows(UnauthorizedException.class,
                () -> chatService.saveMessage(request, "sender@gmail.com"));

        // verify
        verify(messagingTemplate, never())
                .convertAndSend(eq("/topic/room.5"), any(MessageResponseDto.class));
    }
}
