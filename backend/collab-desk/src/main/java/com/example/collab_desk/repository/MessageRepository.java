package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom_Id(Long chatRoomId);
}
