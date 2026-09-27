package com.example.collab_desk.dto.requestDto;

import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Setter
@Getter
public class CreateProjectRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Tittle cannot be exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
    private String description;

    @NotNull(message = "Priority is required")
    private ProjectPriority priority;

    @NotNull(message = "Progress is required")
    @DecimalMin(value = "0.0", message = "Progress cannot be less than 0.0")
    @DecimalMax(value = "100.0", message = "Progress cannot be greater than 100.0")
    private Double progress;

    @NotNull(message = "Status is required")
    private ProjectStatus status;

    @NotEmpty(message = "At least one member is required")
    @Size(max = 100, message = "Cannot add more than 100 members")
    private List<@NotNull(message = "Member id cannot be null")
    @Positive(message = "Member id must be greater than 1") Long> members;

    @NotBlank(message = "Logo is required")
    @Size(max = 500, message = "Logo cannot exceed 500 characters")
    private String logo;

    @NotBlank(message = "Organization name is required")
    @Size(max = 200, message = "Organization name cannot exceed 200 characters")
    private String organizationName;

    @URL(message = "Invalid URL")
    @Size(max = 500, message = "URL cannot exceed 500 characters")
    private String url;
}
