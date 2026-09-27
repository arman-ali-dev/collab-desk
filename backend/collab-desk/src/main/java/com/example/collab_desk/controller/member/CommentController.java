package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.requestDto.CreateCommentRequestDto;
import com.example.collab_desk.dto.responseDto.CommentResponseDto;
import com.example.collab_desk.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{taskId}")
    public ResponseEntity<CommentResponseDto> createCommentHandler(
            @Valid @RequestBody CreateCommentRequestDto req,
            @PathVariable Long taskId) {
        CommentResponseDto response = commentService.createComment(taskId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateCommentHandler(
            @PathVariable Long id, @Valid @RequestBody CreateCommentRequestDto request
    ) {
        CommentResponseDto response = commentService.updateComment(id, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long id) {
        CommentResponseDto response = commentService.getComment(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentByTaskHandler(@PathVariable Long taskId) {
        List<CommentResponseDto> response = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(response);
    }
}
