package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.CreateCommentRequestDto;
import com.example.collab_desk.dto.responseDto.CommentResponseDto;
import com.example.collab_desk.entity.Comment;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.CommentRepository;
import com.example.collab_desk.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Task taskWithId(Long id) {
        Task task = new Task();
        task.setId(id);
        return task;
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }


    // Create Comment Tests

    @Test
    public void createComment_shouldSaveCommentWithAllFieldsFromRequest() {
        // arrange
        Long taskId = 1L;
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test content");
        Task task = taskWithId(taskId);
        User currentUser = userWithId(1L);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(userService.getCurrentUser()).thenReturn(currentUser);

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(i -> i.getArgument(0));

        // act
        commentService.createComment(taskId, request);

        // assert
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment saved = captor.getValue();

        assertEquals("Test content", saved.getContent());
        assertSame(currentUser, saved.getAuthor());
        assertSame(task, saved.getTask());
    }

    @Test
    public void createComment_shouldThrowExceptionWhenTaskNotFound() {
        // arrange
        Long taskId = 1L;
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test content");

        when(taskService.getTaskById(taskId))
                .thenThrow(ResourceNotFoundException.class);

        // act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(taskId, request)
        );

        verify(userService, never())
                .getCurrentUser();

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    @Test
    public void createComment_shouldThrowExceptionWhenUserNotAuthenticated() {
        // arrange
        Long taskId = 1L;
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test content");
        Task task = taskWithId(taskId);

        when(taskService.getTaskById(taskId)).thenReturn(task);
        when(userService.getCurrentUser())
                .thenThrow(new UnauthorizedException("User not authenticated"));

        // act & assert
        assertThrows(
                UnauthorizedException.class,
                () -> commentService.createComment(taskId, request)
        );

        verify(commentRepository, never())
                .save(any(Comment.class));
    }

    // Update Comment Tests

    @Test
    public void updateComment_shouldUpdateAllFieldsOnExistingComment() {
        // arrange
        Long commentId = 1L;
        User currentUser = userWithId(1L);
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("New Content");

        Comment existingComment = new Comment("Old Content", currentUser, taskWithId(1L));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(i -> i.getArgument(0));

        // act
        commentService.updateComment(commentId, request);

        // assert
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();

        assertSame(existingComment, saved);
        assertSame(existingComment.getAuthor(), saved.getAuthor());
        assertSame(existingComment.getTask(), saved.getTask());
        assertEquals("New Content", saved.getContent());
    }

    @Test
    public void updateComment_shouldThrowExceptionWhenUserIsNotAuthor() {
        // arrange
        Long commentId = 1L;
        User currentUser = userWithId(1L);
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("New Content");

        Comment existingComment = new Comment("Old Content", userWithId(2L), taskWithId(1L));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // act
        assertThrows(UnauthorizedException.class,
                () -> commentService.updateComment(commentId, request));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void updateComment_shouldThrowExceptionWhenCommentNotFound() {
        // arrange
        Long commentId = 1L;
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("New Content");


        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(commentId, request));

        verify(userService, never()).getCurrentUser();
        verify(commentRepository, never()).save(any(Comment.class));
    }

    // Get Comment Tests

    @Test
    public void getComment_shouldReturnCommentSuccessfully() {
        // arrange
        Long commentId = 1L;
        Comment existingComment = new Comment("Old Content", userWithId(1L), taskWithId(1L));

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // act
        CommentResponseDto response = commentService.getComment(commentId);

        // assert
        assertNotNull(response);
        assertEquals(existingComment.getContent(), response.getContent());
        assertEquals(existingComment.getAuthor().getId(), response.getAuthorId());
        assertEquals(existingComment.getTask().getId(), response.getTaskId());
    }

    @Test
    public void getComment_shouldThrowExceptionWhenCommentNotFound() {
        // arrange
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getComment(commentId));
    }
}