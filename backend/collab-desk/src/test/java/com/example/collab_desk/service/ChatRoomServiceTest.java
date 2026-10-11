package com.example.collab_desk.service;


import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.repository.MessageRepository;
import com.example.collab_desk.service.impl.ChatRoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    private ChatRoom chatRoomWithId(Long id) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(id);
        return chatRoom;
    }
    // Create chat room tests

    @Test
    public void createChatRoom_shouldSaveChatRoomWithAllFieldsFromRequest() {
        // arrange
        Project project = projectWithId(1L);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setProject(project);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(i -> i.getArgument(0));

        // act
        ChatRoom response = chatRoomService.createRoom(project);

        // assert
        ArgumentCaptor<ChatRoom> captor = ArgumentCaptor.forClass(ChatRoom.class);
        verify(chatRoomRepository).save(captor.capture());
        ChatRoom saved = captor.getValue();

        assertNotNull(response);
        assertSame(response, saved);
        assertEquals(project.getId(), saved.getProject().getId());
    }

    @Test
    public void createChatRoom_shouldThrowExceptionWhenSaveFails() {
        // arrange
        Project project = projectWithId(1L);
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setProject(project);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenThrow(new RuntimeException("Database error"));

        // act & assert
        assertThrows(RuntimeException.class, () -> chatRoomService.createRoom(project));
    }

    // get all chat rooms

    @Test
    public void getAllChatRooms_shouldReturnAllChatRoomsSuccessfully() {
        // arrange
        ChatRoom chatRoom1 = chatRoomWithId(1L);
        ChatRoom chatRoom2 = chatRoomWithId(2L);

        List<ChatRoom> chatRooms = new ArrayList<>(List.of(chatRoom1, chatRoom2));

        when(chatRoomRepository.findAllByOrderByCreatedAtDesc()).thenReturn(chatRooms);

        // act
        List<ChatRoom> response = chatRoomService.getAllChatRoom();

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1, chatRoom1.getId());
        assertEquals(2, chatRoom2.getId());
    }

    @Test
    public void getAllChatRooms_shouldReturnChatRoomEmptyList() {
        // arrange
        List<ChatRoom> chatRooms = new ArrayList<>(List.of());
        when(chatRoomRepository.findAllByOrderByCreatedAtDesc()).thenReturn(chatRooms);

        // act
        List<ChatRoom> response = chatRoomService.getAllChatRoom();

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    // delete chat room tests

    @Test
    public void deleteChatRoom_shouldDeleteChatRoomSuccessfully() {
        // arrange
        Long projectId = 1L;
        ChatRoom room = chatRoomWithId(1L);
        when(chatRoomRepository.findByProjectId(projectId)).thenReturn(Optional.of(room));

        // act
        chatRoomService.deleteChatRoom(projectId);

        // assert
        verify(messageRepository).deleteByChatRoom_Id(room.getId());
        verify(chatRoomRepository).delete(room);
    }

    @Test
    public void deleteChatRoom_shouldThrowExceptionWhenChatRoomNotFound() {
        // arrange
        Long projectId = 1L;

        when(chatRoomRepository.findByProjectId(projectId)).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class, () -> chatRoomService.deleteChatRoom(projectId));

        // assert
        verify(messageRepository, never()).deleteByChatRoom_Id(anyLong());
        verify(chatRoomRepository, never()).delete(any(ChatRoom.class));
    }
}
