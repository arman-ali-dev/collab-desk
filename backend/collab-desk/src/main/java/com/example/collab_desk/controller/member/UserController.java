package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.requestDto.EditProfileRequestDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getProfileHandler() {
        UserProfileResponseDto response = userService.getProfile();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/edit")
    public ResponseEntity<UserProfileResponseDto> editProfileHandler(
            @Valid @RequestBody EditProfileRequestDto request) {
        UserProfileResponseDto response = userService.editProfile(request);
        return ResponseEntity.ok(response);
    }
}
