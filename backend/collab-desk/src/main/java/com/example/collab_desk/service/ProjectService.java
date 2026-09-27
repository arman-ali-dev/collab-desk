package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateProjectRequest;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;

import java.util.List;

public interface ProjectService {
    ProjectResponseDto createProject(CreateProjectRequest request);

    ProjectResponseDto getProject(Long id);

    ProjectResponseDto updateProject(Long id, UpdateProjectRequest request);

    void deleteProject(Long id);

    List<ProjectResponseDto> getAllProjects();

    Project getProjectById(Long id);

    List<ProjectResponseDto> searchProjects(String keyword);

    List<ProjectResponseDto> filterProjects(String status, String priority);
}
