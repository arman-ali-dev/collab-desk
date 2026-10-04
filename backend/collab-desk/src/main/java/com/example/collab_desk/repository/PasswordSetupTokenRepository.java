package com.example.collab_desk.repository;

import com.example.collab_desk.entity.PasswordSetupToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordSetupTokenRepository extends JpaRepository<PasswordSetupToken, Long> {
    Optional<PasswordSetupToken> findByTokenHash(String token);
}
