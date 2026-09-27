package com.example.collab_desk.controller.admin;

import com.example.collab_desk.config.SecurityConfig;
import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateProjectRequest;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.service.ProjectService;
import com.example.collab_desk.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminProjectController.class)
@Import(SecurityConfig.class)
public class AdminProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateProjectRequest buildCreateRequest(List<Long> memberIds) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle("Test Project");
        request.setDescription("Test Description");
        request.setPriority(ProjectPriority.HIGH);
        request.setStatus(ProjectStatus.ACTIVE);
        request.setProgress(67.77);
        request.setMembers(memberIds);
        request.setLogo("http://localhost:8080/logo.png");
        request.setOrganizationName("Test organization name");
        request.setUrl("https://api.test.com");
        return request;
    }

    private UpdateProjectRequest buildUpdateRequest(List<Long> memberIds) {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle("New Test Project");
        request.setDescription("New Test Description");
        request.setPriority(ProjectPriority.HIGH);
        request.setStatus(ProjectStatus.COMPLETED);
        request.setProgress(99.77);
        request.setMembers(memberIds);
        request.setLogo("http://localhost:8080/new-logo.png");
        request.setOrganizationName("New Test organization name");
        request.setUrl("https://new-api.test.com");
        return request;
    }

    // Create Project Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createProject_shouldReturn201WhenRequestIsValid() throws Exception {
        // arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));

        UserResponseDto member = new UserResponseDto(1L, "Test User", "test@gmail.com");

        ProjectResponseDto response = new ProjectResponseDto(
                1L, "Test Project", "Test Description",
                ProjectPriority.HIGH, ProjectStatus.ACTIVE, 67.77,
                "http://localhost:8080/logo.png", "Test organization name",
                "https://api.test.com",
                List.of(member));

        when(projectService.createProject(any(CreateProjectRequest.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/admin/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.progress").value(67.77))
                .andExpect(jsonPath("$.logo").value("http://localhost:8080/logo.png"))
                .andExpect(jsonPath("$.organizationName").value("Test organization name"))
                .andExpect(jsonPath("$.url").value("https://api.test.com"))
                .andExpect(jsonPath("$.members[0].id").value(1L));

    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void createProject_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));

        // act & assert
        mockMvc.perform(post("/api/admin/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(projectService, never()).createProject(any());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createProject_shouldReturn400WhenValidationFails() throws Exception {
        // arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));
        request.setTitle("    ");

        // act & assert
        mockMvc.perform(post("/api/admin/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any());
    }

    @Test
    public void createProject_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));

        // act & assert
        mockMvc.perform(post("/api/admin/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).createProject(any());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createProject_shouldReturn404WhenMemberNotFound() throws Exception {
        // arrange
        CreateProjectRequest request = buildCreateRequest(List.of(1L));

        when(projectService.createProject(any(CreateProjectRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // act & assert
        mockMvc.perform(post("/api/admin/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // Delete Project Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteProject_shouldReturn204WhenProjectDeleted() throws Exception {
        // arrange
        Long projectId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProject(projectId);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteProject_shouldReturn404WhenProjectNotFound() throws Exception {
        // arrange
        Long projectId = 1L;

        doThrow(new ResourceNotFoundException("Project not found"))
                .when(projectService).deleteProject(projectId);

        // act & assert
        mockMvc.perform(delete("/api/admin/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void deleteProject_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        Long projectId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(projectService, never()).deleteProject(projectId);
    }

    @Test
    public void deleteProject_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        Long projectId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).deleteProject(projectId);
    }


    // Update Project Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateProject_shouldReturn200WhenRequestIsValid() throws Exception {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));

        ProjectResponseDto response = new ProjectResponseDto(projectId,
                "New Test title", "New Test Description",
                ProjectPriority.HIGH, ProjectStatus.COMPLETED, 99.99,
                "http://localhost:8080/new-logo.png",
                "New Test Organization Name", "https://new-api-test.com",
                List.of(new UserResponseDto(1L, "Test", "test@gmial.com")));

        when(projectService.updateProject(eq(projectId), any(UpdateProjectRequest.class)))
                .thenReturn(response);

        // act & assert
        mockMvc.perform(put("/api/admin/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.title").value("New Test title"))
                .andExpect(jsonPath("$.description").value("New Test Description"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.progress").value(99))
                .andExpect(jsonPath("$.logo").value("http://localhost:8080/new-logo.png"))
                .andExpect(jsonPath("$.organizationName").value("New Test Organization Name"))
                .andExpect(jsonPath("$.url").value("https://new-api-test.com"))
                .andExpect(jsonPath("$.members[0].id").value(1));

    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void updateProject_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));

        // act & assert
        mockMvc.perform(put("/api/admin/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(projectService, never()).updateProject(anyLong(), any(UpdateProjectRequest.class));
    }

    @Test
    public void updateProject_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));

        // act & assert
        mockMvc.perform(put("/api/admin/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).updateProject(anyLong(), any(UpdateProjectRequest.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateProject_shouldReturn404WhenProjectNotFound() throws Exception {
        // arrange
        Long projectId = 99L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));

        when(projectService.updateProject(eq(projectId), any(UpdateProjectRequest.class)))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // act & assert
        mockMvc.perform(put("/api/admin/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateProject_shouldReturn400WhenValidationFails() throws Exception {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));
        request.setTitle("");

        // act & assert
        mockMvc.perform(put("/api/admin/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).updateProject(anyLong(), any(UpdateProjectRequest.class));

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateProject_shouldReturn404WhenMemberNotFound() throws Exception {
        // arrange
        Long projectId = 1L;
        UpdateProjectRequest request = buildUpdateRequest(List.of(1L));

        when(projectService.updateProject(eq(projectId), any(UpdateProjectRequest.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // act & assert
        mockMvc.perform(put("/api/admin/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }


}
