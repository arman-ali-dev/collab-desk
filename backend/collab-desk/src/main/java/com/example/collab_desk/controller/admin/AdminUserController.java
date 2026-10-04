package com.example.collab_desk.controller.admin;

import com.example.collab_desk.dto.requestDto.CreateMemberRequestDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponseDto>> getAllUsersHandler() {
        List<UserProfileResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsersHandler(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email
    ) {
        List<UserResponseDto> response = userService.searchUsers(fullName, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<UserProfileResponseDto> createMemberHandler(
            @Valid @RequestBody CreateMemberRequestDto request) {
        UserProfileResponseDto response = userService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberHandler(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserProfileResponseDto>> filterUsersHandler(
            @RequestParam(required = false) String status) {
        List<UserProfileResponseDto> response = userService.filterUsers(status);
        return ResponseEntity.ok(response);
    }

}
