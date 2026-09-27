package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.LoginRequestDto;
import com.example.collab_desk.dto.requestDto.RegisterRequestDto;
import com.example.collab_desk.dto.responseDto.AuthResponseDto;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.UserStatus;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.AuthService;
import com.example.collab_desk.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        User user = new User(
                request.getFullName(),
                request.getEmail(),
                request.getDesignation(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole(),
                UserStatus.ACTIVE);

        userRepository.save(user);

        return new AuthResponseDto("null", "Registration Successfully");
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                request.getEmail(),
                request.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationRequest);
        return new AuthResponseDto(jwtService.generateToken(authentication), "Login successfully");
    }
}
