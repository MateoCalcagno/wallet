package com.mateo.wallet.wallet.controller;

import com.mateo.wallet.wallet.dto.AliasRequest;
import com.mateo.wallet.wallet.dto.DepositRequest;
import com.mateo.wallet.wallet.dto.WalletResponse;
import com.mateo.wallet.wallet.dto.WithdrawRequest;
import com.mateo.wallet.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getBalance(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(walletService.getBalance(email));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
            @RequestBody @Valid DepositRequest request,
            @AuthenticationPrincipal String email) {
        walletService.deposit(email, request.getAmount(), request.getPaymentMethod());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @RequestBody @Valid WithdrawRequest request,
            @AuthenticationPrincipal String email) {
        walletService.withdraw(email, request.getAmount());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/alias")
    public ResponseEntity<Void> updateAlias(
            @RequestBody @Valid AliasRequest request,
            @AuthenticationPrincipal String email) {
        walletService.updateAlias(email, request.getAlias());
        return ResponseEntity.ok().build();
    }
}