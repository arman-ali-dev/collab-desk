package com.example.collab_desk.controller.admin;

import com.example.collab_desk.config.SecurityConfig;
import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.requestDto.CreateTaskRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.enums.TaskCategory;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.service.TaskService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTaskController.class)
@Import(SecurityConfig.class)
public class AdminTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    public CreateTaskRequestDto buildCreateRequest(List<Long> members) {
        CreateTaskRequestDto request = new CreateTaskRequestDto();
        request.setTitle("Test title");
        request.setDescription("Test description");
        request.setStatus(TaskStatus.TO_DO);
        request.setCategory(TaskCategory.DESIGN);
        request.setPriority(TaskPriority.LOW);
        request.setDueDate(LocalDate.now().plusDays(2));
        request.setEstimatedTime(2L);
        request.setProjectId(1L);
        request.setAssignedTo(members);
        return request;
    }

    public UpdateTaskRequestDto buildUpdateRequest(List<Long> members) {
        UpdateTaskRequestDto request = new UpdateTaskRequestDto();
        request.setTitle("New test title");
        request.setDescription("New test description");
        request.setStatus(TaskStatus.DONE);
        request.setCategory(TaskCategory.DEVELOPMENT);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.now().plusDays(3));
        request.setEstimatedTime(4L);
        request.setProjectId(1L);
        request.setAssignedTo(members);
        return request;
    }


    // Create Task Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createTask_shouldReturn201WhenRequestIsValid() throws Exception {
        // arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));
        TaskResponseDto response = new TaskResponseDto(
                1L, "Test title", "Test description",
                TaskStatus.TO_DO, TaskCategory.DESIGN, TaskPriority.LOW,
                LocalDate.now().plusDays(2), 2L,
                List.of(new UserResponseDto(1L, "Test", "test@gmail.com"))
        );

        when(taskService.createTask(any(CreateTaskRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/admin/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.status").value("TO_DO"))
                .andExpect(jsonPath("$.category").value("DESIGN"))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusDays(2).toString()))
                .andExpect(jsonPath("$.estimatedTime").value(2))
                .andExpect(jsonPath("$.assignedTo[0].id").value(1));
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void createTask_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));

        // act & assert
        mockMvc.perform(post("/api/admin/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(taskService, never()).createTask(any(CreateTaskRequestDto.class));
    }

    @Test
    public void createTask_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));

        // act & assert
        mockMvc.perform(post("/api/admin/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(taskService, never()).createTask(any(CreateTaskRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createTask_shouldReturn400WhenValidationFails() throws Exception {
        // arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));
        request.setTitle("    ");

        // act & assert
        mockMvc.perform(post("/api/admin/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createTask_shouldReturn404WhenMemberNotFound() throws Exception {
        // arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));

        when(taskService.createTask(any(CreateTaskRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // act & assert
        mockMvc.perform(post("/api/admin/tasks")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // Update Task Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateTask_shouldReturn200WhenRequestIsValid() throws Exception {
        // arrange
        Long taskId = 1L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(2L));

        TaskResponseDto response = new TaskResponseDto(
                1L, "New test title", "New test description",
                TaskStatus.DONE, TaskCategory.DEVELOPMENT, TaskPriority.HIGH,
                LocalDate.now().plusDays(3), 4L,
                List.of(new UserResponseDto(1L, "Test", "test@gmail.com"),
                        new UserResponseDto(2L, "Test2", "test2@gmail.com"))
        );

        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(put("/api/admin/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New test title"))
                .andExpect(jsonPath("$.description").value("New test description"))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.category").value("DEVELOPMENT"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value(LocalDate.now().plusDays(3).toString()))
                .andExpect(jsonPath("$.estimatedTime").value(4))
                .andExpect(jsonPath("$.assignedTo[0].id").value(1))
                .andExpect(jsonPath("$.assignedTo[1].id").value(2));
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void updateTask_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        Long taskId = 1L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(2L));

        // act & assert
        mockMvc.perform(put("/api/admin/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(taskService, never()).updateTask(anyLong(), any(UpdateTaskRequestDto.class));
    }

    @Test
    public void updateTask_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        Long taskId = 1L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(2L));

        // act & assert
        mockMvc.perform(put("/api/admin/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(taskService, never()).updateTask(anyLong(), any(UpdateTaskRequestDto.class));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateTask_shouldReturn404WhenTaskNotFound() throws Exception {
        // arrange
        Long taskId = 1L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(2L));

        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        // act & assert
        mockMvc.perform(put("/api/admin/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateTask_shouldReturn404WhenMemberNotFound() throws Exception {
        // arrange
        Long taskId = 1L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L));

        when(taskService.updateTask(eq(taskId), any(UpdateTaskRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        // act & assert
        mockMvc.perform(put("/api/admin/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateTask_shouldReturn400WhenValidationFails() throws Exception {
        // arrange
        Long taskId = 1L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L));
        request.setTitle("    ");

        // act & assert
        mockMvc.perform(put("/api/admin/tasks/{id}", taskId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).updateTask(anyLong(), any());
    }

    // Delete Task Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteTask_shouldReturn204WhenTaskDeleted() throws Exception {
        // arrange
        Long taskId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/tasks/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteTask_shouldReturn404WhenTaskNotFound() throws Exception {
        // arrange
        Long taskId = 1L;

        doThrow(new ResourceNotFoundException("Task not found"))
                .when(taskService).deleteTask(taskId);

        // act & assert
        mockMvc.perform(delete("/api/admin/tasks/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void deleteTask_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        Long taskId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/tasks/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(taskService, never()).deleteTask(taskId);
    }

    @Test
    public void deleteTask_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        Long taskId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/tasks/{id}", taskId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(taskService, never()).deleteTask(taskId);
    }

    // Get All Tasks Tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getAllTasks_shouldReturn200WithTaskListSuccessfully() throws Exception {
        // arrange
        TaskResponseDto task1 = new TaskResponseDto(
                1L, "Task 1", "New test description",
                TaskStatus.DONE, TaskCategory.DEVELOPMENT, TaskPriority.HIGH,
                LocalDate.now().plusDays(3), 4L,
                List.of(new UserResponseDto(1L, "Test", "test@gmail.com"))
        );

        TaskResponseDto task2 = new TaskResponseDto(
                2L, "Task 2", "New test description",
                TaskStatus.DONE, TaskCategory.DEVELOPMENT, TaskPriority.HIGH,
                LocalDate.now().plusDays(3), 4L,
                List.of(new UserResponseDto(1L, "Test", "test@gmail.com"))
        );
        List<TaskResponseDto> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskService.getAllTasks()).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/admin/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getAllTasks_shouldReturn200WithEmptyTaskListSuccessfully() throws Exception {
        // arrange
        List<TaskResponseDto> tasks = new ArrayList<>(List.of());

        when(taskService.getAllTasks()).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/admin/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllTasks_shouldReturn401WhenUserNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/tasks/all"))
                .andExpect(status().isUnauthorized());

        verify(taskService, never()).getAllTasks();
    }

    @Test
    @WithMockUser
    public void getAllTasks_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/tasks/all"))
                .andExpect(status().isForbidden());

        verify(taskService, never()).getAllTasks();
    }

    // Get Tasks By Year and Month

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getTasksByYearAndMonth_shouldReturn200WithTasks() throws Exception {
        // Arrange
        int year = 2026;
        int month = 9;

        TaskResponseDto task1 = new TaskResponseDto(
                1L,
                "Task 1",
                "Test description",
                TaskStatus.IN_PROGRESS,
                TaskCategory.DESIGN,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(2),
                4L,
                List.of());

        TaskResponseDto task2 = new TaskResponseDto(
                2L,
                "Task 2",
                "Test description",
                TaskStatus.IN_PROGRESS,
                TaskCategory.DESIGN,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(2),
                4L,
                List.of());

        List<TaskResponseDto> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskService.getTaskByYearAndMonth(eq(year), eq(month))).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/admin/tasks/calender")
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
    @WithMockUser(authorities = "ADMIN")
    public void getTasksByYearAndMonth_shouldReturn200WithEmptyTaskList() throws Exception {
        // Arrange
        int year = 2026;
        int month = 9;

        List<TaskResponseDto> tasks = new ArrayList<>(List.of());

        when(taskService.getTaskByYearAndMonth(eq(year), eq(month))).thenReturn(tasks);

        // act & assert
        mockMvc.perform(get("/api/admin/tasks/calender")
                        .param("year", "2026")
                        .param("month", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getTasksByYearAndMonth_shouldReturn401WhenUserNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/tasks/calender")
                        .param("year", "2026")
                        .param("month", "9"))
                .andExpect(status().isUnauthorized());

        verify(taskService, never()).getTaskByYearAndMonth(anyInt(), anyInt());
    }

    @Test
    @WithMockUser
    public void getTasksByYearAndMonth_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/tasks/calender")
                        .param("year", "2026")
                        .param("month", "9"))
                .andExpect(status().isForbidden());

        verify(taskService, never()).getTaskByYearAndMonth(anyInt(), anyInt());
    }
}
