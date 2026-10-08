package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.SendMessageRequest;
import com.example.collab_desk.dto.responseDto.MessageResponseDto;
import com.example.collab_desk.entity.Message;

import java.util.List;

public interface ChatService {

    public MessageResponseDto saveMessage(SendMessageRequest req, String email);

    public List<MessageResponseDto> getAllByChatRoom(Long chatRoomId);
}
