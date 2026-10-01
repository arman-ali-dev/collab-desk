package com.example.collab_desk.controller.admin;

import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsersHandler() {
        List<UserResponseDto> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsersHandler(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email
    ) {
        System.out.println(fullName + " "  + email);
        List<UserResponseDto> response = userService.searchUsers(fullName, email);
        System.out.println(response.size());
        return ResponseEntity.ok(response);
    }
}
