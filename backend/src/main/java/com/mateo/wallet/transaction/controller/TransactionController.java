package com.mateo.wallet.transaction.controller;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            @RequestParam Long toUserId,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal String email
    ) {
        transactionService.transfer(email, toUserId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @AuthenticationPrincipal String email
    ) {
        return ResponseEntity.ok(transactionService.getHistory(email));
    }
}