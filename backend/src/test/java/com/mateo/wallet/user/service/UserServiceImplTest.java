package com.mateo.wallet.user.service;

import com.mateo.wallet.common.exception.DniAlreadyExistsException;
import com.mateo.wallet.common.exception.EmailAlreadyExistsException;
import com.mateo.wallet.common.exception.ResourceNotFoundException;
import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.dto.UserResponse;
import com.mateo.wallet.user.mapper.UserMapper;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest buildRequest() {
        UserRequest request = new UserRequest();
        request.setEmail("mateo@gmail.com");
        request.setPassword("12345678");
        request.setFirstName("Mateo");
        request.setLastName("Lopez");
        request.setDni("12345678");
        return request;
    }

    private User buildUser() {
        return new User("mateo@gmail.com", "hashed", "Mateo", "Lopez", "12345678");
    }

    private Wallet buildWallet(User user) {
        return new Wallet(user, "1234567890123456789012", "sol.luna.rio");
    }

    // ─── createUser ─────────────────────────────────────────────────────────────

    @Test
    void createUser_success() {
        UserRequest request = buildRequest();
        User user = buildUser();
        Wallet wallet = buildWallet(user);
        UserResponse expectedResponse = new UserResponse(1L, "mateo@gmail.com", "Mateo", "Lopez", "12345678", wallet.getCbu(), wallet.getAlias());

        when(userRepository.existsByEmail("mateo@gmail.com")).thenReturn(false);
        when(userRepository.existsByDni("12345678")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hashed");
        when(userMapper.toEntity(request, "hashed")).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(walletService.createForUser(user)).thenReturn(wallet);
        when(userMapper.toResponse(user, wallet)).thenReturn(expectedResponse);

        UserResponse result = userService.createUser(request);

        assertEquals("mateo@gmail.com", result.email());
        verify(userRepository).save(user);
        verify(walletService).createForUser(user);
    }

    @Test
    void createUser_emailAlreadyExists_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("mateo@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(buildRequest()));

        verify(userRepository, never()).save(any());
        verify(walletService, never()).createForUser(any());
    }

    @Test
    void createUser_dniAlreadyExists_throwsDniAlreadyExistsException() {
        when(userRepository.existsByEmail("mateo@gmail.com")).thenReturn(false);
        when(userRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(DniAlreadyExistsException.class, () -> userService.createUser(buildRequest()));

        verify(userRepository, never()).save(any());
    }

    // ─── getUserByEmail ──────────────────────────────────────────────────────────

    @Test
    void getUserByEmail_success() {
        User user = buildUser();
        Wallet wallet = buildWallet(user);
        UserResponse expectedResponse = new UserResponse(1L, "mateo@gmail.com", "Mateo", "Lopez", "12345678", wallet.getCbu(), wallet.getAlias());

        when(userRepository.findByEmail("mateo@gmail.com")).thenReturn(Optional.of(user));
        when(walletService.getByUserId(user.getId())).thenReturn(wallet);
        when(userMapper.toResponse(user, wallet)).thenReturn(expectedResponse);

        UserResponse result = userService.getUserByEmail("mateo@gmail.com");

        assertEquals("mateo@gmail.com", result.email());
    }

    @Test
    void getUserByEmail_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("noexiste@gmail.com"));
    }

    // ─── resetPassword ───────────────────────────────────────────────────────────

    @Test
    void resetPassword_success() {
        User user = buildUser();

        when(userRepository.findByEmail("mateo@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("nueva1234")).thenReturn("nueva_hashed");

        userService.resetPassword("mateo@gmail.com", "nueva1234");

        assertEquals("nueva_hashed", user.getPassword());
    }

    @Test
    void resetPassword_userNotFound_throwsResourceNotFoundException() {
        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.resetPassword("noexiste@gmail.com", "nueva1234"));
    }

    // ─── checkAvailability ───────────────────────────────────────────────────────

    @Test
    void checkAvailability_emailTaken_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("mateo@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () ->
                userService.checkAvailability("mateo@gmail.com", "12345678"));
    }

    @Test
    void checkAvailability_dniTaken_throwsDniAlreadyExistsException() {
        when(userRepository.existsByEmail("mateo@gmail.com")).thenReturn(false);
        when(userRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(DniAlreadyExistsException.class, () ->
                userService.checkAvailability("mateo@gmail.com", "12345678"));
    }

    @Test
    void checkAvailability_bothAvailable_doesNotThrow() {
        when(userRepository.existsByEmail("mateo@gmail.com")).thenReturn(false);
        when(userRepository.existsByDni("12345678")).thenReturn(false);

        assertDoesNotThrow(() -> userService.checkAvailability("mateo@gmail.com", "12345678"));
    }
}