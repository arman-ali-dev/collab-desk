package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.LoginRequestDto;
import com.example.collab_desk.dto.requestDto.PasswordSetupRequestDto;
import com.example.collab_desk.dto.requestDto.RegisterRequestDto;
import com.example.collab_desk.dto.responseDto.AuthResponseDto;
import com.example.collab_desk.dto.responseDto.PasswordSetupResponseDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    PasswordSetupResponseDto setPassword(PasswordSetupRequestDto request);
}
