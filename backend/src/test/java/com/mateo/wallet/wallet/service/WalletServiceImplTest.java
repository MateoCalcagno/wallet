package com.mateo.wallet.wallet.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Wallet buildWallet(BigDecimal balance) {
        User user = new User("mateo@gmail.com", "pass", "Mateo", "Lopez", "12345678");
        Wallet wallet = new Wallet(user);
        wallet.deposit(balance);
        return wallet;
    }

    @Test
    void deposit_success() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletRepository.findByUserEmail("mateo@gmail.com")).thenReturn(Optional.of(wallet));

        walletService.deposit("mateo@gmail.com", new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), wallet.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void deposit_negativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                walletService.deposit("mateo@gmail.com", new BigDecimal("-50"))
        );
    }

    @Test
    void withdraw_success() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletRepository.findByUserEmail("mateo@gmail.com")).thenReturn(Optional.of(wallet));

        walletService.withdraw("mateo@gmail.com", new BigDecimal("50"));

        assertEquals(new BigDecimal("50"), wallet.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void withdraw_insufficientBalance() {
        Wallet wallet = buildWallet(new BigDecimal("30"));

        when(walletRepository.findByUserEmail("mateo@gmail.com")).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientBalanceException.class, () ->
                walletService.withdraw("mateo@gmail.com", new BigDecimal("100"))
        );
    }

    @Test
    void updateAlias_success() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletRepository.existsByAlias("nuevo.alias.test")).thenReturn(false);
        when(walletRepository.findByUserEmail("mateo@gmail.com")).thenReturn(Optional.of(wallet));

        walletService.updateAlias("mateo@gmail.com", "nuevo.alias.test");

        assertEquals("nuevo.alias.test", wallet.getAlias());
    }

    @Test
    void updateAlias_aliasAlreadyInUse() {
        when(walletRepository.existsByAlias("alias.en.uso")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                walletService.updateAlias("mateo@gmail.com", "alias.en.uso")
        );
    }
}