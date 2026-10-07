package com.mateo.wallet.verification.service;

import com.mateo.wallet.common.email.EmailSender;
import com.mateo.wallet.common.exception.TooManyRequestsException;
import com.mateo.wallet.verification.repository.EmailVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:h2:mem:verificationdb;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=15000")
class EmailVerificationAttemptsIntegrationTest {

    private static final String EMAIL = "mateo@test.com";

    @Autowired private EmailVerificationService service;
    @Autowired private EmailVerificationRepository repo;

    @MockitoBean private EmailSender emailSender;

    @BeforeEach
    void clean() {
        repo.deleteAll();
    }

    private String wrongPinFor(String realPin) {
        return realPin.equals("000000") ? "111111" : "000000";
    }

    @Test
    void failedAttempts_arePersisted_andBlockAfterTheLimit() {
        service.sendPin(EMAIL);
        String realPin = repo.findAll().get(0).getPin();
        String wrong = wrongPinFor(realPin);

        for (int i = 0; i < 5; i++) {
            assertThrows(IllegalArgumentException.class, () -> service.verifyPin(EMAIL, wrong));
        }

        assertEquals(5, repo.findAll().get(0).getAttempts());

        assertThrows(TooManyRequestsException.class, () -> service.verifyPin(EMAIL, realPin));
        assertFalse(repo.findAll().get(0).isVerified());
    }

    @Test
    void parallelWrongGuesses_cannotBypassTheLimit() throws Exception {
        service.sendPin(EMAIL);
        String wrong = wrongPinFor(repo.findAll().get(0).getPin());

        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < 20; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    service.verifyPin(EMAIL, wrong);
                } catch (Exception ignored) {
                    // se esperan IllegalArgument y TooManyRequests
                }
            });
        }

        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(60, TimeUnit.SECONDS), "posible deadlock");

        assertEquals(5, repo.findAll().get(0).getAttempts());
    }
}