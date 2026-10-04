package com.example.collab_desk.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_setup_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordSetupToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Boolean isUsed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expireTime;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
