package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.UpdateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.CreateTaskRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.ReminderResponseDto;
import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.*;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.TaskRepository;
import com.example.collab_desk.service.NotificationService;
import com.example.collab_desk.service.ProjectService;
import com.example.collab_desk.service.TaskService;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final NotificationService notificationService;

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

        String title = "New task assigned in " + project.getTitle();
        String message = "You have been assigned a new task in the project " + project.getTitle() + ".";

        for (User member : task.getAssignedTo()) {
            notificationService.notify(member, NotificationType.TASK, title, message);
        }

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

        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole().equals(UserRole.ADMIN)) {
            return taskRepository.findByDueDateBetween(startDate, endDate)
                    .stream().map(this::mapToTaskResponseDto).toList();
        } else {
            return taskRepository.findByDueDateBetweenAndAssignedTo_Id(startDate, endDate, currentUser.getId())
                    .stream().map(this::mapToTaskResponseDto).toList();
        }
    }

    @Override
    public List<TaskResponseDto> getMyTasks() {
        User currentUser = userService.getCurrentUser();
        return taskRepository.findByAssignedTo_id(currentUser.getId())
                .stream().map(this::mapToTaskResponseDto).toList();
    }

    @Override
    public TaskResponseDto updateMembers(Long taskId, UpdateMemberRequestDto request) {
        Task task = getTaskById(taskId);
        Set<User> newMembers = userService.getUsersById(request.getAssignedTo());
        task.setAssignedTo(newMembers);

        return mapToTaskResponseDto(taskRepository.save(task));
    }

    @Override
    public List<TaskResponseDto> filterTasks(String status, String priority) {
        List<Task> tasks;

        if (status != null) {
            tasks = taskRepository.findByStatus(TaskStatus.valueOf(status));
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(TaskPriority.valueOf(priority));
        } else {
            tasks = taskRepository.findAllByOrderByCreatedAtDesc();
        }

        return tasks.stream().
                map(this::mapToTaskResponseDto).
                toList();
    }

    @Override
    public List<ReminderResponseDto> getMyReminders() {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(1);

        return taskRepository.findReminderTasks(user.getId(), TaskStatus.DONE, limit)
                .stream()
                .map((t) -> toReminder(t, today))
                .sorted(Comparator.comparing(ReminderResponseDto::getLevel)
                        .thenComparing(ReminderResponseDto::getDueDate)).toList();
    }

    public ReminderResponseDto toReminder(Task t, LocalDate today) {
        long days = ChronoUnit.DAYS.between(today, t.getDueDate());

        ReminderLevel level = null;
        String message = "";
        if (days < 0) {
            level = ReminderLevel.OVERDUE;
            message = "Overdue by " + (-days) + (days == -1 ? " day" : " days");
        } else if (days == 0) {
            level = ReminderLevel.TODAY;
            message = "Due today";
        } else if (days == 1) {
            level = ReminderLevel.TOMORROW;
            message = "Due tomorrow";
        }

        return new ReminderResponseDto(t.getId(),
                t.getTitle(), message, t.getProject().getTitle(), level, t.getDueDate());
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
                user.getEmail(),
                user.getProfileImage()
        );
    }
}
