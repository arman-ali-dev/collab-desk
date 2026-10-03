package com.example.collab_desk.controller.admin;

import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.requestDto.UpdateProjectRequest;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProjectHandler(
            @Valid  @RequestBody CreateProjectRequest request) {
        ProjectResponseDto responseDto = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectHandler(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProjectHandler(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        System.out.println("hello " + id);
        ProjectResponseDto response = projectService.updateProject(id, request);
        System.out.println(response.getTitle());
        return ResponseEntity.ok(response);
    }
}
