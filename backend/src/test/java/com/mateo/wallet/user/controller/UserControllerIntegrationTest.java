package com.mateo.wallet.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.user.dto.UserRequest;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.mateo.wallet.verification.service.EmailVerificationService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;
  
    @MockitoBean
    private EmailVerificationService emailVerificationService;
    
    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        doNothing().when(emailVerificationService).assertEmailVerified(any(String.class));
    }

    private UserRequest buildRequest(String email, String dni) {
        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword("12345678");
        request.setFirstName("Mateo");
        request.setLastName("Lopez");
        request.setDni(dni);
        return request;
    }

    @Test
    void createUser_success() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("mateo@gmail.com", "12345678"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mateo@gmail.com"))
                .andExpect(jsonPath("$.cbu").exists())
                .andExpect(jsonPath("$.alias").exists());
    }

    @Test
    void createUser_emailAlreadyExists() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("mateo@gmail.com", "12345678"))));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("mateo@gmail.com", "87654321"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    @Test
    void createUser_dniAlreadyExists() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("mateo@gmail.com", "12345678"))));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("otro@gmail.com", "12345678"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("DNI already in use"));
    }

    @Test
    void createUser_invalidEmail() throws Exception {
        UserRequest request = buildRequest("emailinvalido", "12345678");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}