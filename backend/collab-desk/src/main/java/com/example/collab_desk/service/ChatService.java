package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.SendMessageRequest;
import com.example.collab_desk.dto.responseDto.MessageResponseDto;

public interface ChatService {

    public MessageResponseDto saveMessage(SendMessageRequest req, String email);
}
