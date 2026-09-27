package com.example.collab_desk.entity;

import com.example.collab_desk.enums.TaskCategory;
import com.example.collab_desk.enums.TaskPriority;
import com.example.collab_desk.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private TaskCategory category;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Long estimatedTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "user_tasks",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedTo = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private Set<Comment> comments = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Task(
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            TaskCategory category,
            LocalDate dueDate,
            Long estimatedTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.dueDate = dueDate;
        this.estimatedTime = estimatedTime;
    }

    public void addAssignee(User user) {
        assignedTo.add(user);
    }

    public void removeAssignee(User user) {
        assignedTo.remove(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Task.class.hashCode();
    }
}