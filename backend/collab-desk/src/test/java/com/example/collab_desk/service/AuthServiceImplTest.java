package com.example.collab_desk.service;

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
import com.example.collab_desk.exception.ResourceNotFoundException;
import com.example.collab_desk.repository.PasswordSetupTokenRepository;
import com.example.collab_desk.repository.UserRepository;
import com.example.collab_desk.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordSetupTokenRepository passwordSetupTokenRepository;

    @InjectMocks
    private AuthServiceImpl authService;


    private RegisterRequestDto buildRequest() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setDesignation("test designation");
        request.setEmail("test@gmail.com");
        request.setFullName("test name");
        request.setPassword("test@123");
        request.setRole(UserRole.MEMBER);
        return request;
    }

    // register tests

    @Test
    public void register_shouldRegisterUserSuccessfully() {
        RegisterRequestDto request = buildRequest();

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("test@123")).thenReturn("xyz123");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // act
        AuthResponseDto response = authService.register(request);

        // assert
        assertNotNull(response);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("test designation", savedUser.getDesignation());
        assertEquals("test name", savedUser.getFullName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals(UserRole.MEMBER, savedUser.getRole());
        assertEquals("xyz123", savedUser.getPassword());
    }

    @Test
    public void register_shouldRegisterAdminUserSuccessfully() {
        RegisterRequestDto request = buildRequest();
        request.setRole(UserRole.ADMIN);

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("test@123")).thenReturn("xyz123");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // act
        AuthResponseDto response = authService.register(request);

        // assert
        assertNotNull(response);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("test designation", savedUser.getDesignation());
        assertEquals("test name", savedUser.getFullName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals(UserRole.ADMIN, savedUser.getRole());
        assertEquals("xyz123", savedUser.getPassword());
    }

    @Test
    public void register_shouldThrowExceptionWhenUserAlreadyExists() {
        RegisterRequestDto request = buildRequest();

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        // act & assert
        assertThrows(DuplicateResourceException.class, () -> authService.register(request));

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void register_shouldThrowExceptionWhenSaveFails() {
        RegisterRequestDto request = buildRequest();

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // act & assert
        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    // login tests

    private LoginRequestDto buildLoginRequest() {
        LoginRequestDto r = new LoginRequestDto();
        r.setEmail("test@gmail.com");
        r.setPassword("test@123");
        return r;
    }


    @Test
    public void login_shouldReturnTokenWhenCredentialsAreValid() {
        // arrange
        LoginRequestDto request = buildLoginRequest();

        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(
                "test@gmail.com", null, List.of(new SimpleGrantedAuthority("MEMBER")));

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated);
        when(jwtService.generateToken(authenticated)).thenReturn("jwt-token");

        // act
        AuthResponseDto response = authService.login(request);

        // assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Login successfully", response.getMessage());


        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        Authentication sent = captor.getValue();
        assertEquals("test@gmail.com", sent.getPrincipal());
        assertEquals("test@123", sent.getCredentials());
        assertFalse(sent.isAuthenticated());

        verify(jwtService).generateToken(authenticated);
    }

    @Test
    public void login_shouldThrowExceptionWhenCredentialsAreInvalid() {
        // arrange
        LoginRequestDto request = buildLoginRequest();

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        // act & assert
        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtService, never()).generateToken(any(Authentication.class));
    }

    // password setup tests

    private PasswordSetupRequestDto buildPasswordSetupRequest() {
        PasswordSetupRequestDto request = new PasswordSetupRequestDto();
        request.setPassword("test@123");
        request.setToken("xyz123");
        return request;
    }

    private PasswordSetupToken buildPasswordSetupToken() {
        PasswordSetupToken passwordSetupToken = new PasswordSetupToken();
        passwordSetupToken.setTokenHash("xyz123");
        passwordSetupToken.setIsUsed(false);
        passwordSetupToken.setExpireTime(LocalDateTime.now().plusHours(2));
        User user = new User();
        user.setId(1L);
        passwordSetupToken.setUser(user);
        return passwordSetupToken;
    }

    @Test
    public void setPassword_shouldSetPasswordSuccessfully() {
        // arrange
        PasswordSetupRequestDto request = buildPasswordSetupRequest();

        PasswordSetupToken passwordSetupToken = buildPasswordSetupToken();

        when(passwordSetupTokenRepository.findByTokenHash("xyz123"))
                .thenReturn(Optional.of(passwordSetupToken));
        when(passwordEncoder.encode("test@123")).thenReturn("abc");

        // act
        PasswordSetupResponseDto response = authService.setPassword(request);

        // assert
        assertNotNull(response);
        assertEquals("Password set successfully", response.getMessage());

        verify(passwordSetupTokenRepository).save(any(PasswordSetupToken.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void setPassword_shouldThrowExceptionWhenTokenNotFound() {
        // arrange
        PasswordSetupRequestDto request = buildPasswordSetupRequest();

        when(passwordSetupTokenRepository.findByTokenHash("xyz123")).thenReturn(Optional.empty());

        // act
        assertThrows(ResourceNotFoundException.class, () -> authService.setPassword(request));

        verify(passwordEncoder, never()).encode(anyString());
        verify(passwordSetupTokenRepository, never()).save(any(PasswordSetupToken.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void setPassword_shouldThrowExceptionWhenTokenIsAlreadyUsed() {
        // arrange
        PasswordSetupRequestDto request = buildPasswordSetupRequest();

        PasswordSetupToken passwordSetupToken = buildPasswordSetupToken();
        passwordSetupToken.setIsUsed(true);

        when(passwordSetupTokenRepository.findByTokenHash("xyz123"))
                .thenReturn(Optional.of(passwordSetupToken));

        // act
        assertThrows(InvalidTokenException.class, () -> authService.setPassword(request));

        verify(passwordEncoder, never()).encode(anyString());
        verify(passwordSetupTokenRepository, never()).save(any(PasswordSetupToken.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void setPassword_shouldThrowExceptionWhenTokenIsExpired() {
        // arrange
        PasswordSetupRequestDto request = buildPasswordSetupRequest();

        PasswordSetupToken passwordSetupToken = buildPasswordSetupToken();
        passwordSetupToken.setExpireTime(LocalDateTime.now().minusHours(1));

        when(passwordSetupTokenRepository.findByTokenHash("xyz123"))
                .thenReturn(Optional.of(passwordSetupToken));

        // act
        assertThrows(InvalidTokenException.class, () -> authService.setPassword(request));

        verify(passwordEncoder, never()).encode(anyString());
        verify(passwordSetupTokenRepository, never()).save(any(PasswordSetupToken.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void setPassword_shouldThrowExceptionWhenUserSaveFails() {
        // arrange
        PasswordSetupRequestDto request = buildPasswordSetupRequest();

        PasswordSetupToken passwordSetupToken = buildPasswordSetupToken();

        when(passwordSetupTokenRepository.findByTokenHash("xyz123"))
                .thenReturn(Optional.of(passwordSetupToken));


        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // act
        assertThrows(RuntimeException.class, () -> authService.setPassword(request));

        verify(passwordSetupTokenRepository, never()).save(any(PasswordSetupToken.class));
    }
}
