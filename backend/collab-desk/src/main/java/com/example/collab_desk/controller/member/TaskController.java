package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.responseDto.TaskResponseDto;
import com.example.collab_desk.enums.TaskStatus;
import com.example.collab_desk.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateStatusHandler(
            @PathVariable Long id,
            @RequestParam TaskStatus status
    ) {
        TaskResponseDto response = taskService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponseDto>> getAllTasksByProjectHandler(@PathVariable Long projectId) {
        List<TaskResponseDto> response = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calender/my")
    public ResponseEntity<List<TaskResponseDto>> getMyTasksByYearAndMonthHandler(
            @RequestParam int year, @RequestParam int month
    ) {
        List<TaskResponseDto> response = taskService.getMyTaskByYearAndMonth(year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponseDto>> getMyTasksHandler() {
        List<TaskResponseDto> response = taskService.getMyTasks();
        return ResponseEntity.ok(response);
    }
}
