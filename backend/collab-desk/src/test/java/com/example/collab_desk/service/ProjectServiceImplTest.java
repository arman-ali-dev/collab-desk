package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.NotificationType;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.ProjectRepository;
import com.example.collab_desk.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private CreateProjectRequest buildCreateRequest(List<Long> memberIds) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Test Project");
        request.setDescription("Test Description");
        request.setPriority(ProjectPriority.HIGH);
        request.setStatus(ProjectStatus.ACTIVE);
        request.setProgress(67.77);
        request.setMembers(memberIds);
        request.setLogo("https://localhost:8080/logo.png");
        request.setOrganizationName("Test organization name");
        request.setUrl("https://api.test.com");
        return request;
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }


    // Create Project Tests

    @Test
    public void createProject_shouldSaveProjectWithAllFieldsFromRequest() {
        // Arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));
        User currentUser = userWithId(10L);
        User member = userWithId(2L);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getUsersById(List.of(1L)))
                .thenReturn(new HashSet<>(Set.of(member)));
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        projectService.createProject(request);

        // Assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());

        Project saved = captor.getValue();

        assertEquals("Test Project", saved.getTitle());
        assertEquals("Test Description", saved.getDescription());
        assertEquals(ProjectPriority.HIGH, saved.getPriority());
        assertEquals(ProjectStatus.ACTIVE, saved.getStatus());
        assertEquals(67.77, saved.getProgress());
        assertEquals("https://localhost:8080/logo.png", saved.getLogo());
        assertEquals("Test organization name", saved.getOrganizationName());
        assertEquals("https://api.test.com", saved.getUrl());
        assertSame(currentUser, saved.getCreatedBy());
        assertTrue(saved.getMembers().contains(member));

        verify(chatRoomService).createRoom(saved);
        verify(notificationService).notify(
                eq(member),
                eq(NotificationType.PROJECT),
                eq("Added to project: Test Project"),
                eq("You have been added as a member of the project Test Project."));
    }

    @Test
    public void createProject_shouldThrowExceptionWhenMemberNotFound() {
        // Arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L, 2L));

        when(userService.getCurrentUser()).thenReturn(userWithId(10L));
        when(userService.getUsersById(List.of(1L, 2L)))
                .thenThrow(new ResourceNotFoundException("User not found with ids : [2]"));

        // act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.createProject(request)
        );

        verify(projectRepository, never()).save(any(Project.class));
        verify(chatRoomService, never()).createRoom(any(Project.class));
        verify(notificationService, never()).notify(any(User.class),
                any(NotificationType.class), anyString(), anyString());
    }

    @Test
    public void createProject_shouldThrowExceptionWhenUserNotAuthenticated() {
        // Arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));

        when(userService.getCurrentUser())
                .thenThrow(new UnauthorizedException("User not authenticated"));

        // Act & Assert
        assertThrows(
                UnauthorizedException.class,
                () -> projectService.createProject(request)
        );

        verify(userService, never()).getUsersById(anyList());
        verify(projectRepository, never()).save(any(Project.class));
        verify(chatRoomService, never()).createRoom(any(Project.class));
        verify(notificationService, never()).notify(any(User.class),
                any(NotificationType.class), anyString(), anyString());
    }

    @Test
    public void createProject_shouldThrowExceptionWhenSaveFails() {
        // Arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));
        User currentUser = userWithId(10L);
        User member = userWithId(2L);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.getUsersById(List.of(1L)))
                .thenReturn(new HashSet<>(Set.of(member)));
        when(projectRepository.save(any(Project.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Throws
        assertThrows(
                RuntimeException.class,
                () -> projectService.createProject(request)
        );

        verify(chatRoomService, never()).createRoom(any(Project.class));
        verify(notificationService, never()).notify(any(User.class),
                any(NotificationType.class), anyString(), anyString());
    }

    // Update Project Tests

    private UpdateProjectRequest buildUpdateRequest(List<Long> ids) {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle("New title");
        request.setDescription("New description");
        request.setPriority(ProjectPriority.MEDIUM);
        request.setStatus(ProjectStatus.ON_HOLD);
        request.setProgress(88.90);
        request.setMembers(ids);
        request.setLogo("http://localhost:8080/new-logo.png");
        request.setOrganizationName("new organization name");
        request.setUrl("https://new-api.test.com");
        return request;
    }

    private Project buildExistingProject(User oldMember) {
        Project project = new Project();
        project.setTitle("Old title");
        project.setDescription("Old description");
        project.setPriority(ProjectPriority.LOW);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setProgress(22.22);
        project.setMembers(Set.of(oldMember));
        project.setLogo("http://localhost:8080/old-logo.png");
        project.setOrganizationName("Old organization name");
        project.setUrl("https://old-api.test.com");
        return project;
    }

    @Test
    public void updateProject_shouldUpdateAllFieldsOnExistingProject() {
        // arrange
        Long projectId = 1L;
        User oldMember = userWithId(2L);
        User newMember = userWithId(1L);
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));
        Project existingProject = buildExistingProject(oldMember);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(userService.getUsersById(List.of(1L))).thenReturn(new HashSet<>(Set.of(newMember)));
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(i -> i.getArgument(0));

        // act
        projectService.updateProject(projectId, request);

        // assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());

        Project saved = captor.getValue();

        assertSame(existingProject, saved);
        assertEquals("New title", saved.getTitle());
        assertEquals("New description", saved.getDescription());
        assertEquals(ProjectPriority.MEDIUM, saved.getPriority());
        assertEquals(ProjectStatus.ON_HOLD, saved.getStatus());
        assertEquals(88.90, saved.getProgress());
        assertFalse(saved.getMembers().contains(oldMember));
        assertEquals("http://localhost:8080/new-logo.png", saved.getLogo());
        assertEquals("new organization name", saved.getOrganizationName());
        assertEquals("https://new-api.test.com", saved.getUrl());
    }

    @Test
    public void updateProject_shouldThrowExceptionWhenProjectNotFound() {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.updateProject(projectId, request)
        );

        verify(userService, never()).getUsersById(anyList());

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void updateProject_shouldThrowExceptionWhenMemberNotFound() {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L, 2L));
        Project existingProject = buildExistingProject(userWithId(2L));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(userService.getUsersById(List.of(1L, 2L)))
                .thenThrow(new ResourceNotFoundException("Users not found with ids: [2]"));

        // act & assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> projectService.updateProject(projectId, request)
        );

        verify(projectRepository, never())
                .save(any(Project.class));
    }

    @Test
    public void updateProject_shouldThrowExceptionWhenSaveFails() {
        // arrange
        Long projectId = 1L;
        User oldMember = userWithId(2L);
        User newMember = userWithId(1L);
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));
        Project existingProject = buildExistingProject(oldMember);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(userService.getUsersById(List.of(1L))).thenReturn(new HashSet<>(Set.of(newMember)));
        when(projectRepository.save(any(Project.class)))
                .thenThrow(new RuntimeException("Database Error"));

        // act & assert
        assertThrows(
                RuntimeException.class,
                () -> projectService.updateProject(projectId, request)
        );
    }

    // Delete Project Tests

    @Test
    public void deleteProject_shouldDeleteProjectSuccessfully() {
        // arrange
        Long projectId = 1L;
        Project existingProject = buildExistingProject(userWithId(1L));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));

        // act
        projectService.deleteProject(projectId);

        // assert
        verify(projectRepository).delete(existingProject);
    }

    @Test
    public void deleteProject_shouldThrowExceptionWhenProjectNotFound() {
        // arrange
        Long projectId = 1L;
        Project existingProject = buildExistingProject(userWithId(1L));

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // act * assert
        assertThrows(
                RuntimeException.class,
                () -> projectService.deleteProject(projectId)
        );

        verify(projectRepository, never()).delete(existingProject);
    }

    // Get All Project Test

    @Test
    public void getAllProjects_shouldReturnAllProjects() {
        // arrange
        Project project1 = buildExistingProject(userWithId(1L));
        project1.setTitle("Project 1");
        Project project2 = buildExistingProject(userWithId(2L));
        project2.setTitle("Project 2");

        List<Project> projects = new ArrayList<>(List.of(project1, project2));

        when(projectRepository.findAllByOrderByCreatedAtDesc()).thenReturn(projects);

        // act
        List<ProjectResponseDto> response = projectService.getAllProjects();

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Project 1", response.get(0).getTitle());
        assertEquals("Project 2", response.get(1).getTitle());
    }

    @Test
    public void getAllProjects_shouldReturnEmptyProjectList() {
        // arrange
        List<Project> projects = new ArrayList<>();
        when(projectRepository.findAllByOrderByCreatedAtDesc()).thenReturn(projects);

        // act
        List<ProjectResponseDto> response = projectService.getAllProjects();

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    // Get Project Test

    @Test
    public void getProject_shouldReturnProjectSuccessfully() {
        // arrange
        Long projectId = 1L;
        Project project = buildExistingProject(userWithId(1L));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // act
        ProjectResponseDto response = projectService.getProject(projectId);

        // assert
        assertNotNull(response);
        assertEquals(project.getTitle(), response.getTitle());
        assertEquals(project.getDescription(), response.getDescription());
        assertEquals(project.getMembers().size(), response.getMembers().size());
    }

    @Test
    public void getProject_shouldThrowExceptionWhenProjectNotFound() {
        // arrange
        Long projectId = 99L;

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProject(projectId));
    }

    // Search Project Tests

    @Test
    public void searchProjects_shouldReturnProjectsSuccessfully() {
        // arrange
        String keyword = "Hello World";

        Project project1 = buildExistingProject(userWithId(1L));
        project1.setTitle("Hello World");
        Project project2 = buildExistingProject(userWithId(1L));
        project2.setTitle("Hello World");
        List<Project> projects = new ArrayList<>(List.of(project1, project2));

        when(projectRepository.findByTitleContainingIgnoreCase(keyword)).thenReturn(projects);

        // act
        List<ProjectResponseDto> response = projectService.searchProjects(keyword);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Hello World", response.get(0).getTitle());
        assertEquals("Hello World", response.get(1).getTitle());
    }

    @Test
    public void searchProjects_shouldReturnEmptyProjectListSuccessfully() {
        // arrange
        String keyword = "Test";

        List<Project> projects = new ArrayList<>(List.of());

        when(projectRepository.findByTitleContainingIgnoreCase(keyword)).thenReturn(projects);

        // act
        List<ProjectResponseDto> response = projectService.searchProjects(keyword);

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    // Filter Project By Status

    @Test
    public void filterProjects_shouldReturnProjectsByStatus() {
        // arrange
        String status = "COMPLETED";

        Project project1 = buildExistingProject(userWithId(1L));
        project1.setId(1L);
        project1.setStatus(ProjectStatus.COMPLETED);
        Project project2 = buildExistingProject(userWithId(1L));
        project2.setId(2L);
        project2.setStatus(ProjectStatus.COMPLETED);
        List<Project> projects = new ArrayList<>(List.of(project1, project2));

        when(projectRepository.findByStatus(ProjectStatus.valueOf(status))).thenReturn(projects);

        // act
        List<ProjectResponseDto> response = projectService.filterProjects(status, null);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(ProjectStatus.COMPLETED, response.get(0).getStatus());
        assertEquals(ProjectStatus.COMPLETED, response.get(1).getStatus());
    }
}
