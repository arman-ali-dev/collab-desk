package com.example.collab_desk.service;

import com.example.collab_desk.dto.requestDto.CreateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.EditProfileRequestDto;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.entity.PasswordSetupToken;
import com.example.collab_desk.entity.Project;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.enums.UserStatus;
import com.example.collab_desk.exception.DuplicateResourceException;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.exception.UnauthorizedException;
import com.example.collab_desk.repository.PasswordSetupTokenRepository;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordSetupTokenRepository passwordSetupTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User userWithId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    // get user tests

    @Test
    public void getUser_shouldReturnUserSuccessfully() {
        // arrange
        Long userId = 1L;
        User user = userWithId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // act
        User response = userService.getUser(userId);

        // assert
        assertNotNull(response);
        assertEquals(userId, response.getId());
    }

    @Test
    public void getUser_shouldThrowExceptionWhenUserNotFound() {
        // arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(userId));
    }

    // get user by email tests

    @Test
    public void getUserByEmail_shouldReturnUserSuccessfully() {
        // arrange
        String email = "test@gmail.com";

        User user = userWithId(1L);
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // act
        User response = userService.getUserByEmail(email);

        // assert
        assertNotNull(response);
        assertEquals(email, response.getEmail());
    }

    @Test
    public void getUserByEmail_shouldThrowExceptionWhenUserNotFound() {
        // arrange
        String email = "test@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    // get all users tests

    @Test
    public void getAllUsers_shouldReturnUsersList() {
        // arrange
        User user1 = userWithId(1L);
        User user2 = userWithId(2L);

        List<User> users = List.of(user1, user2);

        when(userRepository.findAllByDeletedFalseOrderByCreatedAtDesc()).thenReturn(users);

        // act
        List<UserProfileResponseDto> response = userService.getAllUsers();

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.getFirst().getId());
        assertEquals(2L, response.get(1).getId());
    }


    @Test
    public void getAllUsers_shouldReturnUsersEmptyList() {
        // arrange
        List<User> users = List.of();

        when(userRepository.findAllByDeletedFalseOrderByCreatedAtDesc()).thenReturn(users);

        // act
        List<UserProfileResponseDto> response = userService.getAllUsers();

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }


    // search users

    @Test
    public void searchUsers_shouldReturnUsersList() {
        // arrange
        String fullName = "test name";
        String email = "test@gmail.com";

        User user1 = userWithId(1L);
        user1.setFullName("test name");
        user1.setEmail("test@gmail.com");

        List<User> users = List.of(user1);

        when(userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(fullName, email))
                .thenReturn(users);

        // act
        List<UserResponseDto> response = userService.searchUsers(fullName, email);

        // assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("test name", response.getFirst().getFullName());
        assertEquals("test@gmail.com", response.getFirst().getEmail());
    }


    @Test
    public void searchUsers_shouldReturnUserEmptyListWhenFullNameAndEmailNotMatch() {
        // arrange
        String fullName = "test name";
        String email = "test@gmail.com";

        List<User> users = List.of();

        when(userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(fullName, email))
                .thenReturn(users);

        // act
        List<UserResponseDto> response = userService.searchUsers(fullName, email);

        // assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    // get profile tests

    @Test
    public void getProfile_shouldReturnUserProfileSuccessfully() {
        // arrange
        User currentUser = userWithId(1L);
        currentUser.setEmail("test@gmail.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(currentUser));

        // act
        UserProfileResponseDto response = userService.getProfile();

        // assert
        assertNotNull(response);
    }

    @Test
    public void getProfile_shouldThrowExceptionWhenUserIsNotAuthenticated() {
        // arrange
        User currentUser = userWithId(1L);
        currentUser.setEmail("test@gmail.com");

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // act & assert
        assertThrows(UnauthorizedException.class, () -> userService.getProfile());

        verify(authentication, never()).getName();
        verify(userRepository, never()).findByEmail(anyString());
    }

    // edit profile tests

    @Test
    public void editProfile_shouldEditProfileSuccessfully() {
        // arrange
        EditProfileRequestDto request = new EditProfileRequestDto();
        request.setFullName("New name");
        request.setEmail("newemail@gmail.com");
        request.setProfileImage("https://api.com/new-profile.png");
        request.setDesignation("New Designation");

        User currentUser = userWithId(1L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@gmail.com");
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenAnswer(i -> i.getArgument(0));

        // act
        UserProfileResponseDto response = userService.editProfile(request);

        // assert
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertNotNull(response);

        assertSame(1L, saved.getId());
        assertEquals("New name", saved.getFullName());
        assertEquals("newemail@gmail.com", saved.getEmail());
        assertEquals("https://api.com/new-profile.png", saved.getProfileImage());
        assertEquals("New Designation", saved.getDesignation());
    }

    @Test
    public void editProfile_shouldThrowExceptionWhenUserIsNotAuthenticated() {
        // arrange
        EditProfileRequestDto request = new EditProfileRequestDto();
        request.setFullName("New name");
        request.setEmail("newemail@gmail.com");
        request.setProfileImage("https://api.com/new-profile.png");
        request.setDesignation("New Designation");

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // act
        assertThrows(UnauthorizedException.class, () -> userService.editProfile(request));

        verify(authentication, never()).getName();
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // Create member tests

    @Test
    public void createMember_shouldCreateMemberSuccessfully() {
        // arrange
        CreateMemberRequestDto request = new CreateMemberRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setDesignation("test designation");
        request.setRole(UserRole.MEMBER);

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // act
        UserProfileResponseDto response = userService.createMember(request);

        assertNotNull(response);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("test name", savedUser.getFullName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals("test designation", savedUser.getDesignation());
        assertEquals(UserRole.MEMBER, savedUser.getRole());
        assertEquals(UserStatus.INACTIVE, savedUser.getStatus());

        ArgumentCaptor<PasswordSetupToken> tokenCaptor = ArgumentCaptor.forClass(PasswordSetupToken.class);
        verify(passwordSetupTokenRepository).save(tokenCaptor.capture());
        PasswordSetupToken savedToken = tokenCaptor.getValue();

        assertEquals(savedUser, savedToken.getUser());
        assertNotNull(savedToken.getTokenHash());
        assertFalse(savedToken.getIsUsed());
        assertTrue(savedToken.getExpireTime().isAfter(LocalDateTime.now().plusHours(23)));

        String link = "http://localhost:5173/set-password?token=" + savedToken.getTokenHash();
        verify(emailService).sendInvitation("test@gmail.com", "test name", link);
    }

    @Test
    public void createMember_shouldCreateAdminSuccessfully() {
        // arrange
        CreateMemberRequestDto request = new CreateMemberRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setDesignation("test designation");
        request.setRole(UserRole.ADMIN);

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // act
        UserProfileResponseDto response = userService.createMember(request);

        assertNotNull(response);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("test name", savedUser.getFullName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals("test designation", savedUser.getDesignation());
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertEquals(UserStatus.INACTIVE, savedUser.getStatus());

        ArgumentCaptor<PasswordSetupToken> tokenCaptor = ArgumentCaptor.forClass(PasswordSetupToken.class);
        verify(passwordSetupTokenRepository).save(tokenCaptor.capture());
        PasswordSetupToken savedToken = tokenCaptor.getValue();

        assertEquals(savedUser, savedToken.getUser());
        assertNotNull(savedToken.getTokenHash());
        assertFalse(savedToken.getIsUsed());
        assertTrue(savedToken.getExpireTime().isAfter(LocalDateTime.now().plusHours(23)));

        String link = "http://localhost:5173/set-password?token=" + savedToken.getTokenHash();
        verify(emailService).sendInvitation("test@gmail.com", "test name", link);
    }

    @Test
    public void createMember_shouldThrowExceptionWhenUserIsAlreadyExists() {
        // arrange
        CreateMemberRequestDto request = new CreateMemberRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setDesignation("test designation");
        request.setRole(UserRole.MEMBER);

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        // act
        assertThrows(DuplicateResourceException.class, () -> userService.createMember(request));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordSetupTokenRepository, never()).save(any(PasswordSetupToken.class));
        verify(emailService, never()).sendInvitation(anyString(), anyString(), anyString());
    }

    @Test
    public void createMember_shouldNotSendEmailWhenUserSaveFails() {
        // arrange
        CreateMemberRequestDto request = new CreateMemberRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setDesignation("test designation");
        request.setRole(UserRole.MEMBER);

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // act & assert
        assertThrows(RuntimeException.class, () -> userService.createMember(request));

        verify(passwordSetupTokenRepository, never()).save(any(PasswordSetupToken.class));
        verify(emailService, never()).sendInvitation(anyString(), anyString(), anyString());
    }

    @Test
    public void createMember_shouldNotSendEmailWhenTokenSaveFails() {
        // arrange
        CreateMemberRequestDto request = new CreateMemberRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setDesignation("test designation");
        request.setRole(UserRole.MEMBER);

        when(passwordSetupTokenRepository.save(any(PasswordSetupToken.class)))
                .thenThrow(new RuntimeException("Database error"));

        // act & assert
        assertThrows(RuntimeException.class, () -> userService.createMember(request));

        verify(emailService, never()).sendInvitation(anyString(), anyString(), anyString());
    }

    // delete user tests

    @Test
    public void deleteUser_shouldDeleteUserSuccessfully() {
        /// arrange
        Long userId = 1L;
        User user = userWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // act
        userService.deleteUser(userId);

        // assert
        assertTrue(user.getDeleted());
        verify(userRepository).save(user);
    }

    @Test
    public void deleteUser_shouldDeleteUserNotFound() {
        /// arrange
        Long userId = 1L;
        User user = userWithId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));

        // assert
        verify(userRepository, never()).save(user);
    }

    // filter users
    @Test
    public void filterUsers_shouldFilterByStatusOnly() {
        // arrange
        String status = "INACTIVE";

        User user1 = userWithId(1L);
        user1.setStatus(UserStatus.ACTIVE);

        User user2 = userWithId(2L);
        user2.setStatus(UserStatus.ACTIVE);

        List<User> users = new ArrayList<>(List.of(user1, user2));

        when(userRepository.findByStatusAndDeletedFalse(UserStatus.valueOf(status)))
                .thenReturn(users);

        // act
        List<UserProfileResponseDto> response = userService.filterUsers(status);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(UserStatus.ACTIVE, response.get(0).getStatus());
        assertEquals(UserStatus.ACTIVE, response.get(1).getStatus());

        verify(userRepository, never()).findAllByDeletedFalseOrderByCreatedAtDesc();
    }

    @Test
    public void filterUsers_shouldReturnAllUsersWhenNothingMatches() {
        // arrange
        User user1 = userWithId(1L);
        user1.setStatus(UserStatus.ACTIVE);

        User user2 = userWithId(2L);
        user2.setStatus(UserStatus.INACTIVE);

        List<User> users = new ArrayList<>(List.of(user1, user2));

        when(userRepository.findAllByDeletedFalseOrderByCreatedAtDesc())
                .thenReturn(users);

        // act
        List<UserProfileResponseDto> response = userService.filterUsers(null);

        // assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());
        assertEquals(UserStatus.ACTIVE, response.get(0).getStatus());
        assertEquals(UserStatus.INACTIVE, response.get(1).getStatus());

        verify(userRepository, never()).findByStatusAndDeletedFalse(any(UserStatus.class));
    }

    @Test
    public void filterUsers_shouldThrowExceptionWhenStatusIsNotValidEnum() {
        // arrange
        String status = "INVALID_STATUS";

        // act & assert
        assertThrows(IllegalArgumentException.class, () -> userService.filterUsers(status));

        verify(userRepository, never()).findByStatusAndDeletedFalse(any(UserStatus.class));
        verify(userRepository, never()).findAllByDeletedFalseOrderByCreatedAtDesc();
    }
}
