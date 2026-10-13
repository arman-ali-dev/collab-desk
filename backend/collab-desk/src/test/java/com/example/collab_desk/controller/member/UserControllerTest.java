package com.example.collab_desk.controller.member;

import com.example.collab_desk.dto.requestDto.EditProfileRequestDto;
import com.example.collab_desk.dto.responseDto.UserProfileResponseDto;
import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.enums.UserStatus;
import com.example.collab_desk.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    // get profile

    @Test
    @WithMockUser
    public void getProfile_shouldReturn200WithUserProfile() throws Exception {
        // arrange
        UserProfileResponseDto user = new UserProfileResponseDto(1L, "Test name",
                "test@gmail.com", "Test designation", UserRole.MEMBER,
                "https://api.com/profile.png", UserStatus.ACTIVE);

        when(userService.getProfile()).thenReturn(user);

        // act & assert
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Test name"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.designation").value("Test designation"))
                .andExpect(jsonPath("$.role").value("MEMBER"))
                .andExpect(jsonPath("$.profileImage")
                        .value("https://api.com/profile.png"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    public void getProfile_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // act & assert
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getProfile();
    }

    // edit profile tests

    @Test
    @WithMockUser
    public void editProfile_shouldReturn200WithEditedProfile() throws Exception {
        // arrange

        EditProfileRequestDto request = new EditProfileRequestDto();
        request.setFullName("new name");
        request.setEmail("new@gmail.com");
        request.setDesignation("new designation");
        request.setProfileImage("https://api.com/new-profile.png");

        UserProfileResponseDto response = new UserProfileResponseDto(1L, "new name",
                "new@gmail.com", "new designation", UserRole.MEMBER,
                "https://api.com/new-profile.png", UserStatus.ACTIVE);

        when(userService.editProfile(any(EditProfileRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(put("/api/users/me/edit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("new name"))
                .andExpect(jsonPath("$.email").value("new@gmail.com"))
                .andExpect(jsonPath("$.designation").value("new designation"))
                .andExpect(jsonPath("$.role").value("MEMBER"))
                .andExpect(jsonPath("$.profileImage")
                        .value("https://api.com/new-profile.png"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    public void editProfile_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        // arrange
        EditProfileRequestDto request = new EditProfileRequestDto();
        request.setFullName("new name");
        request.setEmail("new@gmail.com");
        request.setDesignation("new designation");
        request.setProfileImage("https://api.com/new-profile.png");

        // act & assert
        mockMvc.perform(put("/api/users/me/edit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).editProfile(any(EditProfileRequestDto.class));
    }

}
