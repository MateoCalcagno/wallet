package com.mateo.wallet.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo.wallet.auth.dto.LoginRequest;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.dto.AliasRequest;
import com.mateo.wallet.wallet.dto.DepositRequest;
import com.mateo.wallet.wallet.dto.WithdrawRequest;
import com.mateo.wallet.wallet.factory.WalletFactory;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import com.mateo.wallet.wallet.strategy.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletFactory walletFactory;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User("mateo@gmail.com", passwordEncoder.encode("12345678"), "Mateo", "Lopez", "12345678");
        User savedUser = userRepository.save(user);

        Wallet wallet = walletFactory.createForUser(savedUser);
        wallet.deposit(new BigDecimal("500"));
        walletRepository.save(wallet);

        LoginRequest loginRequest = new LoginRequest("mateo@gmail.com", "12345678");
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    private DepositRequest buildAmountRequest(BigDecimal amount, PaymentMethod method) {
        DepositRequest request = new DepositRequest();
        request.setAmount(amount);
        request.setPaymentMethod(method);
        return request;
    }

    private WithdrawRequest buildWithdrawRequest(BigDecimal amount) {
        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(amount);
        return request;
    }

    @Test
    void getBalance_success() throws Exception {
        mockMvc.perform(get("/wallet/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500.0))
                .andExpect(jsonPath("$.cbu").exists())
                .andExpect(jsonPath("$.alias").exists());
    }

    @Test
    void getBalance_noToken() throws Exception {
        mockMvc.perform(get("/wallet/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deposit_success_bankTransfer() throws Exception {
        mockMvc.perform(post("/wallet/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(
                        buildAmountRequest(new BigDecimal("100"), PaymentMethod.BANK_TRANSFER))))
                .andExpect(status().isOk());
    }

    @Test
    void deposit_invalidAmount() throws Exception {
        mockMvc.perform(post("/wallet/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(
                        buildAmountRequest(new BigDecimal("0"), PaymentMethod.BANK_TRANSFER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_success() throws Exception {
        mockMvc.perform(post("/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(buildWithdrawRequest(new BigDecimal("100")))))
                .andExpect(status().isOk());
    }

    @Test
    void withdraw_insufficientBalance() throws Exception {
        mockMvc.perform(post("/wallet/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(buildWithdrawRequest(new BigDecimal("9999")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAlias_success() throws Exception {
        AliasRequest request = new AliasRequest();
        request.setAlias("nuevo.alias.test");  

        mockMvc.perform(patch("/wallet/alias")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAlias_invalidFormat() throws Exception {
        AliasRequest request = new AliasRequest();
        request.setAlias("aliasInvalido");

        mockMvc.perform(patch("/wallet/alias")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}