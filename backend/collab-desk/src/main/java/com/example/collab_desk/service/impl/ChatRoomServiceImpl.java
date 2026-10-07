package com.example.collab_desk.service.impl;

import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoom createRoom(Project project) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setProject(project);
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> getAllChatRoom() {
        return chatRoomRepository.findAllByOrderByCreatedAtDesc();
    }
}
