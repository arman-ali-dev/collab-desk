package com.example.collab_desk.controller.admin;

import com.example.collab_desk.dto.requestDto.CreateTaskRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.UpdateTaskRequestDto;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTaskHandler(@Valid @RequestBody CreateTaskRequestDto request) {
        TaskResponseDto response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTaskHandler(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequestDto request) {
        TaskResponseDto response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskHandler(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksHandler() {
        List<TaskResponseDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/calender")
    public ResponseEntity<List<TaskResponseDto>> getTasksByYearAndMonthHandler(@RequestParam int year, @RequestParam int month) {
        List<TaskResponseDto> response = taskService.getTaskByYearAndMonth(year, month);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/members/update/{id}")
    public ResponseEntity<TaskResponseDto> updateMembersHandler(
            @PathVariable Long id, @Valid @RequestBody UpdateMemberRequestDto request) {
        TaskResponseDto response = taskService.updateMembers(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TaskResponseDto>> filterProjectsHandler(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        List<TaskResponseDto> response = taskService.filterTasks(status, priority);
        return ResponseEntity.ok(response);
    }
}
