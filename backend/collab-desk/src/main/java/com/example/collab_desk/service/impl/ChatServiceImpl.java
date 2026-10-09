package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.SendMessageRequest;
import com.example.collab_desk.dto.responseDto.MessageResponseDto;
import com.example.collab_desk.dto.responseDto.SenderResponseDto;
import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.Message;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.NotificationType;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.repository.MessageRepository;
import com.example.collab_desk.service.ChatService;
import com.example.collab_desk.service.NotificationService;
import com.example.collab_desk.service.RoomAccessService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RoomAccessService roomAccessService;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;

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

        String preview = switch (message.getType()) {
            case TEXT -> message.getContent().length() > 80 ?
                    message.getContent().substring(0, 80) + "..." : message.getContent();
            case IMAGE -> "[Image]";
            case VIDEO -> "[Video]";
            case FILE -> "[File] " + (message.getFilename() != null ? message.getFilename() : "");
        };

        String title = "New message in " + room.getProject().getTitle();
        String body = sender.getFullName() + ": " + preview;

        for (User member : room.getProject().getMembers()) {
            if (member.getId().equals(sender.getId())) continue;
            notificationService.notify(member, NotificationType.NEW_MESSAGE, title, body);
        }

        return mapToMessageResponseDto(messageRepository.save(message));
    }

    @Override
    public List<MessageResponseDto> getAllByChatRoom(Long chatRoomId) {
        return messageRepository.findByChatRoom_Id(chatRoomId)
                .stream().map(this::mapToMessageResponseDto).toList();
    }


    private MessageResponseDto mapToMessageResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getChatRoom().getId(),
                message.getContent(),
                message.getType(),
                new SenderResponseDto(message.getSender().getId(),
                        message.getSender().getFullName(),
                        message.getSender().getProfileImage()),
                message.getCaption(),
                message.getFilename(),
                message.getSentAt()
        );
    }

}
