package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.EditProfileRequestDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User getUser(Long id);

    User getUserByEmail(String email);

    Set<User> getUsersById(List<Long> ids);

    User getCurrentUser();

    List<UserProfileResponseDto> getAllUsers();

    List<UserResponseDto> searchUsers(String fullName, String email);

    UserProfileResponseDto getProfile();

    UserProfileResponseDto editProfile(EditProfileRequestDto request);

    
}
