package com.example.collab_desk.service;

import com.example.collab_desk.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(Authentication authentication);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails user);
}
