package com.example.collab_desk.dto.responseDto;

import com.example.collab_desk.enums.TaskCategory;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskCategory category;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long estimatedTime;
    private List<UserResponseDto> assignedTo;
}
