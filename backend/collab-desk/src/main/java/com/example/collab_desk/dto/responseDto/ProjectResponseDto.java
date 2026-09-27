package com.example.collab_desk.dto.responseDto;

import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDto {
    private Long id;
    private String title;
    private String description;
    private ProjectPriority priority;
    private ProjectStatus status;
    private Double progress;
    private String logo;
    private String organizationName;
    private String url;
    private List<UserResponseDto> members;
}
