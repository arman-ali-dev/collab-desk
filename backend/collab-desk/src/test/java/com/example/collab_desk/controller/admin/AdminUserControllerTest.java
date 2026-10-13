package com.example.collab_desk.controller.admin;

import com.example.collab_desk.config.SecurityConfig;
import com.example.collab_desk.dto.requestDto.CreateMemberRequestDto;
import com.example.collab_desk.dto.requestDto.CreateProjectRequest;
import com.example.collab_desk.dto.responseDto.ProjectResponseDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.dto.responseDto.UserResponseDto;
import com.example.collab_desk.enums.ProjectPriority;
import com.example.collab_desk.enums.ProjectStatus;
import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.enums.UserStatus;
import com.example.collab_desk.exception.DuplicateResourceException;
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.service.UserService;
import com.example.collab_desk.service.impl.CustomUserDetailsService;
import com.example.collab_desk.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import(SecurityConfig.class)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private CreateMemberRequestDto buildCreateRequest() {
        CreateMemberRequestDto request = new CreateMemberRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setRole(UserRole.MEMBER);
        request.setDesignation("test designation");
        return request;
    }

    private UserProfileResponseDto buildProfileResponse(Long id) {
        UserProfileResponseDto response = new UserProfileResponseDto();
        response.setId(id);
        response.setFullName("Test name");
        response.setEmail("test@gmail.com");
        response.setDesignation("Test Designation");
        response.setRole(UserRole.MEMBER);
        response.setProfileImage("https://api.com/profile.png");
        response.setStatus(UserStatus.ACTIVE);
        return response;
    }

    private UserResponseDto buildUserResponse(Long id) {
        UserResponseDto response = new UserResponseDto();
        response.setId(id);
        response.setFullName("Test name");
        response.setEmail("test@gmail.com");
        response.setProfileImage("https://api.com/profile.png");
        return response;
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getAllUsers_shouldReturn200WithAllUsers() throws Exception {
        // arrange
        UserProfileResponseDto user1 = buildProfileResponse(1L);
        user1.setFullName("User 1");
        UserProfileResponseDto user2 = buildProfileResponse(2L);
        user2.setFullName("User 2");
        List<UserProfileResponseDto> users = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName").value("User 1"))
                .andExpect(jsonPath("$[1].fullName").value("User 2"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getAllUsers_shouldReturn200WithEmptyUserList() throws Exception {
        // arrange
        when(userService.getAllUsers()).thenReturn(List.of());

        // act & assert
        mockMvc.perform(get("/api/admin/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void getAllUsers_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/users/all"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    public void getAllUsers_shouldReturn403WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/users/all"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAllUsers();
    }

    // search users

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void searchUsers_shouldReturn200WithUsersByFullName() throws Exception {
        // arrange
        String fullName = "user";

        UserResponseDto user1 = buildUserResponse(1L);
        user1.setFullName("user");
        UserResponseDto user2 = buildUserResponse(2L);
        user2.setFullName("user");
        List<UserResponseDto> users = List.of(user1, user2);

        when(userService.searchUsers(fullName, null)).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/search")
                        .queryParam("fullName" , "user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName").value("user"))
                .andExpect(jsonPath("$[1].fullName").value("user"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void searchUsers_shouldReturn200WithUsersByEmail() throws Exception {
        // arrange
        String email = "user@gmail.com";

        UserResponseDto user1 = buildUserResponse(1L);
        user1.setEmail("user@gmail.com");
        UserResponseDto user2 = buildUserResponse(2L);
        user2.setEmail("user2@gmail.com");
        List<UserResponseDto> users = List.of(user1, user2);

        when(userService.searchUsers(null, email)).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/search")
                        .queryParam("email" , "user@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("user@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("user2@gmail.com"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void searchUsers_shouldReturn200WithAllUserWhenNothingMatches() throws Exception {
        // arrange
        UserResponseDto user1 = buildUserResponse(1L);
        UserResponseDto user2 = buildUserResponse(2L);
        List<UserResponseDto> users = List.of(user1, user2);

        when(userService.searchUsers(null, null)).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }


    @Test
    @WithMockUser(authorities = "MEMBER")
    public void searchUsers_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/users/search"))
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    public void searchUsers_shouldReturn403WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/users/search"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAllUsers();
    }

    // create member tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createMember_shouldReturn201WhenRequestIsValid() throws Exception {
        // arrange
        CreateMemberRequestDto request = buildCreateRequest();

        UserProfileResponseDto response = buildProfileResponse(1L);
        response.setStatus(UserStatus.INACTIVE);
        response.setProfileImage(null);

        when(userService.createMember(any(CreateMemberRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/admin/users/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Test name"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.designation").value("Test Designation"))
                .andExpect(jsonPath("$.role").value("MEMBER"))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.profileImage").isEmpty());

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createMember_shouldReturn201WithAdminCreation() throws Exception {
        // arrange
        CreateMemberRequestDto request = buildCreateRequest();
        request.setRole(UserRole.ADMIN);

        UserProfileResponseDto response = buildProfileResponse(1L);
        response.setRole(UserRole.ADMIN);
        response.setStatus(UserStatus.INACTIVE);
        response.setProfileImage(null);

        when(userService.createMember(any(CreateMemberRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/admin/users/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Test name"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.designation").value("Test Designation"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.status").value("INACTIVE"))
                .andExpect(jsonPath("$.profileImage").isEmpty());

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createMember_shouldReturn409WhenUserIsAlreadyExists() throws Exception {
        // arrange
        CreateMemberRequestDto request = buildCreateRequest();

        when(userService.createMember(any(CreateMemberRequestDto.class)))
                .thenThrow(new DuplicateResourceException("User is already exists"));

        // act & assert
        mockMvc.perform(post("/api/admin/users/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void createMember_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // arrange
        CreateMemberRequestDto request = buildCreateRequest();

        // act & assert
        mockMvc.perform(post("/api/admin/users/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(userService, never()).createMember(any(CreateMemberRequestDto.class));
    }

    @Test
    public void createMember_shouldReturn403WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        CreateMemberRequestDto request = buildCreateRequest();

        // act & assert
        mockMvc.perform(post("/api/admin/users/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).createMember(any(CreateMemberRequestDto.class));
    }

    // delete user tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteUser_shouldReturn204WhenUserDeleted() throws Exception {
        // arrange
        Long userId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void deleteUser_shouldReturn404WhenUserNotFound() throws Exception {
        // arrange
        Long userId = 1L;

        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(userId);

        // act & assert
        mockMvc.perform(delete("/api/admin/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void deleteUser_shouldReturn404WhenUserIsNotAdmin() throws Exception {
        // arrange
        Long userId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(userId);
    }

    @Test
    public void deleteUser_shouldReturn404WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        Long userId = 1L;

        // act & assert
        mockMvc.perform(delete("/api/admin/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).deleteUser(userId);
    }

    // filter users tests

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void filterUsers_shouldReturn200WithFilterUsersByStatus() throws Exception {
        // arrange
        String status = "ACTIVE";

        UserProfileResponseDto user1 = buildProfileResponse(1L);
        user1.setStatus(UserStatus.ACTIVE);
        UserProfileResponseDto user2 = buildProfileResponse(2L);
        user2.setStatus(UserStatus.ACTIVE);
        List<UserProfileResponseDto> users = List.of(user1, user2);

        when(userService.filterUsers(status)).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/filter")
                        .queryParam("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"));
    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    public void filterUsers_shouldReturn200WithFilterUsersByNothing() throws Exception {
        // arrange
        UserProfileResponseDto user1 = buildProfileResponse(1L);
        user1.setStatus(UserStatus.ACTIVE);
        UserProfileResponseDto user2 = buildProfileResponse(2L);
        user2.setStatus(UserStatus.INACTIVE);
        List<UserProfileResponseDto> users = List.of(user1, user2);

        when(userService.filterUsers(null)).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1].status").value("INACTIVE"));
    }

    @Test
    @WithMockUser
    public void filterUsers_shouldReturn400WhenStatusIsNotValidEnum() throws Exception {
        // arrange
        String status = "INVALID_STATUS";

        when(userService.filterUsers(status)).thenThrow(new IllegalArgumentException("Invalid type"));

        // act & assert
        mockMvc.perform(get("/api/admin/users/filter")
                        .queryParam("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void filterUsers_shouldReturn200WithFilterUserEmptyList() throws Exception {
        // arrange
        List<UserProfileResponseDto> users = List.of();

        when(userService.filterUsers(null)).thenReturn(users);

        // act & assert
        mockMvc.perform(get("/api/admin/users/filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void filterUsers_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/users/filter")
                        .queryParam("status", "ACTIVE"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).filterUsers(anyString());
    }

    @Test
    @WithMockUser(authorities = "MEMBER")
    public void filterUsers_shouldReturn403WhenUserIsNotAdmin() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/admin/users/filter")
                        .queryParam("status", "ACTIVE"))
                .andExpect(status().isForbidden());

        verify(userService, never()).filterUsers(anyString());
    }
}
