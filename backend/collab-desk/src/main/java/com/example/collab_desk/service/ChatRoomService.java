package com.example.collab_desk.service;

import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.Project;

import java.util.List;

public interface ChatRoomService {
    ChatRoom createRoom(Project project);

    List<ChatRoom> getAllChatRoom();
}
