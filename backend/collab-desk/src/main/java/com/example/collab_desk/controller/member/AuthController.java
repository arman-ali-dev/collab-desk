package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.requestDto.LoginRequestDto;
import com.example.collab_desk.dto.requestDto.PasswordSetupRequestDto;
import com.example.collab_desk.dto.requestDto.RegisterRequestDto;
import com.example.collab_desk.dto.responseDto.AuthResponseDto;
import com.example.collab_desk.dto.responseDto.PasswordSetupResponseDto;
import com.example.collab_desk.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registrationHandler(@Valid @RequestBody RegisterRequestDto request) {
        AuthResponseDto response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginHandler(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/set-password")
    public ResponseEntity<PasswordSetupResponseDto> setPasswordHandler(
            @Valid @RequestBody PasswordSetupRequestDto request) {
        PasswordSetupResponseDto response = authService.setPassword(request);
        return ResponseEntity.ok(response);
    }
}
