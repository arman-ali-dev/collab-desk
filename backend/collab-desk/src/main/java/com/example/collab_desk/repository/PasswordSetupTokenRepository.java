package com.example.collab_desk.repository;

import com.example.collab_desk.entity.PasswordSetupToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordSetupTokenRepository extends JpaRepository<PasswordSetupToken, Long> {
}
