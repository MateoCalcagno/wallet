package com.mateo.wallet.wallet.controller;

import com.mateo.wallet.wallet.service.WalletService;

import jakarta.validation.Valid;

import com.mateo.wallet.wallet.dto.DepositRequest;
import com.mateo.wallet.wallet.dto.WithdrawRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(walletService.getBalance(email));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
            @RequestBody @Valid DepositRequest request,
            @AuthenticationPrincipal String email
    ){
        walletService.deposit(email, request.getAmount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestBody @Valid WithdrawRequest request,
                                        @AuthenticationPrincipal String email) {
        walletService.withdraw(email, request.getAmount());
        return ResponseEntity.ok().build();
    }
}