package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_IdAndReadAtIsNullOrderByIdDesc(Long userId);

    @Modifying
    @Query("""
        update Notification n set n.readAt = :now
        where n.user.id = :userId and n.readAt is null
    """)
    void markAllRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
