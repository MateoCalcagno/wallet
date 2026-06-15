package com.mateo.wallet.transaction.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.transaction.factory.TransactionFactory;
import com.mateo.wallet.transaction.mapper.TransactionMapper;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.resolver.WalletResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private WalletResolver walletResolver;

    @Mock
    private TransactionFactory transactionFactory;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Wallet buildWallet(Long id, BigDecimal balance) {
        User user = new User("user" + id + "@gmail.com", "pass", "Nombre", "Apellido", "1234567" + id);
        Wallet wallet = new Wallet(user, "000000000000000000000" + id, "alias.test." + id);
        ReflectionTestUtils.setField(wallet, "id", id);
        wallet.deposit(balance);
        return wallet;
    }

    @Test
    void transfer_success() {
        Wallet from = buildWallet(1L, new BigDecimal("500"));
        Wallet to = buildWallet(2L, new BigDecimal("100"));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(from);
        when(walletResolver.resolveByIdentifier("alias.destino.test")).thenReturn(to);
        when(transactionFactory.createTransfer(any(), any(), any()))
                .thenReturn(new Transaction(from, to, new BigDecimal("200"), TransactionType.TRANSFER));

        transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"));

        assertEquals(new BigDecimal("300"), from.getBalance());
        assertEquals(new BigDecimal("300"), to.getBalance());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void transfer_insufficientBalance() {
        Wallet from = buildWallet(1L, new BigDecimal("50"));
        Wallet to = buildWallet(2L, new BigDecimal("100"));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(from);
        when(walletResolver.resolveByIdentifier("alias.destino.test")).thenReturn(to);

        assertThrows(InsufficientBalanceException.class, () ->
                transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"))
        );
    }

    @Test
    void transfer_toYourself() {
        Wallet from = buildWallet(1L, new BigDecimal("500"));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(from);
        when(walletResolver.resolveByIdentifier("alias.destino.test")).thenReturn(from);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"))
        );
    }
}