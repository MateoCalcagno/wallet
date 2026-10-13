package com.mateo.wallet.transaction;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.common.exception.IdempotencyKeyReuseException;
import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.transaction.service.IdempotentTransferService;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.factory.WalletFactory;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:h2:mem:idempotencydb;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000")
class TransferIdempotencyIntegrationTest {

    @Autowired private IdempotentTransferService transferService;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private WalletFactory walletFactory;
    @Autowired private JdbcTemplate jdbc;

    @MockitoBean private EmailSender emailSender;

    private Wallet a;
    private Wallet b;

    @BeforeEach
    void setUp() {
        jdbc.execute("DELETE FROM idempotency_keys");
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
        a = createWallet("a@test.com", "31000001", "1000");
        b = createWallet("b@test.com", "31000002", "0");
    }

    private Wallet createWallet(String email, String dni, String balance) {
        User user = userRepository.save(new User(email, "hash", "Test", "User", dni));
        Wallet w = walletFactory.createForUser(user);
        w.deposit(new BigDecimal(balance));
        return walletRepository.save(w);
    }

    private BigDecimal balanceOf(Wallet w) {
        return walletRepository.findById(w.getId()).orElseThrow().getBalance();
    }

    @Test
    void sameKeyTwice_debitsOnlyOnce() {
        assertTrue(transferService.transfer("a@test.com", b.getCbu(), new BigDecimal("100"), "key-1"));
        assertFalse(transferService.transfer("a@test.com", b.getCbu(), new BigDecimal("100"), "key-1"),
                "El segundo intento debe ser un replay, no una nueva transferencia");

        assertEquals(0, new BigDecimal("900").compareTo(balanceOf(a)));
        assertEquals(0, new BigDecimal("100").compareTo(balanceOf(b)));
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void sameKeyDifferentPayload_isRejected() {
        transferService.transfer("a@test.com", b.getCbu(), new BigDecimal("100"), "key-2");

        assertThrows(IdempotencyKeyReuseException.class,
                () -> transferService.transfer("a@test.com", b.getCbu(), new BigDecimal("999"), "key-2"));

        assertEquals(1, transactionRepository.count());
        assertEquals(0, new BigDecimal("900").compareTo(balanceOf(a)));
    }

    @Test
    void failedTransfer_doesNotBurnTheKey() {
        // Saldo insuficiente: se revierte todo, incluida la clave.
        assertThrows(InsufficientBalanceException.class,
                () -> transferService.transfer("a@test.com", b.getCbu(), new BigDecimal("5000"), "key-3"));

        // La clave no quedó guardada, así que se puede reintentar con un monto válido.
        assertTrue(transferService.transfer("a@test.com", b.getCbu(), new BigDecimal("50"), "key-3"));
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void manyParallelRequestsWithSameKey_debitOnlyOnce() throws Exception {
        int threads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger executed = new AtomicInteger();
        AtomicInteger replays = new AtomicInteger();
        List<Throwable> unexpected = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    boolean ran = transferService.transfer(
                            "a@test.com", b.getCbu(), new BigDecimal("100"), "double-click");
                    (ran ? executed : replays).incrementAndGet();
                } catch (Throwable t) {
                    unexpected.add(t);
                }
            });
        }

        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS), "Posible deadlock");

        assertTrue(unexpected.isEmpty(), "Errores inesperados: " + unexpected);
        assertEquals(1, executed.get(), "Solo un request debe ejecutar la transferencia");
        assertEquals(threads - 1, replays.get());
        assertEquals(0, new BigDecimal("900").compareTo(balanceOf(a)));
        assertEquals(1, transactionRepository.count());
    }
}