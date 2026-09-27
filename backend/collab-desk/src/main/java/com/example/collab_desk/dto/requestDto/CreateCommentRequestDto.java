package com.example.collab_desk.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequestDto {
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 1000, message = "content cannot be exceed 1000 characters")
    private String content;
}
