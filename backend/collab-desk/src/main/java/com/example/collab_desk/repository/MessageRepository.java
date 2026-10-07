package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
