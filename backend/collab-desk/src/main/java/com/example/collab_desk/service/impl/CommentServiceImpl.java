package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.CreateCommentRequestDto;
import com.example.collab_desk.dto.responseDto.CommentResponseDto;
import com.example.collab_desk.entity.Comment;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.CommentRepository;
import com.example.collab_desk.service.CommentService;
import com.example.collab_desk.service.TaskService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TaskService taskService;


    @Override
    @Transactional
    public CommentResponseDto createComment(Long taskId, CreateCommentRequestDto request) {
        Task task = taskService.getTaskById(taskId);
        User author = userService.getCurrentUser();

        return mapToCommentResponseDto(
                commentRepository.save(new Comment(request.getContent(), author, task)));
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long id, CreateCommentRequestDto request) {
        Comment existingComment = getCommentById(id);
        User author = userService.getCurrentUser();

        if (!Objects.equals(author.getId(), existingComment.getAuthor().getId())) {
            throw new UnauthorizedException("You are not authorized to modify this comment");
        }

        existingComment.setContent(request.getContent());
        return mapToCommentResponseDto(commentRepository.save(existingComment));
    }

    @Override
    public CommentResponseDto getComment(Long id) {
        return mapToCommentResponseDto(getCommentById(id));
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment existingComment = getCommentById(id);
        User author = userService.getCurrentUser();

        if (!Objects.equals(author.getId(), existingComment.getAuthor().getId())) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(existingComment);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    @Override
    public List<CommentResponseDto> getCommentsByTask(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return commentRepository
                .findAllByTask(task)
                .stream()
                .map(this::mapToCommentResponseDto)
                .toList();
    }

    private CommentResponseDto mapToCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getTask().getId()
        );
    }
}
