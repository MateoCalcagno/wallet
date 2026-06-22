package com.mateo.wallet.transaction.service;

import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.common.exception.SelfTransferException;
import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.event.TransferCompletedEvent;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Wallet buildWallet(Long id, BigDecimal balance) {
        User user = new User("user" + id + "@gmail.com", "pass", "Nombre", "Apellido", "1234567" + id);
        Wallet wallet = new Wallet(user, "000000000000000000000" + id, "alias.test." + id);
        ReflectionTestUtils.setField(wallet, "id", id);
        wallet.deposit(balance);
        return wallet;
    }

    // ─── transfer ───────────────────────────────────────────────────────────────

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
        verify(transactionRepository).save(any());
    }

    @Test
    void transfer_success_publishesEvent() {
        Wallet from = buildWallet(1L, new BigDecimal("500"));
        Wallet to = buildWallet(2L, new BigDecimal("100"));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(from);
        when(walletResolver.resolveByIdentifier("alias.destino.test")).thenReturn(to);
        when(transactionFactory.createTransfer(any(), any(), any()))
                .thenReturn(new Transaction(from, to, new BigDecimal("200"), TransactionType.TRANSFER));

        transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"));

        ArgumentCaptor<TransferCompletedEvent> captor = ArgumentCaptor.forClass(TransferCompletedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
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

        verify(transactionRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void transfer_toYourself() {
        Wallet from = buildWallet(1L, new BigDecimal("500"));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(from);
        when(walletResolver.resolveByIdentifier("alias.destino.test")).thenReturn(from);

        assertThrows(SelfTransferException.class, () ->
                transactionService.transfer("from@gmail.com", "alias.destino.test", new BigDecimal("200"))
        );

        verify(transactionRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    // ─── getHistory ──────────────────────────────────────────────────────────────

    @Test
    void getHistory_noFilter_returnsAllTransactions() {
        Wallet wallet = buildWallet(1L, new BigDecimal("500"));
        Transaction tx = new Transaction(wallet, buildWallet(2L, BigDecimal.ZERO), new BigDecimal("100"), TransactionType.TRANSFER);
        Page<Transaction> page = new PageImpl<>(List.of(tx));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(wallet);
        when(transactionRepository.findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);
        when(transactionMapper.toResponse(eq(tx), eq(wallet))).thenReturn(mock(TransactionResponse.class));

        Page<TransactionResponse> result = transactionService.getHistory("from@gmail.com", 0, 10, null);

        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                eq(1L), eq(1L), any(Pageable.class));
        verify(transactionRepository, never()).findByWalletIdAndType(any(), any(), any());
    }

    @Test
    void getHistory_withTypeFilter_usesFilteredQuery() {
        Wallet wallet = buildWallet(1L, new BigDecimal("500"));
        Transaction tx = new Transaction(wallet, buildWallet(2L, BigDecimal.ZERO), new BigDecimal("100"), TransactionType.TRANSFER);
        Page<Transaction> page = new PageImpl<>(List.of(tx));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(wallet);
        when(transactionRepository.findByWalletIdAndType(eq(1L), eq(TransactionType.TRANSFER), any(Pageable.class)))
                .thenReturn(page);
        when(transactionMapper.toResponse(eq(tx), eq(wallet))).thenReturn(mock(TransactionResponse.class));

        Page<TransactionResponse> result = transactionService.getHistory("from@gmail.com", 0, 10, TransactionType.TRANSFER);

        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByWalletIdAndType(eq(1L), eq(TransactionType.TRANSFER), any(Pageable.class));
        verify(transactionRepository, never()).findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(any(), any(), any());
    }

    @Test
    void getHistory_empty_returnsEmptyPage() {
        Wallet wallet = buildWallet(1L, new BigDecimal("500"));

        when(walletResolver.resolveByEmail("from@gmail.com")).thenReturn(wallet);
        when(transactionRepository.findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                eq(1L), eq(1L), any(Pageable.class))).thenReturn(Page.empty());

        Page<TransactionResponse> result = transactionService.getHistory("from@gmail.com", 0, 10, null);

        assertTrue(result.isEmpty());
    }
}