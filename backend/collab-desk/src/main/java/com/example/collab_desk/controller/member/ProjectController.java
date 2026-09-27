package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectHandler(@PathVariable Long id) {
        ProjectResponseDto responseDto = projectService.getProject(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjectsHandler() {
        List<ProjectResponseDto> response = projectService.getAllProjects();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponseDto>> searchProjectsHandler(@RequestParam String keyword) {
        List<ProjectResponseDto> response = projectService.searchProjects(keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProjectResponseDto>> filterProjectsHandler(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        List<ProjectResponseDto> response = projectService.filterProjects(status, priority);
        return ResponseEntity.ok(response);
    }
}
