package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.SendMessageRequest;
import com.example.collab_desk.dto.responseDto.MessageResponseDto;
import com.example.collab_desk.dto.responseDto.SenderResponseDto;
import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.Message;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.repository.MessageRepository;
import com.example.collab_desk.service.ChatService;
import com.example.collab_desk.service.RoomAccessService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RoomAccessService roomAccessService;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public MessageResponseDto saveMessage(SendMessageRequest req, String email) {
        if (!roomAccessService.canAccess(email, req.getRoomId())) {
            throw new UnauthorizedException("You are not a member of this room");
        }

        User sender = userService.getUserByEmail(email);
        ChatRoom room = chatRoomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        Message message = new Message();

        message.setChatRoom(room);
        message.setSender(sender);
        message.setType(req.getType());
        message.setContent(req.getContent());
        message.setCaption(req.getCaption());
        message.setFilename(req.getFilename());

        messageRepository.save(message);

        return new MessageResponseDto(
                message.getId(),
                message.getChatRoom().getId(),
                message.getContent(),
                message.getType(),
                new SenderResponseDto(sender.getId(), sender.getFullName(), sender.getProfileImage()),
                message.getCaption(),
                message.getFilename(),
                message.getSentAt()
        );
    }


}
