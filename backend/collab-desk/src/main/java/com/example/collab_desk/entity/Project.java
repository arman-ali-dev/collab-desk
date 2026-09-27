package com.example.collab_desk.entity;

import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ProjectPriority priority;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(nullable = false)
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double progress = 0.0;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_member",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<User> members = new HashSet<>();

    @Column(nullable = false, length = 500)
    private String logo;

    @Column(nullable = false, length = 150)
    private String organizationName;

    @Column(length = 500)
    private String url;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Project(
            String title,
            String description,
            ProjectPriority priority,
            ProjectStatus status,
            Double progress,
            String logo,
            String organizationName
    ) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.progress = progress;
        this.logo = logo;
        this.organizationName = organizationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Project.class.hashCode();
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }
}
