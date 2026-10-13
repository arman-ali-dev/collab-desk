package com.example.collab_desk.controller.member;


import com.example.collab_desk.config.SecurityConfig;
import com.example.collab_desk.dto.requestDto.LoginRequestDto;
import com.example.collab_desk.dto.requestDto.PasswordSetupRequestDto;
import com.example.collab_desk.dto.requestDto.RegisterRequestDto;
import com.example.collab_desk.dto.responseDto.AuthResponseDto;
import com.example.collab_desk.dto.responseDto.PasswordSetupResponseDto;
import com.example.collab_desk.entity.PasswordSetupToken;
import com.example.collab_desk.entity.User;
import com.example.collab_desk.enums.UserRole;
import com.example.collab_desk.exception.DuplicateResourceException;
import com.example.collab_desk.exception.InvalidTokenException;
import com.example.collab_desk.service.AuthService;
import com.example.collab_desk.service.impl.CustomUserDetailsService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @Inject
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    // register tests

    private RegisterRequestDto buildRegisterRequest() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setFullName("test name");
        request.setEmail("test@gmail.com");
        request.setDesignation("test designation");
        request.setPassword("test@123");
        request.setRole(UserRole.MEMBER);
        return request;
    }

    @Test
    public void register_shouldReturn201WithAuthResponse() throws Exception {
        // arrange
        RegisterRequestDto request = buildRegisterRequest();
        AuthResponseDto response = new AuthResponseDto("null", "Registration Successfully");
        when(authService.register(any(RegisterRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration Successfully"))
                .andExpect(jsonPath("$.token").value("null"));
    }

    @Test
    public void register_shouldReturn409WhenUserIsAlreadyExists() throws Exception {
        // arrange
        RegisterRequestDto request = buildRegisterRequest();
        when(authService.register(any(RegisterRequestDto.class)))
                .thenThrow(new DuplicateResourceException("User is already exists"));

        // act & assert
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void register_shouldReturn400WhenValidationFailed() throws Exception {
        // arrange
        RegisterRequestDto request = buildRegisterRequest();
        request.setFullName("");

        // act & assert
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequestDto.class));
    }


    // login tests

    private LoginRequestDto buildLoginRequest() {
        LoginRequestDto r = new LoginRequestDto();
        r.setEmail("test@gmail.com");
        r.setPassword("test@123");
        return r;
    }

    @Test
    public void login_shouldReturn200WhenCredentialsAreValid() throws Exception {
        // arrange
        LoginRequestDto request = buildLoginRequest();

        AuthResponseDto response = new AuthResponseDto();
        response.setToken("jwt-token");
        response.setMessage("Login successfully");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        // act & assert

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.message").value("Login successfully"));
    }

    @Test
    public void login_shouldReturn401WhenCredentialsAreNotValid() throws Exception {
        // arrange
        LoginRequestDto request = buildLoginRequest();

        AuthResponseDto response = new AuthResponseDto();
        response.setToken("jwt-token");
        response.setMessage("Login successfully");

        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        // act & assert

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void login_shouldReturn400WhenValidationsFails() throws Exception {
        // arrange
        LoginRequestDto request = buildLoginRequest();
        request.setEmail("");

        // act & assert
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequestDto.class));
    }


    // set password tests


    @Test
    public void setPassword_shouldReturn200WhenPasswordSetSuccessfully() throws Exception {
        // arrange
        PasswordSetupRequestDto request = new PasswordSetupRequestDto();
        request.setToken("token");
        request.setPassword("test@123");
        PasswordSetupResponseDto response = new PasswordSetupResponseDto("Password set successfully");

        when(authService.setPassword(any(PasswordSetupRequestDto.class))).thenReturn(response);

        // act & assert
        mockMvc.perform(put("/auth/set-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password set successfully"));
    }

    @Test
    public void setPassword_shouldReturn400WhenTokenIsAlreadyUsedOrExpired() throws Exception {
        // arrange
        PasswordSetupRequestDto request = new PasswordSetupRequestDto();
        request.setToken("token");
        request.setPassword("test@123");

        when(authService.setPassword(any(PasswordSetupRequestDto.class)))
                .thenThrow(new InvalidTokenException("Token is expired or used"));

        // act & assert
        mockMvc.perform(put("/auth/set-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void setPassword_shouldReturn400WhenValidationsFails() throws Exception {
        // arrange
        PasswordSetupRequestDto request = new PasswordSetupRequestDto();
        request.setToken("");

        // act & assert
        mockMvc.perform(put("/auth/set-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).setPassword(any(PasswordSetupRequestDto.class));
    }
}
