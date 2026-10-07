package com.example.collab_desk.service.impl;

import com.example.collab_desk.entity.ChatRoom;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.repository.ChatRoomRepository;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.RoomAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomAccessServiceImpl implements RoomAccessService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional
    public boolean canAccess(String email, Long roomId) {
        if (email == null || roomId == null) return false;

        User user = userRepository.findByEmail(email).orElse(null);
        ChatRoom room = chatRoomRepository.findById(roomId).orElse(null);

        if (user == null || room == null) return false;

        if (user.getRole().equals(UserRole.ADMIN)) return true;

        return room.getProject().getMembers().stream()
                .anyMatch(m -> m.getId().equals(user.getId()));
    }
}
