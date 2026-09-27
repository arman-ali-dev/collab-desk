package com.example.collab_desk.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateToken(Authentication authentication);
}
