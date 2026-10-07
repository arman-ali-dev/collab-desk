package com.example.collab_desk.dto.responseDto;

import com.example.collab_desk.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private Long id;
    private Long roomId;
    private String content;
    private MessageType type;
    private SenderResponseDto sender;
    private String caption;
    private String filename;
    private LocalDateTime sentAt;
}
