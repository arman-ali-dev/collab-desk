package com.example.collab_desk.dto.requestDto;

import com.example.collab_desk.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Chat room is required")
    private Long roomId;

    @NotNull(message = "Message type is required")
    private MessageType type;

    private String caption;
    private String filename;
}
