package com.example.collab_desk.repository;

import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.Task;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
                select distinct t from Task t
                join t.assignedTo u
                join fetch t.project
                where u.id = :userId
                  and t.status <> :doneStatus
                  and t.dueDate <= :limit
            """)
    List<Task> findReminderTasks(@Param("userId") Long userId,
                                 @Param("doneStatus") TaskStatus doneStatus,
                                 @Param("limit") LocalDate limit);
}
