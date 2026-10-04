package com.example.collab_desk.service.impl;

import com.example.collab_desk.dto.requestDto.CreateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.EditProfileRequestDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.PasswordSetupToken;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.UserStatus;
import com.example.collab_desk.exception.DuplicateResourceException;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.PasswordSetupTokenRepository;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.EmailService;
import com.example.collab_desk.service.UserService;
import com.example.collab_desk.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordSetupTokenRepository passwordSetupTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

    @Override
    public Set<User> getUsersById(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.size() != ids.size()) {
            List<Long> foundIds = users
                    .stream()
                    .map(User::getId)
                    .toList();

            Set<Long> missingIds = new TreeSet<>(ids);
            missingIds.removeAll(foundIds);

            throw new ResourceNotFoundException("Users not found with ids: " + missingIds);
        }

        return new HashSet<>(users);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = authentication.getName();
        return getUserByEmail(email);
    }

    @Override
    public List<UserProfileResponseDto> getAllUsers() {
        return userRepository.findAllByDeletedFalseOrderByCreatedAtDesc()
                .stream().map(this::mapToUserProfileResponse).toList();
    }

    @Override
    public List<UserResponseDto> searchUsers(String fullName, String email) {
        return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(fullName, email)
                .stream().map(this::mapToUserResponse).toList();
    }

    @Override
    public UserProfileResponseDto getProfile() {
        return mapToUserProfileResponse(getCurrentUser());
    }

    @Override
    public UserProfileResponseDto editProfile(EditProfileRequestDto request) {
        User currentUser = getCurrentUser();
        currentUser.setFullName(request.getFullName());
        currentUser.setEmail(request.getEmail());
        currentUser.setProfileImage(request.getProfileImage());
        currentUser.setDesignation(request.getDesignation());

        return mapToUserProfileResponse(userRepository.save(currentUser));
    }

    @Override
    public UserProfileResponseDto createMember(CreateMemberRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User is already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setDesignation(request.getDesignation());
        user.setRole(request.getRole());
        user.setStatus(UserStatus.INACTIVE);

        User savedUser = userRepository.save(user);

        PasswordSetupToken passwordSetupToken = new PasswordSetupToken();

        String hashedToken = TokenUtil.generateToken();
        passwordSetupToken.setTokenHash(hashedToken);
        passwordSetupToken.setUser(savedUser);
        passwordSetupToken.setIsUsed(false);
        passwordSetupToken.setExpireTime(LocalDateTime.now().plusHours(24));

        passwordSetupTokenRepository.save(passwordSetupToken);

        String link = frontendUrl + "/set-password?token=" + hashedToken;
        String email = user.getEmail();
        String fullName = user.getFullName();

        emailService.sendInvitation(email, fullName, link);

        return mapToUserProfileResponse(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User existingUser = getUser(id);
        existingUser.setDeleted(true);
        userRepository.save(existingUser);
    }

    @Override
    public List<UserProfileResponseDto> filterUsers(String status) {
        List<User> users;

        if (status != null) {
            users = userRepository.findByStatusAndDeletedFalse(UserStatus.valueOf(status));
        } else {
            users = userRepository.findAllByDeletedFalseOrderByCreatedAtDesc();
        }
        return users.stream().map(this::mapToUserProfileResponse).toList();
    }

    private UserResponseDto mapToUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getProfileImage()
        );
    }

    private UserProfileResponseDto mapToUserProfileResponse(User user) {
        return new UserProfileResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getDesignation(),
                user.getRole(),
                user.getProfileImage(),
                user.getStatus()
        );
    }
}
