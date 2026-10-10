package com.example.collab_desk.dto.responseDto;

import com.example.collab_desk.enums.ReminderLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ReminderResponseDto {
    private Long taskId;
    private String title;
    private String message;
    private String projectName;
    private ReminderLevel level;
    private LocalDate dueDate;
}
