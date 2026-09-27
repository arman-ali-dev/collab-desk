package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.CreateCommentRequestDto;
import com.example.collab_desk.dto.responseDto.CommentResponseDto;
import com.example.collab_desk.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(Long taskId, CreateCommentRequestDto request);

    CommentResponseDto updateComment(Long id, CreateCommentRequestDto request);

    CommentResponseDto getComment(Long id);

    void deleteComment(Long id);

    Comment getCommentById(Long id);

    List<CommentResponseDto> getCommentsByTask(Long taskId);
}
