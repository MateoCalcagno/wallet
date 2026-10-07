package com.mateo.wallet.wallet;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.common.exception.InsufficientBalanceException;
import com.mateo.wallet.transaction.repository.TransactionRepository;
import com.mateo.wallet.transaction.service.TransactionService;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.user.repository.UserRepository;
import com.mateo.wallet.wallet.factory.WalletFactory;
import com.mateo.wallet.wallet.model.Wallet;
import com.mateo.wallet.wallet.repository.WalletRepository;
import com.mateo.wallet.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba que el saldo se mantiene consistente cuando llegan operaciones simultáneas.
 * No usa @Transactional a nivel de test: cada hilo necesita su propia transacción real.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:h2:mem:concurrencydb;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000")
class WalletConcurrencyIntegrationTest {

    private static final int THREADS = 8;

    @Autowired private WalletService walletService;
    @Autowired private TransactionService transactionService;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private WalletFactory walletFactory;

    // Evita que el listener asíncrono intente enviar mails reales.
    @MockitoBean private EmailSender emailSender;

    @BeforeEach
    void cleanDatabase() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    private Wallet createWallet(String email, String dni, String balance) {
        User user = userRepository.save(new User(email, "hash", "Test", "User", dni));
        Wallet wallet = walletFactory.createForUser(user);
        wallet.deposit(new BigDecimal(balance));
        return walletRepository.save(wallet);
    }

    private BigDecimal balanceOf(Wallet wallet) {
        return walletRepository.findById(wallet.getId()).orElseThrow().getBalance();
    }

    /** Lanza todas las tareas a la vez y devuelve los errores inesperados. */
    private List<Throwable> runConcurrently(List<Runnable> tasks) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Throwable> unexpected = Collections.synchronizedList(new ArrayList<>());

        for (Runnable task : tasks) {
            pool.submit(() -> {
                try {
                    start.await();
                    task.run();
                } catch (InsufficientBalanceException expected) {
                    // esperado: perdió la carrera por el saldo
                } catch (Throwable t) {
                    unexpected.add(t);
                }
            });
        }

        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS),
                "Las operaciones no terminaron a tiempo: posible deadlock");
        return unexpected;
    }

    @Test
    void concurrentWithdrawals_cannotSpendTheSameBalanceTwice() throws Exception {
        Wallet wallet = createWallet("solo@test.com", "30000001", "100");
        AtomicInteger successes = new AtomicInteger();

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                walletService.withdraw("solo@test.com", new BigDecimal("100"));
                successes.incrementAndGet();
            });
        }

        List<Throwable> unexpected = runConcurrently(tasks);

        assertTrue(unexpected.isEmpty(), "Errores inesperados: " + unexpected);
        assertEquals(1, successes.get(),
                "Con saldo 100 y retiros de 100, solo uno puede tener éxito");
        assertEquals(0, BigDecimal.ZERO.compareTo(balanceOf(wallet)), "El saldo final debe ser 0");
        assertEquals(1, transactionRepository.count(), "Solo debe quedar registrado un retiro");
    }

    @Test
    void crossedTransfers_keepTotalMoneyAndDoNotDeadlock() throws Exception {
        Wallet a = createWallet("a@test.com", "30000002", "1000");
        Wallet b = createWallet("b@test.com", "30000003", "1000");

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> transactionService.transfer("a@test.com", b.getCbu(), new BigDecimal("10")));
            tasks.add(() -> transactionService.transfer("b@test.com", a.getCbu(), new BigDecimal("10")));
        }

        List<Throwable> unexpected = runConcurrently(tasks);

        BigDecimal total = balanceOf(a).add(balanceOf(b));
        assertTrue(unexpected.isEmpty(), "Errores inesperados: " + unexpected);
        assertEquals(0, new BigDecimal("2000").compareTo(total),
                "La suma de saldos debe seguir siendo 2000, pero es " + total);
        assertEquals(20, transactionRepository.count(), "Deben registrarse las 20 transferencias");
    }
}