package com.mateo.wallet.transaction.service;

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
class TransactionServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Wallet buildWallet(Long id, BigDecimal balance) {
        User user = new User("user" + id + "@gmail.com", "pass", "Nombre", "Apellido", "1234567" + id);
        Wallet wallet = new Wallet(user);
        wallet.setId(id);
        wallet.deposit(balance);
        return wallet;
    }

    @Test
    void transfer_success() {
        Wallet from = buildWallet(1L, new BigDecimal("500"));
        Wallet to = buildWallet(2L, new BigDecimal("100"));

        when(walletRepository.findByUserEmail("from@gmail.com")).thenReturn(Optional.of(from));
        when(walletRepository.findByCbu(any())).thenReturn(Optional.empty());
        when(walletRepository.findByAlias("alias.destino.test")).thenReturn(Optional.of(to));

        transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"));

        assertEquals(new BigDecimal("300"), from.getBalance());
        assertEquals(new BigDecimal("300"), to.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void transfer_insufficientBalance() {
        Wallet from = buildWallet(1L, new BigDecimal("50"));
        Wallet to = buildWallet(2L, new BigDecimal("100"));

        when(walletRepository.findByUserEmail("from@gmail.com")).thenReturn(Optional.of(from));
        when(walletRepository.findByCbu(any())).thenReturn(Optional.empty());
        when(walletRepository.findByAlias("alias.destino.test")).thenReturn(Optional.of(to));

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"))
        );
    }

    @Test
    void transfer_toYourself() {
        Wallet from = buildWallet(1L, new BigDecimal("500"));

        when(walletRepository.findByUserEmail("from@gmail.com")).thenReturn(Optional.of(from));
        when(walletRepository.findByCbu(any())).thenReturn(Optional.empty());
        when(walletRepository.findByAlias("alias.destino.test")).thenReturn(Optional.of(from));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"))
        );
    }

    @Test
    void transfer_negativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("-100"))
        );
    }
}