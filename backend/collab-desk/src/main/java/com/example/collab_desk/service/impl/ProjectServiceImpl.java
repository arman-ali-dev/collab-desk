package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateProjectRequest;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.repository.ProjectRepository;
import com.example.collab_desk.service.ProjectService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ProjectResponseDto createProject(CreateProjectRequest request) {
        User currentUser = userService.getCurrentUser();
        Set<User> members = userService.getUsersById(request.getMembers());

        Project project = new Project(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getStatus(),
                request.getProgress(),
                request.getLogo(),
                request.getOrganizationName()
        );

        project.setMembers(members);
        project.setCreatedBy(currentUser);

        if (request.getUrl() != null && !request.getUrl().trim().isEmpty()) {
            project.setUrl(request.getUrl());
        }

        return mapToProjectResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDto getProject(Long id) {
        Project project = this.getProjectById(id);
        return mapToProjectResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProject(Long id, UpdateProjectRequest request) {
        Project existingProject = getProjectById(id);
        Set<User> newMembers = userService.getUsersById(request.getMembers());

        existingProject.setTitle(request.getTitle());
        existingProject.setDescription(request.getDescription());
        existingProject.setPriority(request.getPriority());
        existingProject.setStatus(request.getStatus());
        existingProject.setProgress(request.getProgress());
        existingProject.setLogo(request.getLogo());
        existingProject.setOrganizationName(request.getOrganizationName());
        existingProject.setUrl(request.getUrl());
        existingProject.setMembers(newMembers);

        return mapToProjectResponse(projectRepository.save(existingProject));
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = this.getProjectById(id);
        projectRepository.delete(project);
    }

    @Override
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToProjectResponse).toList();
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @Override
    public List<ProjectResponseDto> searchProjects(String keyword) {
        return projectRepository.findByTitleContainingIgnoreCase(keyword)
                .stream().map(this::mapToProjectResponse).toList();
    }

    @Override
    public List<ProjectResponseDto> filterProjects(String status, String priority) {
        List<Project> projects;

        if (status != null && priority != null) {
            projects = projectRepository.findByStatusAndPriority(ProjectStatus.valueOf(status),
                    ProjectPriority.valueOf(priority));
        } else if (status != null) {
            projects = projectRepository.findByStatus(ProjectStatus.valueOf(status));
        } else if (priority != null) {
            projects = projectRepository.findByPriority(ProjectPriority.valueOf(priority));
        } else {
            projects = projectRepository.findAll();
        }

        return projects.stream().
                map(this::mapToProjectResponse).
                toList();
    }

    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(user.getId(), user.getFullName(), user.getEmail());
    }

    private ProjectResponseDto mapToProjectResponse(Project project) {
        return new ProjectResponseDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getPriority(),
                project.getStatus(),
                project.getProgress(),
                project.getLogo(),
                project.getOrganizationName(),
                project.getUrl(),
                project.getMembers().stream().map(this::mapToUserResponse).toList());
    }


}
