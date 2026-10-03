package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.CreateTaskRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.TaskStatus;
import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.ProjectRepository;
import com.example.collab_desk.repository.TaskRepository;
import com.example.collab_desk.service.ProjectService;
import com.example.collab_desk.service.TaskService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;

    @Override
    @Transactional
    public TaskResponseDto createTask(CreateTaskRequestDto request) {
        Project project = projectService.getProjectById(request.getProjectId());
        Set<User> users = userService.getUsersById(request.getAssignedTo());
        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getCategory(),
                request.getDueDate(),
                request.getEstimatedTime()
        );

        task.setProject(project);
        task.setAssignedTo(users);
        return mapToTaskResponseDto(taskRepository.save(task));
    }

    @Override
    public TaskResponseDto getTask(Long id) {
        return mapToTaskResponseDto(getTaskById(id));
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, UpdateTaskRequestDto request) {
        Task existingTask = getTaskById(id);
        Project project = projectService.getProjectById(request.getProjectId());
        Set<User> users = userService.getUsersById(request.getAssignedTo());

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setProject(project);
        existingTask.setStatus(request.getStatus());
        existingTask.setCategory(request.getCategory());
        existingTask.setPriority(request.getPriority());
        existingTask.setDueDate(request.getDueDate());
        existingTask.setEstimatedTime(request.getEstimatedTime());
        existingTask.setAssignedTo(users);

        return mapToTaskResponseDto(taskRepository.save(existingTask));
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task existingTask = getTaskById(id);
        taskRepository.delete(existingTask);
    }

    @Override
    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToTaskResponseDto)
                .toList();
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @Override
    public TaskResponseDto updateStatus(Long taskId, TaskStatus status) {
        Task existingTask = getTaskById(taskId);
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN ||
                existingTask
                        .getAssignedTo()
                        .stream()
                        .anyMatch(u -> Objects.equals(u.getId(), currentUser.getId()))) {
            existingTask.setStatus(status);
            return mapToTaskResponseDto(taskRepository.save(existingTask));
        }

        throw new UnauthorizedException("You are not authorized to update status");
    }

    @Override
    public List<TaskResponseDto> getTasksByProject(Long projectId) {
        Project project = projectService.getProjectById(projectId);

        return taskRepository.findAllByProject(project)
                .stream()
                .map(this::mapToTaskResponseDto)
                .toList();
    }

    @Override
    public List<TaskResponseDto> getTaskByYearAndMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return taskRepository.findByDueDateBetween(startDate, endDate)
                .stream().map(this::mapToTaskResponseDto).toList();
    }

    @Override
    public List<TaskResponseDto> getMyTaskByYearAndMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        User currentUser = userService.getCurrentUser();
        return taskRepository.findByDueDateBetweenAndAssignedTo_Id(startDate, endDate, currentUser.getId())
                .stream().map(this::mapToTaskResponseDto).toList();
    }

    @Override
    public List<TaskResponseDto> getMyTasks() {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByAssignedTo_id(currentUser.getId())
                .stream().map(this::mapToTaskResponseDto).toList();
    }

    private TaskResponseDto mapToTaskResponseDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCategory(),
                task.getPriority(),
                task.getDueDate(),
                task.getEstimatedTime(),
                task.getAssignedTo().stream().map(this::mapToUserResponse).toList(),
                task.getCreatedAt(),
                task.getProject().getTitle()
        );
    }

    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }
}
