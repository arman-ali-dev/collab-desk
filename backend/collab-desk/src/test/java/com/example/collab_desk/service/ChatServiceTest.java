package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.SendMessageRequest;
import com.example.collab_desk.dto.responseDto.MessageResponseDto;
import com.example.collab_desk.entity.*;
import com.example.collab_desk.enums.MessageType;
import com.example.collab_desk.enums.NotificationType;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.repository.MessageRepository;
import com.example.collab_desk.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private RoomAccessService roomAccessService;

    @Mock
    private UserService userService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private User sender() {
        User sender = userWithId(1L);
        sender.setFullName("Sender");
        return sender;
    }

    private ChatRoom createRoom(User... members) {
        Project project = projectWithId(4L);
        project.setTitle("Test project");
        project.setMembers(Set.of(members));
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(5L);
        chatRoom.setProject(project);
        return chatRoom;
    }

    private SendMessageRequest buildRequest(MessageType type, String content, String filename) {
        SendMessageRequest request = new SendMessageRequest();
        request.setRoomId(5L);
        request.setType(type);
        request.setContent(content);
        request.setFilename(filename);

        return request;
    }

    public void successfulSave(String email, User sender, ChatRoom chatRoom) {
        when(roomAccessService.canAccess(email, 5L)).thenReturn(true);
        when(userService.getUserByEmail(email)).thenReturn(sender);
        when(chatRoomRepository.findById(5L)).thenReturn(Optional.of(chatRoom));
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(i -> i.getArgument(0));
    }


    // save message tests

    @Test
    public void saveMessage_shouldSaveMessageSuccessfully() {
        // arrange
        String email = "sender@gmail.com";

        User sender = sender();

        User member1 = userWithId(2L);
        User member2 = userWithId(3L);

        ChatRoom chatRoom = createRoom(sender, member1, member2);
        SendMessageRequest request = buildRequest(MessageType.TEXT, "Hello", null);
        successfulSave(email, sender, chatRoom);

        // act
        MessageResponseDto response = chatService.saveMessage(request, email);

        // assert
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        Message saved = captor.getValue();


        assertNotNull(response);
        assertSame(sender, saved.getSender());
        assertSame(chatRoom, saved.getChatRoom());
        assertEquals(MessageType.TEXT, saved.getType());
        assertEquals("Hello", saved.getContent());

        String title = "New message in Test project";
        String message = "Sender: Hello";

        verify(notificationService).notify(member1, NotificationType.MESSAGE, title, message);
        verify(notificationService).notify(member2, NotificationType.MESSAGE, title, message);
        verify(notificationService, never()).notify(eq(sender), any(), any(), any());
    }

    @Test
    public void saveMessage_shouldThrowExceptionWhenRoomAccessIsNotAllowed() {
        // arrange
        String email = "sender@gmail.com";

        SendMessageRequest request = buildRequest(MessageType.TEXT, "Hello", null);
        when(roomAccessService.canAccess(email, 5L)).thenReturn(false);

        // act & assert
        assertThrows(UnauthorizedException.class, () -> chatService.saveMessage(request, email));

        verify(userService, never()).getUserByEmail(email);
        verify(chatRoomRepository, never()).findById(anyLong());
        verify(chatRoomRepository, never()).save(any());
        verify(notificationService, never()).notify(any(), any(), any(), any());
    }

    @Test
    public void saveMessage_shouldThrowExceptionWhenSenderNotFound() {
        // arrange
        String email = "sender@gmail.com";

        SendMessageRequest request = buildRequest(MessageType.TEXT, "Hello", null);

        when(roomAccessService.canAccess(email, 5L)).thenReturn(true);
        when(userService.getUserByEmail(email)).thenThrow(new ResourceNotFoundException("User not found"));

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> chatService.saveMessage(request, email));

        verify(chatRoomRepository, never()).findById(anyLong());
        verify(chatRoomRepository, never()).save(any());
        verify(notificationService, never()).notify(any(), any(), any(), any());
    }

    @Test
    public void saveMessage_shouldThrowExceptionWhenChatRoomNotFound() {
        // arrange
        String email = "sender@gmail.com";

        User sender = sender();

        SendMessageRequest request = buildRequest(MessageType.TEXT, "Hello", null);

        when(roomAccessService.canAccess(email, 5L)).thenReturn(true);
        when(userService.getUserByEmail(email)).thenReturn(sender);
        when(chatRoomRepository.findById(5L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> chatService.saveMessage(request, email));

        verify(chatRoomRepository, never()).save(any());
        verify(notificationService, never()).notify(any(), any(), any(), any());
    }

    @Test
    public void saveMessage_shouldBuildTruncateLongTextPreview() {
        // arrange
        String email = "sender@gmail.com";

        User sender = sender();
        User member1 = userWithId(2L);
        User member2 = userWithId(3L);

        ChatRoom chatRoom = createRoom(sender, member1, member2);
        SendMessageRequest request = buildRequest(MessageType.TEXT, "a".repeat(100), null);
        successfulSave(email, sender, chatRoom);

        // act
        MessageResponseDto response = chatService.saveMessage(request, email);

        // assert
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        Message saved = captor.getValue();


        assertNotNull(response);
        assertSame(sender, saved.getSender());
        assertSame(chatRoom, saved.getChatRoom());
        assertEquals(MessageType.TEXT, saved.getType());

        String title = "New message in Test project";
        String message = "Sender: " + "a".repeat(80) + "...";

        verify(notificationService).notify(member1, NotificationType.MESSAGE, title, message);
        verify(notificationService).notify(member2, NotificationType.MESSAGE, title, message);
        verify(notificationService, never()).notify(eq(sender), any(), any(), any());
    }

    @Test
    public void saveMessage_shouldBuildPreviewForImageVideoAndFile() {
        // arrange
        String email = "sender@gmail.com";

        User sender = sender();
        User member1 = userWithId(2L);
        User member2 = userWithId(3L);

        ChatRoom chatRoom = createRoom(sender, member1, member2);
        SendMessageRequest request = buildRequest(MessageType.FILE, "a".repeat(100), "filename");
        successfulSave(email, sender, chatRoom);

        // act
        MessageResponseDto response = chatService.saveMessage(request, email);

        // assert
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        Message saved = captor.getValue();

        assertNotNull(response);
        assertSame(sender, saved.getSender());
        assertSame(chatRoom, saved.getChatRoom());
        assertEquals(MessageType.FILE, saved.getType());

        String title = "New message in Test project";
        String message = "Sender: [File] filename";

        verify(notificationService).notify(member1, NotificationType.MESSAGE, title, message);
        verify(notificationService).notify(member2, NotificationType.MESSAGE, title, message);
        verify(notificationService, never()).notify(eq(sender), any(), any(), any());
    }


    @Test
    public void saveMessage_shouldNotNotifyWhenSenderIsOnlyMember() {
        // arrange
        String email = "sender@gmail.com";

        User sender = sender();

        ChatRoom chatRoom = createRoom(sender);
        SendMessageRequest request = buildRequest(MessageType.FILE, "a".repeat(100), "filename");
        successfulSave(email, sender, chatRoom);

        // act
        MessageResponseDto response = chatService.saveMessage(request, email);

        // assert
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        Message saved = captor.getValue();


        assertNotNull(response);
        assertSame(sender, saved.getSender());
        assertSame(chatRoom, saved.getChatRoom());
        assertEquals(MessageType.FILE, saved.getType());

        verify(notificationService, never()).notify(eq(sender), any(), any(), any());
    }

    private Message messageWithId(Long id) {
        Message message = new Message();
        message.setId(id);

        User sender = userWithId(1L);
        message.setSender(sender);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(5L);
        message.setChatRoom(chatRoom);
        return message;
    }

    @Test
    public void getAllByChatRoom_shouldReturnMessagesSuccessfully() {
        // arrange
        Long chatRoomId = 1L;

        Message message1 = messageWithId(1L);
        Message message2 = messageWithId(2L);
        List<Message> messages = List.of(message1, message2);

        when(messageRepository.findByChatRoom_Id(chatRoomId)).thenReturn(messages);

        // act
        List<MessageResponseDto> response = chatService.getAllByChatRoom(chatRoomId);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(5L, response.getFirst().getRoomId());
        assertEquals(5L, response.get(1).getRoomId());
    }


    @Test
    public void getAllByChatRoom_shouldReturnEmptyMessageList() {
        // arrange
        Long chatRoomId = 1L;
        List<Message> messages = List.of();

        when(messageRepository.findByChatRoom_Id(chatRoomId)).thenReturn(messages);

        // act
        List<MessageResponseDto> response = chatService.getAllByChatRoom(chatRoomId);

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

}
