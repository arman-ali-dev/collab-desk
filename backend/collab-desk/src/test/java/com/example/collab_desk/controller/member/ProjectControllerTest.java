package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    private ProjectResponseDto buildProjectResponse(Long id) {
        return new ProjectResponseDto(
                id, "Test Project", "Test Description",
                ProjectPriority.HIGH, ProjectStatus.ACTIVE, 50.0,
                "logo.png", "Test Org", "https://test.com", List.of()
        );
    }

    @Test
    @WithMockUser
    public void getProject_shouldReturn200WithProjectWhenFound() throws Exception {
        // arrange
        Long projectId = 1L;
        ProjectResponseDto response = buildProjectResponse(projectId);

        when(projectService.getProject(projectId)).thenReturn(response);

        // act & assert
        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Project"));
    }


    @Test
    @WithMockUser
    public void getProject_shouldReturn404WhenProjectNotFound() throws Exception {
        // arrange
        Long projectId = 99L;

        when(projectService.getProject(projectId))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // act & assert
        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser
    public void getAllProjects_shouldReturn200WithAllProjects() throws Exception {
        // arrange
        ProjectResponseDto project1 = buildProjectResponse(1L);
        project1.setTitle("Project 1");

        ProjectResponseDto project2 = buildProjectResponse(2L);
        project2.setTitle("Project 2");

        List<ProjectResponseDto> projects = new ArrayList<>(List.of(project1, project2));

        when(projectService.getAllProjects()).thenReturn(projects);

        // act & assert
        mockMvc.perform(get("/api/projects/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Project 1"))
                .andExpect(jsonPath("$[1].title").value("Project 2"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }


    @Test
    @WithMockUser
    public void getAllProjects_shouldReturn200WithEmptyListProjects() throws Exception {
        // arrange
        List<ProjectResponseDto> projects = new ArrayList<>(List.of());

        when(projectService.getAllProjects()).thenReturn(projects);

        // act & assert
        mockMvc.perform(get("/api/projects/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // Search Projects Tests

    @Test
    @WithMockUser
    public void searchProjects_shouldReturn200WithProjects() throws Exception {
        // arrange
        String keyword = "Test";

        ProjectResponseDto project1 = buildProjectResponse(1L);
        project1.setTitle("Test");
        ProjectResponseDto project2 = buildProjectResponse(2L);
        project2.setTitle("Test");
        List<ProjectResponseDto> projects = new ArrayList<>(List.of(project1, project2));

        when(projectService.searchProjects(eq(keyword))).thenReturn(projects);

        // act & assert
        mockMvc.perform(get("/api/projects/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Test"))
                .andExpect(jsonPath("$[1].title").value("Test"));
    }

    @Test
    @WithMockUser
    public void searchProjects_shouldReturn200WithEmptyProjectList() throws Exception {
        // arrange
        String keyword = "Test";
        List<ProjectResponseDto> projects = new ArrayList<>(List.of());

        when(projectService.searchProjects(eq(keyword))).thenReturn(projects);

        // act & assert
        mockMvc.perform(get("/api/projects/search")
                        .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void searchProjects_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/projects/search")
                        .param("keyword", "Test"))
                .andExpect(status().isUnauthorized());

        verify(projectService, never()).searchProjects(anyString());
    }

}
