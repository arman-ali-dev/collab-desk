package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.CreateTaskRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.ReminderResponseDto;
import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.*;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.TaskRepository;
import com.example.collab_desk.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private CreateTaskRequestDto buildCreateRequest(List<Long> assignedUsers) {
        CreateTaskRequestDto request = new CreateTaskRequestDto();
        request.setTitle("Test title");
        request.setDescription("Test description");
        request.setStatus(TaskStatus.TO_DO);
        request.setCategory(TaskCategory.DESIGN);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setEstimatedTime(4L);
        request.setProjectId(1L);
        request.setAssignedTo(assignedUsers);

        return request;
    }

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Project projectWithId(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }

    // Create Task Tests

    @Test
    public void createTask_shouldSaveTaskWithAllFieldsFromRequest() {
        // Arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));
        User member = userWithId(1L);
        Project project = projectWithId(1L);
        project.setTitle("Test project");

        when(projectService.getProjectById(request.getProjectId()))
                .thenReturn(project);
        when(userService.getUsersById(List.of(1L)))
                .thenReturn(new HashSet<>(Set.of(member)));
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        taskService.createTask(request);

        // Assert
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        Task saved = captor.getValue();

        assertEquals("Test title", saved.getTitle());
        assertEquals("Test description", saved.getDescription());
        assertEquals(TaskStatus.TO_DO, saved.getStatus());
        assertEquals(TaskCategory.DESIGN, saved.getCategory());
        assertEquals(TaskPriority.HIGH, saved.getPriority());
        assertEquals(LocalDate.now().plusDays(5), saved.getDueDate());
        assertEquals(4L, saved.getEstimatedTime());
        assertSame(project, saved.getProject());
        assertTrue(saved.getAssignedTo().contains(member));

        verify(notificationService).notify(
                eq(member),
                eq(NotificationType.TASK),
                eq("New task assigned in Test project"),
                eq("You have been assigned a new task in the project Test project."));
    }

    @Test
    public void createTask_shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));

        when(projectService.getProjectById(request.getProjectId()))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));

        verify(userService, never()).getUsersById(anyList());
        verify(notificationService, never()).notify(any(User.class), any(NotificationType.class),
                anyString(), anyString());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void createTask_shouldThrowExceptionWhenMemberNotFound() {
        // Arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L, 2L));

        when(projectService.getProjectById(request.getProjectId()))
                .thenReturn(projectWithId(request.getProjectId()));
        when(userService.getUsersById(List.of(1L, 2L)))
                .thenThrow(new ResourceNotFoundException("Users not found with ids: [2]"));

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));

        verify(notificationService, never()).notify(any(User.class), any(NotificationType.class),
                anyString(), anyString());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void createTask_shouldThrowExceptionWhenSaveFails() {
        // Arrange
        CreateTaskRequestDto request = buildCreateRequest(List.of(1L));
        User member = userWithId(1L);
        Project project = projectWithId(1L);

        when(projectService.getProjectById(request.getProjectId()))
                .thenReturn(project);
        when(userService.getUsersById(List.of(1L)))
                .thenReturn(new HashSet<>(Set.of(member)));
        when(taskRepository.save(any(Task.class)))
                .thenThrow(new RuntimeException("Database Error"));

        // act & assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(request));

        verify(notificationService, never()).notify(any(User.class), any(NotificationType.class),
                anyString(), anyString());
    }


    // Update Task Tests

    private UpdateTaskRequestDto buildUpdateRequest(List<Long> ids) {
        UpdateTaskRequestDto request = new UpdateTaskRequestDto();
        request.setTitle("New title");
        request.setDescription("New description");
        request.setStatus(TaskStatus.REVIEW);
        request.setCategory(TaskCategory.DEVELOPMENT);
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.now().plusDays(2));
        request.setEstimatedTime(10L);
        request.setProjectId(1L);
        request.setAssignedTo(ids);
        return request;
    }

    private Task buildExistingTask(User oldMember) {
        Task task = new Task();
        task.setTitle("Old title");
        task.setDescription("Old description");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setCategory(TaskCategory.DESIGN);
        task.setPriority(TaskPriority.MEDIUM);
        task.setDueDate(LocalDate.now().minusDays(2));
        task.setEstimatedTime(4L);
        task.setAssignedTo(Set.of(oldMember));
        task.setProject(projectWithId(1L));
        return task;
    }

    @Test
    public void updateTask_shouldUpdateAllFieldsOnExistingTask() {
        // arrange
        Long taskId = 1L;
        User oldMember = userWithId(10L);
        User newMember = userWithId(1L);
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L));
        Task existingTask = buildExistingTask(oldMember);
        Project project = projectWithId(request.getProjectId());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userService.getUsersById(List.of(1L))).thenReturn(Set.of(newMember));
        when(projectService.getProjectById(request.getProjectId()))
                .thenReturn(project);
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(i -> i.getArgument(0));

        // act
        taskService.updateTask(taskId, request);

        // assert
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertSame(existingTask, saved);
        assertSame(project, saved.getProject());
        assertEquals("New title", saved.getTitle());
        assertEquals("New description", saved.getDescription());
        assertEquals(TaskStatus.REVIEW, saved.getStatus());
        assertEquals(TaskCategory.DEVELOPMENT, saved.getCategory());
        assertEquals(TaskPriority.HIGH, saved.getPriority());
        assertEquals(existingTask.getDueDate(), saved.getDueDate());
        assertEquals(10L, saved.getEstimatedTime());
        assertEquals(Set.of(newMember), saved.getAssignedTo());
        assertFalse(saved.getAssignedTo().contains(oldMember));
    }

    @Test
    public void updateTask_shouldThrowExceptionWhenTaskNotFound() {
        // arrange
        Long taskId = 10L;
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L));

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(taskId, request));

        verify(userService, never()).getUsersById(anyList());
        verify(projectService, never()).getProjectById(any());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void updateTask_shouldThrowExceptionWhenProjectNotFound() {
        // arrange
        Long taskId = 10L;
        Task existingTask = buildExistingTask(userWithId(1L));
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(projectService.getProjectById(request.getProjectId()))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(taskId, request));

        verify(userService, never()).getUsersById(anyList());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void updateTask_shouldThrowExceptionWhenMemberNotFound() {
        // arrange
        Long taskId = 10L;
        Task existingTask = buildExistingTask(userWithId(1L));
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L, 2L));
        Project project = projectWithId(request.getProjectId());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(projectService.getProjectById(request.getProjectId()))
                .thenReturn(project);
        when(userService.getUsersById(List.of(1L, 2L)))
                .thenThrow(new ResourceNotFoundException("User not found with ids: [2]"));

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(taskId, request));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void updateTask_shouldThrowExceptionWhenSaveFails() {
        // arrange
        Long taskId = 1L;
        User oldMember = userWithId(10L);
        User newMember = userWithId(1L);
        UpdateTaskRequestDto request = buildUpdateRequest(List.of(1L));
        Task existingTask = buildExistingTask(oldMember);
        Project project = projectWithId(request.getProjectId());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userService.getUsersById(List.of(1L))).thenReturn(Set.of(newMember));
        when(projectService.getProjectById(request.getProjectId()))
                .thenReturn(project);
        when(taskRepository.save(any(Task.class)))
                .thenThrow(new RuntimeException("Database Error"));

        // act & assert
        assertThrows(RuntimeException.class,
                () -> taskService.updateTask(taskId, request));
    }

    // Delete Task Test

    @Test
    public void deleteTask_shouldDeleteTaskSuccessfully() {
        // arrange
        Long taskId = 1L;
        Task existingTask = buildExistingTask(userWithId(1L));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // act
        taskService.deleteTask(taskId);

        // assert
        verify(taskRepository).delete(any());
    }

    @Test
    public void deleteTask_shouldThrowExceptionWhenTaskNotFound() {
        // arrange
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(taskId));

        // assert
        verify(taskRepository, never()).delete(any());
    }

    // Get All Tasks Tests

    @Test
    public void getAllTasks_shouldReturnTasksSuccessfully() {
        // arrange
        Task task1 = buildExistingTask(userWithId(1L));
        task1.setProject(projectWithId(1L));
        task1.setTitle("Task 1");
        Task task2 = buildExistingTask(userWithId(2L));
        task2.setProject(projectWithId(1L));
        task2.setTitle("Task 2");

        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskRepository.findAll()).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getAllTasks();

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Task 1", response.get(0).getTitle());
        assertEquals("Task 2", response.get(1).getTitle());
    }

    @Test
    public void getAllTasks_shouldReturnEmptyTaskList() {
        // arrange
        List<Task> tasks = new ArrayList<>();
        when(taskRepository.findAll()).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getAllTasks();

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    // Get Task Test

    @Test
    public void getTask_shouldReturnTaskSuccessfully() {
        // arrange
        Long taskId = 1L;
        Task task = buildExistingTask(userWithId(1L));
        task.setProject(projectWithId(1L));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // act
        TaskResponseDto response = taskService.getTask(taskId);

        // assert
        assertNotNull(response);
        assertEquals(task.getTitle(), response.getTitle());
        assertEquals(task.getDescription(), response.getDescription());
        assertEquals(task.getAssignedTo().size(), response.getAssignedTo().size());
    }

    @Test
    public void getTask_shouldThrowExceptionWhenTaskNotFound() {
        // arrange
        Long taskId = 99L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.getTask(taskId));
    }

    // Update Status Tests

    @Test
    public void updateStatus_shouldUpdateStatusSuccessfully() {
        // arrange
        Long taskId = 1L;
        User assignedUser = userWithId(1L);
        Task task = buildExistingTask(assignedUser);
        task.setProject(projectWithId(1L));
        TaskStatus taskStatus = TaskStatus.DONE;
        User currentUser = userWithId(1L);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // act
        taskService.updateStatus(taskId, taskStatus);

        // assert
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertSame(task, saved);
        assertEquals(TaskStatus.DONE, saved.getStatus());
    }

    @Test
    public void updateStatus_shouldUpdateStatusWhenUserIsAdmin() {
        // arrange
        Long taskId = 1L;
        User assignedUser = userWithId(2L);
        Task task = buildExistingTask(assignedUser);
        task.setProject(projectWithId(1L));
        TaskStatus taskStatus = TaskStatus.DONE;
        User currentUser = userWithId(1L);
        currentUser.setRole(UserRole.ADMIN);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        // act
        taskService.updateStatus(taskId, taskStatus);

        // assert
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertSame(task, saved);
        assertEquals(TaskStatus.DONE, saved.getStatus());
    }

    @Test
    public void updateStatus_shouldThrowExceptionWhenUserNotAuthorized() {
        // arrange
        Long taskId = 1L;
        User assignedUser = userWithId(2L);
        Task task = buildExistingTask(assignedUser);
        TaskStatus taskStatus = TaskStatus.DONE;
        User currentUser = userWithId(1L);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        // act
        assertThrows(UnauthorizedException.class,
                () -> taskService.updateStatus(taskId, taskStatus));

        // assert
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void updateStatus_shouldThrowExceptionWhenUserNotAuthenticated() {
        // arrange
        Long taskId = 1L;
        User assignedUser = userWithId(2L);
        Task task = buildExistingTask(assignedUser);
        TaskStatus taskStatus = TaskStatus.DONE;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenThrow(new UnauthorizedException("User not authenticated"));

        // act
        assertThrows(UnauthorizedException.class,
                () -> taskService.updateStatus(taskId, taskStatus));

        // assert
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void updateStatus_shouldThrowExceptionWhenTaskNotFound() {
        // arrange
        Long taskId = 1L;
        TaskStatus taskStatus = TaskStatus.DONE;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateStatus(taskId, taskStatus));

        // assert
        verify(userService, never()).getCurrentUser();
        verify(taskRepository, never()).save(any(Task.class));
    }

    // Get Tasks By Project Tests

    @Test
    public void getTasksByProject_shouldReturnTasksByProjectSuccessfully() {
        // arrange
        Long projectId = 1L;
        Project project = projectWithId(projectId);

        Task task1 = buildExistingTask(userWithId(1L));
        task1.setProject(projectWithId(1L));
        task1.setTitle("Task 1");
        Task task2 = buildExistingTask(userWithId(2L));
        task2.setProject(projectWithId(2L));
        task2.setTitle("Task 2");

        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(taskRepository.findAllByProject(project)).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getTasksByProject(projectId);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Task 1", response.get(0).getTitle());
        assertEquals("Task 2", response.get(1).getTitle());
    }

    @Test
    public void getTasksByProject_shouldReturnEmptyTaskListByProject() {
        // arrange
        Long projectId = 1L;
        Project project = projectWithId(projectId);

        List<Task> tasks = new ArrayList<>();
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(taskRepository.findAllByProject(project)).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getTasksByProject(projectId);

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void getTasksByProject_shouldThrowExceptionWhenProjectNotFound() {
        // arrange
        Long projectId = 1L;

        when(projectService.getProjectById(projectId))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTasksByProject(projectId));

        verify(taskRepository, never()).findAllByProject(any());
    }

    // Get My Tasks By Year And Month

    @Test
    public void getTasksByYearAndMonth_shouldReturnTasksWhenUserIsMemberSuccessfully() {
        // arrange
        YearMonth yearMonth = YearMonth.of(2026, 9);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        User currentUser = userWithId(1L);
        currentUser.setRole(UserRole.MEMBER);
        Task task1 = buildExistingTask(currentUser);
        task1.setProject(projectWithId(1L));
        task1.setTitle("Task 1");
        Task task2 = buildExistingTask(currentUser);
        task2.setProject(projectWithId(2L));
        task2.setTitle("Task 2");
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findByDueDateBetweenAndAssignedTo_Id(eq(startDate),
                eq(endDate), eq(currentUser.getId()))).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getTaskByYearAndMonth(2026, 9);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Task 1", response.get(0).getTitle());
        assertEquals("Task 2", response.get(1).getTitle());
        assertEquals(1, response.get(0).getAssignedTo().getFirst().getId());
        assertEquals(1, response.get(1).getAssignedTo().getFirst().getId());

        verify(taskRepository, never()).findByDueDateBetween(any(), any());
    }

    @Test
    public void getTasksByYearAndMonth_shouldReturnTasksWhenUserIsAdminSuccessfully() {
        // arrange
        YearMonth yearMonth = YearMonth.of(2026, 9);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        User currentUser = userWithId(1L);
        currentUser.setRole(UserRole.ADMIN);
        Task task1 = buildExistingTask(userWithId(3L));
        task1.setProject(projectWithId(1L));
        task1.setTitle("Task 1");
        Task task2 = buildExistingTask(userWithId(4L));
        task2.setProject(projectWithId(2L));
        task2.setTitle("Task 2");
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findByDueDateBetween(eq(startDate), eq(endDate))).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getTaskByYearAndMonth(2026, 9);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Task 1", response.get(0).getTitle());
        assertEquals("Task 2", response.get(1).getTitle());
        assertEquals(3, response.get(0).getAssignedTo().getFirst().getId());
        assertEquals(4, response.get(1).getAssignedTo().getFirst().getId());

        verify(taskRepository, never()).findByDueDateBetweenAndAssignedTo_Id(any(), any(), anyLong());

    }

    @Test
    public void getTasksByYearAndMonth_shouldReturnEmptyTaskListWhenUserIsMemberSuccessfully() {
        // arrange
        YearMonth yearMonth = YearMonth.of(2026, 10);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        User currentUser = userWithId(1L);
        currentUser.setRole(UserRole.MEMBER);

        List<Task> tasks = new ArrayList<>();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findByDueDateBetweenAndAssignedTo_Id(eq(startDate),
                eq(endDate), eq(currentUser.getId()))).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getTaskByYearAndMonth(2026, 10);

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());

        verify(taskRepository, never()).findByDueDateBetween(any(), any());

    }

    @Test
    public void getTasksByYearAndMonth_shouldReturnEmptyTaskListWhenUserIsAdminSuccessfully() {
        // arrange
        YearMonth yearMonth = YearMonth.of(2026, 10);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        User currentUser = userWithId(1L);
        currentUser.setRole(UserRole.ADMIN);

        List<Task> tasks = new ArrayList<>();

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findByDueDateBetween(eq(startDate),
                eq(endDate))).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.getTaskByYearAndMonth(2026, 10);

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());


        verify(taskRepository, never()).findByDueDateBetweenAndAssignedTo_Id(any(), any(), anyLong());
    }

    // Update members tests

    @Test
    public void updateMembers_shouldUpdateMembersSuccessfully() {
        // arrange
        Long taskId = 1L;

        UpdateMemberRequestDto request = new UpdateMemberRequestDto();
        request.setAssignedTo(List.of(3L));

        User assignedUser = userWithId(2L);
        Task existingTask = buildExistingTask(assignedUser);
        existingTask.setProject(projectWithId(1L));

        when(taskRepository.findById(eq(taskId))).thenReturn(Optional.of(existingTask));
        when(userService.getUsersById(List.of(3L))).thenReturn(Set.of(userWithId(3L)));
        when(taskRepository.save(eq(existingTask))).thenAnswer(i -> i.getArgument(0));

        // act
        TaskResponseDto response = taskService.updateMembers(taskId, request);

        // assert
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();


        assertNotNull(response);
        assertSame(existingTask, saved);
        assertEquals(new HashSet<>(request.getAssignedTo()), saved.getAssignedTo().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
    }

    @Test
    public void updateMembers_shouldThrowExceptionWhenTaskNotFound() {
        // arrange
        Long taskId = 1L;

        UpdateMemberRequestDto request = new UpdateMemberRequestDto();
        request.setAssignedTo(List.of(2L));

        when(taskRepository.findById(eq(taskId))).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateMembers(taskId, request));

        verify(userService, never()).getUsersById(anyList());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    public void updateMembers_shouldThrowExceptionWhenMemberNotFound() {
        // arrange
        Long taskId = 1L;

        UpdateMemberRequestDto request = new UpdateMemberRequestDto();
        request.setAssignedTo(List.of(2L, 3L));

        User assignedUser = userWithId(2L);
        Task existingTask = buildExistingTask(assignedUser);
        existingTask.setProject(projectWithId(1L));

        when(taskRepository.findById(eq(taskId))).thenReturn(Optional.of(existingTask));
        when(userService.getUsersById(List.of(2L, 3L)))
                .thenThrow(new ResourceNotFoundException("Users not found with ids: [3]"));

        // act
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateMembers(taskId, request));

        verify(taskRepository, never()).save(any(Task.class));
    }

    // filter tasks tests

    @Test
    public void filterTasks_shouldFilterByStatusOnly() {
        // arrange
        String status = "DONE";

        Task task1 = buildExistingTask(userWithId(1L));
        task1.setId(1L);
        task1.setStatus(TaskStatus.DONE);
        Task task2 = buildExistingTask(userWithId(1L));
        task2.setId(2L);
        task2.setStatus(TaskStatus.DONE);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskRepository.findByStatus(TaskStatus.valueOf(status))).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.filterTasks(status, null);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(TaskStatus.DONE, response.get(0).getStatus());
        assertEquals(TaskStatus.DONE, response.get(1).getStatus());

        verify(taskRepository, never()).findByPriority(any(TaskPriority.class));
        verify(taskRepository, never()).findAllByOrderByCreatedAtDesc();
    }

    @Test
    public void filterTasks_shouldFilterByPriorityOnly() {
        // arrange
        String priority = "LOW";

        Task task1 = buildExistingTask(userWithId(1L));
        task1.setId(1L);
        task1.setPriority(TaskPriority.LOW);
        Task task2 = buildExistingTask(userWithId(1L));
        task2.setId(2L);
        task2.setPriority(TaskPriority.LOW);
        List<Task> projects = new ArrayList<>(List.of(task1, task2));

        when(taskRepository.findByPriority(TaskPriority.valueOf(priority))).thenReturn(projects);

        // act
        List<TaskResponseDto> response = taskService.filterTasks(null, priority);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(TaskPriority.LOW, response.get(0).getPriority());
        assertEquals(TaskPriority.LOW, response.get(1).getPriority());

        verify(taskRepository, never())
                .findByStatus(any(TaskStatus.class));
        verify(taskRepository, never()).findAllByOrderByCreatedAtDesc();
    }

    @Test
    public void filterTasks_shouldReturnAllTasksWhenNothingMatches() {
        // arrange
        Task task1 = buildExistingTask(userWithId(1L));
        task1.setId(1L);
        task1.setPriority(TaskPriority.LOW);
        task1.setStatus(TaskStatus.DONE);
        Task task2 = buildExistingTask(userWithId(1L));
        task2.setId(2L);
        task2.setPriority(TaskPriority.LOW);
        task2.setStatus(TaskStatus.DONE);
        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(taskRepository.findAllByOrderByCreatedAtDesc()).thenReturn(tasks);

        // act
        List<TaskResponseDto> response = taskService.filterTasks(null, null);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(TaskStatus.DONE, response.get(0).getStatus());
        assertEquals(TaskStatus.DONE, response.get(1).getStatus());
        assertEquals(TaskPriority.LOW, response.get(0).getPriority());
        assertEquals(TaskPriority.LOW, response.get(1).getPriority());

        verify(taskRepository, never()).findByStatus(any(TaskStatus.class));
        verify(taskRepository, never()).findByPriority(any(TaskPriority.class));
    }

    @Test
    public void filterTasks_shouldThrowExceptionWhenStatusIsNotValidEnum() {
        // arrange
        String status = "INVALID_STATUS";

        // act & assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.filterTasks(status, null));

        verify(taskRepository, never()).findAllByOrderByCreatedAtDesc();
        verify(taskRepository, never()).findByStatus(any(TaskStatus.class));
        verify(taskRepository, never()).findByPriority(any(TaskPriority.class));
    }

    @Test
    public void filterTasks_shouldThrowExceptionWhenPriorityIsNotValidEnum() {
        // arrange
        String status = "INVALID_PRIORITY";

        // act & assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.filterTasks(status, null));

        verify(taskRepository, never()).findAllByOrderByCreatedAtDesc();
        verify(taskRepository, never()).findByStatus(any(TaskStatus.class));
        verify(taskRepository, never()).findByPriority(any(TaskPriority.class));
    }

    // Get my reminders tests

    @Test
    public void getMyReminders_shouldReturnRemindersSuccessfully() {
        // arrange
        User currentUser = userWithId(1L);
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(1);

        Task task1 = buildExistingTask(userWithId(1L));
        task1.setId(1L);
        Task task2 = buildExistingTask(userWithId(1L));
        task2.setId(2L);

        List<Task> tasks = new ArrayList<>(List.of(task1, task2));

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findReminderTasks(currentUser.getId(), TaskStatus.DONE, limit)).thenReturn(tasks);

        // act
        List<ReminderResponseDto> response = taskService.getMyReminders();

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    public void getMyReminders_shouldReturnReminderEmptyListSuccessfully() {
        // arrange
        User currentUser = userWithId(1L);
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(1);

        List<Task> tasks = new ArrayList<>(List.of());

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(taskRepository.findReminderTasks(currentUser.getId(), TaskStatus.DONE, limit)).thenReturn(tasks);

        // act
        List<ReminderResponseDto> response = taskService.getMyReminders();

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void getMyReminders_shouldThrowExceptionWhenUserIsNotAuthenticated() {
        when(userService.getCurrentUser()).thenThrow(new UnauthorizedException("User is not authenticated"));

        // act & assert
        assertThrows(UnauthorizedException.class, () -> taskService.getMyReminders());

        verify(taskRepository, never()).findReminderTasks(anyLong(), any(TaskStatus.class), any());
    }


}
