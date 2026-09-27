package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Comment;
import com.example.collab_desk.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTask(Task task);
}
