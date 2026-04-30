package com.example.bankcards.service;

import com.example.bankcards.dto.Requests.LoginRequest;
import com.example.bankcards.dto.Requests.RegisterRequest;
import com.example.bankcards.dto.Responses.LoginResponse;
import com.example.bankcards.entity.Enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.Interfaces.UserRepository;
import com.example.bankcards.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthServiceImpl authService = new AuthServiceImpl(userRepository, jwtUtil, passwordEncoder);

    @Test
    void login_shouldReturnToken() {
        User user = new User(1L, "alice", "alice@example.com", passwordEncoder.encode("password123"), Role.USER, null, null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");
        when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

        LoginResponse response = authService.login(new LoginRequest("alice", "password123"));

        assertEquals("jwt-token", response.getToken());
        assertEquals("alice", response.getUsername());
        assertEquals(Role.USER, response.getRole());
        assertEquals(1L, response.getUserId());
        verify(jwtUtil).generateToken(user);
    }

    @Test
    void login_withWrongPassword_shouldThrowBadCredentials() {
        User user = new User(1L, "alice", "alice@example.com", passwordEncoder.encode("password123"), Role.USER, null, null);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("alice", "wrong")));
    }

    @Test
    void login_withUnknownUser_shouldThrowBadCredentials() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("unknown", "password")));
    }

    @Test
    void register_shouldReturnToken() {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(false);

        User saved = new User(2L, "bob", "bob@example.com", passwordEncoder.encode("password123"), Role.USER, null, null);
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(jwtUtil.generateToken(saved)).thenReturn("new-token");
        when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

        LoginResponse response = authService.register(new RegisterRequest("bob", "bob@example.com", "password123"));

        assertEquals("new-token", response.getToken());
        assertEquals("bob", response.getUsername());
        assertEquals(Role.USER, response.getRole());
        assertEquals(2L, response.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_withDuplicateUsername_shouldThrowException() {
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(new RegisterRequest("bob", "bob@example.com", "password123")));

        assertEquals("Пользователь с таким именем уже существует", ex.getMessage());
    }

    @Test
    void register_withDuplicateEmail_shouldThrowException() {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.register(new RegisterRequest("bob", "bob@example.com", "password123")));

        assertEquals("Пользователь с таким email уже существует", ex.getMessage());
    }
}
