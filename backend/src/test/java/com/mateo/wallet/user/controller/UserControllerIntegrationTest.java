package com.mateo.wallet.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo.wallet.auth.dto.LoginRequest;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.user.dto.CheckAvailabilityRequest;
import com.mateo.wallet.user.dto.ResetPasswordRequest;
import com.mateo.wallet.user.dto.SendVerificationRequest;
import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.verification.dto.VerifyPinRequest;
import com.mateo.wallet.verification.service.EmailVerificationService;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        doNothing().when(emailVerificationService).assertEmailVerified(any());
        doNothing().when(emailVerificationService).sendPin(any());
        doNothing().when(emailVerificationService).verifyPin(any(), any());
    }

    private UserRequest buildUserRequest(String email, String dni) {
        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword("12345678");
        request.setFirstName("Mateo");
        request.setLastName("Lopez");
        request.setDni(dni);
        return request;
    }

    private User saveUser(String email, String dni) {
        return userRepository.save(new User(email, passwordEncoder.encode("12345678"), "Mateo", "Lopez", dni));
    }

    private String loginAndGetToken(String email) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, "12345678");
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    // ─── POST /users ─────────────────────────────────────────────────────────────

    @Test
    void createUser_success() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("mateo@gmail.com", "12345678"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mateo@gmail.com"))
                .andExpect(jsonPath("$.cbu").exists())
                .andExpect(jsonPath("$.alias").exists());
    }

    @Test
    void createUser_emailAlreadyExists() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("mateo@gmail.com", "12345678"))));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("mateo@gmail.com", "87654321"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    @Test
    void createUser_dniAlreadyExists() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("mateo@gmail.com", "12345678"))));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("otro@gmail.com", "12345678"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("DNI already in use"));
    }

    @Test
    void createUser_invalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("emailinvalido", "12345678"))))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /users/me ───────────────────────────────────────────────────────────

    @Test
    void getMe_success() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildUserRequest("mateo@gmail.com", "12345678"))))
                .andExpect(status().isOk());

        String token = loginAndGetToken("mateo@gmail.com");

        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mateo@gmail.com"))
                .andExpect(jsonPath("$.cbu").exists());
    }

    @Test
    void getMe_noToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    // ─── POST /users/send-verification ───────────────────────────────────────────

    @Test
    void sendVerification_success() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest();
        request.setEmail("mateo@gmail.com");

        mockMvc.perform(post("/users/send-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void sendVerification_invalidEmail() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest();
        request.setEmail("emailinvalido");

        mockMvc.perform(post("/users/send-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─── POST /users/verify-pin ───────────────────────────────────────────────────

    @Test
    void verifyPin_success() throws Exception {
        VerifyPinRequest request = new VerifyPinRequest();
        request.setEmail("mateo@gmail.com");
        request.setPin("123456");

        mockMvc.perform(post("/users/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void verifyPin_incorrectPin() throws Exception {
        doThrow(new IllegalArgumentException("Incorrect PIN"))
                .when(emailVerificationService).verifyPin(any(), any());

        VerifyPinRequest request = new VerifyPinRequest();
        request.setEmail("mateo@gmail.com");
        request.setPin("999999");

        mockMvc.perform(post("/users/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Incorrect PIN"));
    }

    @Test
    void verifyPin_pinTooShort() throws Exception {
        VerifyPinRequest request = new VerifyPinRequest();
        request.setEmail("mateo@gmail.com");
        request.setPin("123"); // menos de 6 dígitos

        mockMvc.perform(post("/users/verify-pin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─── POST /users/forgot-password/send-verification ───────────────────────────

    @Test
    void forgotPasswordSendVerification_success() throws Exception {
        saveUser("mateo@gmail.com", "12345678");

        SendVerificationRequest request = new SendVerificationRequest();
        request.setEmail("mateo@gmail.com");

        mockMvc.perform(post("/users/forgot-password/send-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void forgotPasswordSendVerification_userNotFound() throws Exception {
        SendVerificationRequest request = new SendVerificationRequest();
        request.setEmail("noexiste@gmail.com");

        mockMvc.perform(post("/users/forgot-password/send-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ─── POST /users/forgot-password/reset ───────────────────────────────────────

    @Test
    void resetPassword_success() throws Exception {
        saveUser("mateo@gmail.com", "12345678");

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("mateo@gmail.com");
        request.setNewPassword("nueva1234");

        mockMvc.perform(post("/users/forgot-password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void resetPassword_passwordTooShort() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("mateo@gmail.com");
        request.setNewPassword("corta");

        mockMvc.perform(post("/users/forgot-password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─── POST /users/check-availability ──────────────────────────────────────────

    @Test
    void checkAvailability_bothAvailable() throws Exception {
        CheckAvailabilityRequest request = new CheckAvailabilityRequest("mateo@gmail.com", "12345678");

        mockMvc.perform(post("/users/check-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void checkAvailability_emailTaken() throws Exception {
        saveUser("mateo@gmail.com", "12345678");

        CheckAvailabilityRequest request = new CheckAvailabilityRequest("mateo@gmail.com", "87654321");

        mockMvc.perform(post("/users/check-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}