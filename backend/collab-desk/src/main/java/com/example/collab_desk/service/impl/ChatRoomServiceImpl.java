package com.example.collab_desk.service.impl;

import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.repository.MessageRepository;
import com.example.collab_desk.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

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

    @Override
    public void deleteChatRoom(Long projectId) {
        ChatRoom chatRoom = chatRoomRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));
        messageRepository.deleteByChatRoom_Id(chatRoom.getId());
        chatRoomRepository.delete(chatRoom);
    }
}
