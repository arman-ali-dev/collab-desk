package com.example.collab_desk.controller.member;

import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoom>> getChatRoomsHandler() {
        List<ChatRoom> response = chatRoomService.getAllChatRoom();
        return ResponseEntity.ok(response);
    }
}
