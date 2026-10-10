package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.UpdateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.CreateTaskRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.dto.responseDto.ReminderResponseDto;
import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.enums.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(CreateTaskRequestDto request);

    TaskResponseDto getTask(Long id);

    TaskResponseDto updateTask(Long id, UpdateTaskRequestDto request);

    void deleteTask(Long id);

    List<TaskResponseDto> getAllTasks();

    Task getTaskById(Long id);

    TaskResponseDto updateStatus(Long taskId, TaskStatus status);

    List<TaskResponseDto> getTasksByProject(Long projectId);

    List<TaskResponseDto> getTaskByYearAndMonth(int year, int month);

    List<TaskResponseDto> getMyTasks();

    TaskResponseDto updateMembers(Long taskId, UpdateMemberRequestDto request);

    List<TaskResponseDto> filterTasks(String status, String priority);

    List<ReminderResponseDto> getMyReminders();
}
