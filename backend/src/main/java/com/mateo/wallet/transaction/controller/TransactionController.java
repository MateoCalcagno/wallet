package com.mateo.wallet.transaction.controller;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.dto.TransferRequest;
import com.mateo.wallet.transaction.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @RequestBody @Valid TransferRequest request,
            @AuthenticationPrincipal String email
    ) {
        transactionService.transfer(email, request.getIdentifier(), request.getAmount());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(transactionService.getHistory(email));
    }
}