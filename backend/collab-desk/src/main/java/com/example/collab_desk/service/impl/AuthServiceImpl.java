package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.LoginRequestDto;
import com.example.collab_desk.dto.requestDto.PasswordSetupRequestDto;
import com.example.collab_desk.dto.requestDto.RegisterRequestDto;
import com.example.collab_desk.dto.responseDto.AuthResponseDto;
import com.example.collab_desk.dto.responseDto.PasswordSetupResponseDto;
import com.example.collab_desk.entity.PasswordSetupToken;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.UserStatus;
import com.example.collab_desk.exception.InvalidTokenException;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.repository.PasswordSetupTokenRepository;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.AuthService;
import com.example.collab_desk.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordSetupTokenRepository passwordSetupTokenRepository;


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


    @Override
    public PasswordSetupResponseDto setPassword(PasswordSetupRequestDto request) {
        PasswordSetupToken token = passwordSetupTokenRepository.findByTokenHash(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        if (token.getIsUsed() || token.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token is expired or already used");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        token.setIsUsed(true);
        passwordSetupTokenRepository.save(token);

        return new PasswordSetupResponseDto("Password set successfully");
    }

}
