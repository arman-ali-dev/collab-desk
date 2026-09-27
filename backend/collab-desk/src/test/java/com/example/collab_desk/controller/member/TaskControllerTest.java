package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.enums.TaskCategory;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    private TaskResponseDto buildResponse(Long id) {
        return new TaskResponseDto(
                id,
                "Test title",
                "Test description",
                TaskStatus.IN_PROGRESS,
                TaskCategory.DESIGN,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(2),
                4L,
                List.of());
    }

    // Update Task's Status Tests

    @Test
    @WithMockUser
    public void updateStatus_shouldReturn200WhenStatusUpdated() throws Exception {
        // arrange
        Long taskId = 1L;
        TaskResponseDto taskResponseDto = buildResponse(taskId);
        taskResponseDto.setStatus(TaskStatus.DONE);

        when(taskService.updateStatus(taskId, TaskStatus.DONE)).thenReturn(taskResponseDto);

        // act & assert
        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                        .param("status", "DONE")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @WithMockUser
    public void updateStatus_shouldReturn400WhenStatusParamMissing() throws Exception {
        // arrange
        Long taskId = 1L;

        // act & assert
        mockMvc.perform(patch("/api/tasks/{id}/status", taskId).with(csrf()))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateStatus(anyLong(), any());
    }

    @Test
    @WithMockUser
    public void updateStatus_shouldReturn400WhenStatusIsInvalidEnum() throws Exception {
        // arrange
        Long taskId = 1L;

        // act & assert
        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                        .param("status", "NOT_A_REAL_STATUS")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateStatus(anyLong(), any());
    }

    @Test
    @WithMockUser
    public void updateStatus_shouldReturn404WhenTaskNotFound() throws Exception {
        // arrange
        Long taskId = 1L;

        when(taskService.updateStatus(taskId, TaskStatus.DONE))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // act & assert
        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                        .param("status", "DONE")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // Get Tasks By Project Tests

    @Test
    @WithMockUser
    public void getTasksByProject_shouldReturn200WithTasksByProject() throws Exception {
        // Arrange
        Long projectId = 1L;

        TaskResponseDto task1 = buildResponse(1L);
        task1.setTitle("Task 1");

        TaskResponseDto task2 = buildResponse(2L);
        task2.setTitle("Task 2");

        List<TaskResponseDto> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskService.getTasksByProject(projectId)).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/tasks/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    @WithMockUser
    public void getTasksByProject_shouldReturn404WhenProjectNotFound() throws Exception {
        // Arrange
        Long projectId = 99L;

        when(taskService.getTasksByProject(projectId))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // act & assert
        mockMvc.perform(get("/api/tasks/project/{projectId}", projectId))
                .andExpect(status().isNotFound());
    }


    // Get Tasks By Year and Month

    @Test
    @WithMockUser
    public void getTasksByYearAndMonth_shouldReturn200WithTasks() throws Exception {
        // Arrange
        int year = 2026;
        int month = 9;

        TaskResponseDto task1 = buildResponse(1L);
        task1.setTitle("Task 1");
        TaskResponseDto task2 = buildResponse(2L);
        task2.setTitle("Task 2");

        List<TaskResponseDto> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskService.getMyTaskByYearAndMonth(eq(year), eq(month))).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/tasks/calender/my")
                        .param("year", "2026")
                        .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    @WithMockUser
    public void getTasksByYearAndMonth_shouldReturn200WithEmptyTaskList() throws Exception {
        // Arrange
        int year = 2026;
        int month = 9;

        List<TaskResponseDto> tasks = new ArrayList<>(List.of());

        when(taskService.getMyTaskByYearAndMonth(eq(year), eq(month))).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/tasks/calender/my")
                        .param("year", "2026")
                        .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getTasksByYearAndMonth_shouldReturn401WhenUserNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/tasks/calender/my")
                        .param("year", "2026")
                        .param("month", "9"))
                .andExpect(status().isUnauthorized());

        verify(taskService, never()).getMyTaskByYearAndMonth(anyInt(), anyInt());
    }

}
