package com.example.collab_desk.controller.member;

import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomController.class)
public class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatRoomService chatRoomService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatRoom chatRoomWithId(Long id) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(id);
        return chatRoom;
    }

    @Test
    @WithMockUser
    public void getAllChatRooms_shouldReturn200WithAllChatRooms() throws Exception {
        // arrange
        ChatRoom chatRoom1 = chatRoomWithId(1L);
        ChatRoom chatRoom2 = chatRoomWithId(1L);

        List<ChatRoom> chatRooms = List.of(chatRoom1, chatRoom2);

        when(chatRoomService.getAllChatRoom()).thenReturn(chatRooms);

        // act
        mockMvc.perform(get("/api/chat/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    public void getAllChatRooms_shouldReturn200WithChatRoomEmptyList() throws Exception {
        // arrange
        List<ChatRoom> chatRooms = List.of();

        when(chatRoomService.getAllChatRoom()).thenReturn(chatRooms);

        // act & assert
        mockMvc.perform(get("/api/chat/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllChatRooms_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/chat/rooms"))
                .andExpect(status().isUnauthorized());
    }
}
