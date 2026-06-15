package com.mateo.wallet.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateo.wallet.auth.dto.LoginRequest;
import com.mateo.wallet.transaction.dto.TransferRequest;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.factory.WalletFactory;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

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

    private String token;
    private Wallet toWallet;

    @Autowired
    private WalletFactory walletFactory;

    @BeforeEach
    void setUp() throws Exception {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        User fromUser = new User("from@gmail.com", passwordEncoder.encode("12345678"), "Mateo", "Lopez", "12345678");
        User savedFrom = userRepository.save(fromUser);
        Wallet fromWallet = walletFactory.createForUser(savedFrom);
        fromWallet.deposit(new BigDecimal("500"));
        walletRepository.save(fromWallet);

        User toUser = new User("to@gmail.com", passwordEncoder.encode("12345678"), "Juan", "Garcia", "87654321");
        User savedTo = userRepository.save(toUser);
        toWallet = walletFactory.createForUser(savedTo);
        walletRepository.save(toWallet);

        LoginRequest loginRequest = new LoginRequest("from@gmail.com", "12345678");
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        token = objectMapper.readTree(response).get("token").asText();
    }

    private TransferRequest buildTransfer(String identifier, BigDecimal amount) {
        TransferRequest request = new TransferRequest();
        request.setIdentifier(identifier);
        request.setAmount(amount);
        return request;
    }

    @Test
    void transfer_success() throws Exception {
        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(buildTransfer(toWallet.getAlias(), new BigDecimal("100")))))
                .andExpect(status().isOk());
    }

    @Test
    void transfer_insufficientBalance() throws Exception {
        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(buildTransfer(toWallet.getAlias(), new BigDecimal("9999")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_toYourself() throws Exception {
        Wallet fromWallet = walletRepository.findByUserEmail("from@gmail.com").get();

        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(buildTransfer(fromWallet.getAlias(), new BigDecimal("100")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_noToken() throws Exception {
        mockMvc.perform(post("/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildTransfer(toWallet.getAlias(), new BigDecimal("100")))))
                .andExpect(status().isUnauthorized());
    }
}