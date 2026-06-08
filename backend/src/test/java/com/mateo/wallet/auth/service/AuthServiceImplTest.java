package com.mateo.wallet.auth.service;

import com.mateo.wallet.auth.dto.LoginRequest;
import com.mateo.wallet.auth.dto.LoginResponse;
import com.mateo.wallet.auth.util.JwtUtil;
import com.mateo.wallet.common.exception.InvalidCredentialsException;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_success() {
        User user = new User("mateo@gmail.com", "hasheada", "Mateo", "Lopez", "12345678");
        LoginRequest request = new LoginRequest("mateo@gmail.com", "1234");

        when(userRepository.findByEmail("mateo@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("1234", "hasheada")).thenReturn(true);
        when(jwtUtil.generateToken("mateo@gmail.com")).thenReturn("fake-token");

        LoginResponse response = authService.login(request);

        assertEquals("fake-token", response.getToken());
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest("noexiste@gmail.com", "1234");

        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_wrongPassword() {
        User user = new User("mateo@gmail.com", "hasheada", "Mateo", "Lopez", "12345678");
        LoginRequest request = new LoginRequest("mateo@gmail.com", "incorrecta");

        when(userRepository.findByEmail("mateo@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrecta", "hasheada")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}