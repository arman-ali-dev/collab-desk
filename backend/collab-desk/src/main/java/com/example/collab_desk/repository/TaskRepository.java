package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProject(Project project);

    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    List<Task> findByDueDateBetweenAndAssignedTo_Id(LocalDate startDate, LocalDate endDate, Long userId);

    List<Task> findByAssignedTo_id(Long userId);

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findAllByOrderByCreatedAtDesc();
}
