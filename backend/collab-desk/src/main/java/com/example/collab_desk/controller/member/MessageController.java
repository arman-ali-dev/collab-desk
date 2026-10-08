package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.responseDto.MessageResponseDto;
import com.example.collab_desk.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;


    @GetMapping("/chat/rooms/{id}")
    public ResponseEntity<List<MessageResponseDto>> getAllByChatRoomHandler(@PathVariable Long id) {
        List<MessageResponseDto> response = chatService.getAllByChatRoom(id);
        return ResponseEntity.ok(response);
    }
}
