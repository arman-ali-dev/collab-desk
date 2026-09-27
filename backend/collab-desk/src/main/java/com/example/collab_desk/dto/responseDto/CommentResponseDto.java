package com.example.collab_desk.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long taskId;
}
