package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.requestDto.CreateCommentRequestDto;
import com.example.collab_desk.dto.responseDto.CommentResponseDto;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    // Create Comment Tests

    @Test
    @WithMockUser
    public void createComment_shouldReturn201WhenRequestIsValid() throws Exception {
        // arrange
        Long taskId = 1L;

        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test Content");

        CommentResponseDto response =
                new CommentResponseDto(1L, "Test Content", null, taskId);

        when(commentService.createComment(eq(taskId), any(CreateCommentRequestDto.class)))
                .thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/comments/{taskId}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    @WithMockUser
    public void createComment_shouldReturn400WhenContentIsBlank() throws Exception {
        // arrange
        Long taskId = 1L;

        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("");

        // act & assert
        mockMvc.perform(post("/api/comments/{taskId}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(anyLong(), any());

    }

    @Test
    @WithMockUser
    public void createComment_shouldReturn404WhenTaskNotFound() throws Exception {
        // arrange
        Long taskId = 99L;

        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test Content");

        when(commentService.createComment(eq(taskId), any(CreateCommentRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // act & assert
        mockMvc.perform(post("/api/comments/{taskId}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }

    // Update Comment Tests

    @Test
    @WithMockUser
    public void updateComment_shouldReturn200WhenRequestIsValid() throws Exception {
        // arrange
        Long commentId = 1L;
        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("New Content");

        CommentResponseDto response =
                new CommentResponseDto(1L, "New Content", null, 1L);

        when(commentService.updateComment(eq(commentId), any(CreateCommentRequestDto.class)))
                .thenReturn(response);

        // act & assert
        mockMvc.perform(patch("/api/comments/{id}", commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New Content"));
    }

    @Test
    @WithMockUser
    public void updateComment_shouldReturn400WhenContentIsBlank() throws Exception {
        // arrange
        Long commentId = 1L;

        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("");

        // act & assert
        mockMvc.perform(patch("/api/comments/{taskId}", commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(anyLong(), any());

    }

    @Test
    @WithMockUser
    public void updateComment_shouldReturn404WhenCommentNotFound() throws Exception {
        // arrange
        Long commentId = 99L;

        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test Content");

        when(commentService.updateComment(eq(commentId), any(CreateCommentRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        // act & assert
        mockMvc.perform(patch("/api/comments/{taskId}", commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser
    public void updateComment_shouldReturn401WhenUserIsNotAuthor() throws Exception {
        // arrange
        Long commentId = 99L;

        CreateCommentRequestDto request = new CreateCommentRequestDto();
        request.setContent("Test Content");

        when(commentService.updateComment(eq(commentId), any(CreateCommentRequestDto.class)))
                .thenThrow(new UnauthorizedException("You are not authorized to modify this comment"));

        // act & assert
        mockMvc.perform(patch("/api/comments/{taskId}", commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

    }

    // Get Comment Controller

    @Test
    @WithMockUser
    public void getComment_shouldReturn200WithCommentWhenFound() throws Exception {
        // arrange
        Long commentId = 1L;
        CommentResponseDto response =
                new CommentResponseDto(1L, "Test Content", null, null);

        when(commentService.getComment(eq(commentId))).thenReturn(response);

        // act & assert
        mockMvc.perform(get("/api/comments/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    @WithMockUser
    public void getComment_shouldReturn404WhenCommentNotFound() throws Exception {
        // arrange
        Long commentId = 1L;

        when(commentService.getComment(eq(commentId)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // act & assert
        mockMvc.perform(get("/api/comments/{id}", commentId))
                .andExpect(status().isNotFound());
    }

    // Get Comments By Task Tests

    @Test
    @WithMockUser
    public void getCommentsByTask_shouldReturn200WhenCommentsFoundByTask() throws Exception {
        // arrange
        Long taskId = 1L;

        CommentResponseDto comment1 =
                new CommentResponseDto(1L, "Comment 1", null, taskId);
        CommentResponseDto comment2 =
                new CommentResponseDto(2L, "Comment 2", null, taskId);

        List<CommentResponseDto> comments = new ArrayList<>(List.of(comment1, comment2));

        when(commentService.getCommentsByTask(taskId)).thenReturn(comments);

        // act & assert
        mockMvc.perform(get("/api/comments/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].content").value("Comment 1"))
                .andExpect(jsonPath("$[1].content").value("Comment 2"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }


    @Test
    @WithMockUser
    public void getCommentsByTask_shouldReturn200WhenCommentsListIsEmpty() throws Exception {
        // arrange
        Long taskId = 1L;
        List<CommentResponseDto> comments = new ArrayList<>(List.of());

        when(commentService.getCommentsByTask(taskId)).thenReturn(comments);

        // act & assert
        mockMvc.perform(get("/api/comments/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    public void getCommentsByTask_shouldReturn404WhenTaskNotFound() throws Exception {
        // arrange
        Long taskId = 1L;

        when(commentService.getCommentsByTask(taskId))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // act & assert
        mockMvc.perform(get("/api/comments/task/{taskId}", taskId))
                .andExpect(status().isNotFound());
    }


    // Delete Comment Tests

    @Test
    @WithMockUser
    public void deleteComment_shouldReturn204WhenDeleteComment() throws Exception {
        // arrange
        Long commentId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/comments/{id}", commentId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(commentId);
    }

    @Test
    @WithMockUser
    public void deleteComment_shouldReturn404WhenCommentNotFound() throws Exception {
        // arrange
        Long commentId = 1L;

        doThrow(new ResourceNotFoundException("Comment not found"))
                .when(commentService).deleteComment(commentId);

        // act & assert
        mockMvc.perform(delete("/api/comments/{id}", commentId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    public void deleteComment_shouldReturn401WhenUserIsNotAuthor() throws Exception {
        // arrange
        Long commentId = 1L;

        doThrow(new UnauthorizedException("You are not authorized to delete this comment"))
                .when(commentService).deleteComment(commentId);

        // act & assert
        mockMvc.perform(delete("/api/comments/{id}", commentId).with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
