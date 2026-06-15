package com.mateo.wallet.wallet.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.transaction.recorder.TransactionRecorder;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.factory.WalletFactory;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import com.mateo.wallet.wallet.resolver.WalletResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRecorder transactionRecorder;  // antes era TransactionService

    @Mock
    private WalletResolver walletResolver;

    @Mock
    private WalletFactory walletFactory;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Wallet buildWallet(BigDecimal balance) {
        User user = new User("mateo@gmail.com", "pass", "Mateo", "Lopez", "12345678");
        Wallet wallet = new Wallet(user, "1234567890123456789012", "sol.luna.rio");
        wallet.deposit(balance);
        return wallet;
    }

    @Test
    void deposit_success() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletResolver.resolveByEmail("mateo@gmail.com")).thenReturn(wallet);

        walletService.deposit("mateo@gmail.com", new BigDecimal("50"));

        assertEquals(new BigDecimal("150"), wallet.getBalance());
        verify(transactionRecorder, times(1)).recordDeposit(eq(wallet), eq(new BigDecimal("50")));  // antes registerDeposit
    }

    @Test
    void withdraw_success() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletResolver.resolveByEmail("mateo@gmail.com")).thenReturn(wallet);

        walletService.withdraw("mateo@gmail.com", new BigDecimal("50"));

        assertEquals(new BigDecimal("50"), wallet.getBalance());
        verify(transactionRecorder, times(1)).recordWithdrawal(eq(wallet), eq(new BigDecimal("50")));  // antes registerWithdrawal
    }

    @Test
    void withdraw_insufficientBalance() {
        Wallet wallet = buildWallet(new BigDecimal("30"));

        when(walletResolver.resolveByEmail("mateo@gmail.com")).thenReturn(wallet);

        assertThrows(InsufficientBalanceException.class, () ->
                walletService.withdraw("mateo@gmail.com", new BigDecimal("100"))
        );
    }

    @Test
    void updateAlias_success() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletRepository.existsByAlias("nuevo.alias.test")).thenReturn(false);
        when(walletResolver.resolveByEmail("mateo@gmail.com")).thenReturn(wallet);

        walletService.updateAlias("mateo@gmail.com", "nuevo.alias.test");

        assertEquals("nuevo.alias.test", wallet.getAlias());
    }

    @Test
    void updateAlias_aliasAlreadyInUse() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletResolver.resolveByEmail("mateo@gmail.com")).thenReturn(wallet);
        when(walletRepository.existsByAlias("alias.en.uso")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                walletService.updateAlias("mateo@gmail.com", "alias.en.uso")
        );
    }

    @Test
    void updateAlias_sameAlias_doesNothing() {
        Wallet wallet = buildWallet(new BigDecimal("100"));

        when(walletResolver.resolveByEmail("mateo@gmail.com")).thenReturn(wallet);

        walletService.updateAlias("mateo@gmail.com", "sol.luna.rio");

        verify(walletRepository, never()).existsByAlias(any());
        assertEquals("sol.luna.rio", wallet.getAlias());
    }
}