package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Project;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByTitleContainingIgnoreCase(String title);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByPriority(ProjectPriority priority);

    List<Project> findByStatusAndPriority(ProjectStatus status, ProjectPriority priority);
}
