package com.example.collab_desk.dto.requestDto;

import com.example.collab_desk.enums.TaskCategory;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private TaskStatus status;

    @NotNull(message = "Task Category is required")
    private TaskCategory category;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;

    @NotNull(message = "Estimated Time is required")
    @Positive(message = "Estimated Time must be greater than 0")
    @Max(value = 100000, message = "Estimated time is too large")
    private Long estimatedTime;

    @NotNull(message = "Project is required")
    @Positive(message = "Project must be greater than 0")
    private Long projectId;

    @NotEmpty(message = "At least one member is required")
    @Size(max = 50, message = "Cannot assign more than 50 members")
    private List<@NotNull(message = "User id cannot be null")
        @Positive(message = "User must be greater than 0") Long> assignedTo;
}
