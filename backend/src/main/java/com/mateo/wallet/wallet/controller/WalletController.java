package com.mateo.wallet.wallet.controller;

import com.mateo.wallet.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@RequestParam Long userId,
                                        @RequestParam BigDecimal amount) {
        walletService.deposit(userId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@RequestParam Long userId,
                                          @RequestParam BigDecimal amount) {
        walletService.withdraw(userId, amount);
        return ResponseEntity.ok().build();
    }
}