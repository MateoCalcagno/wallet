package com.mateo.wallet.transaction.controller;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.dto.TransferRequest;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.transaction.service.TransactionService;
import com.mateo.wallet.transaction.service.IdempotentTransferService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final IdempotentTransferService idempotentTransferService;

    public TransactionController(TransactionService transactionService,
                                IdempotentTransferService idempotentTransferService) {
        this.transactionService = transactionService;
        this.idempotentTransferService = idempotentTransferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @RequestBody @Valid TransferRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal String email
    ) {
        boolean executed = idempotentTransferService.transfer(
                email, request.getIdentifier(), request.getAmount(), idempotencyKey);

        return executed
                ? ResponseEntity.ok().build()
                : ResponseEntity.ok().header("Idempotent-Replay", "true").build();
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TransactionResponse>> getHistory(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) TransactionType type
    ) {
        return ResponseEntity.ok(
                transactionService.getHistory(email, page, size, type)
        );
    }
}