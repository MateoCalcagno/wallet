package com.mateo.wallet.transaction.mapper;

import com.mateo.wallet.transaction.dto.TransactionResponse;
import com.mateo.wallet.transaction.model.Transaction;
import com.mateo.wallet.transaction.model.TransactionType;
import com.mateo.wallet.user.model.User;
import com.mateo.wallet.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    private Wallet buildWallet(Long id, String firstName, String lastName) {
        User user = new User("user" + id + "@gmail.com", "pass", firstName, lastName, "1234567" + id);
        Wallet wallet = new Wallet(user, "000000000000000000000" + id, "alias.test." + id);
        ReflectionTestUtils.setField(wallet, "id", id);
        return wallet;
    }

    @Test
    void toResponse_transfer_asSender() {
        Wallet from = buildWallet(1L, "Mateo", "Lopez");
        Wallet to = buildWallet(2L, "Juan", "Garcia");
        Transaction tx = new Transaction(from, to, new BigDecimal("100"), TransactionType.TRANSFER);

        TransactionResponse response = mapper.toResponse(tx, from);

        assertEquals("SENT", response.direction());
        assertEquals("Juan Garcia", response.counterpartName());
        assertEquals(TransactionType.TRANSFER, response.type());
    }

    @Test
    void toResponse_transfer_asReceiver() {
        Wallet from = buildWallet(1L, "Mateo", "Lopez");
        Wallet to = buildWallet(2L, "Juan", "Garcia");
        Transaction tx = new Transaction(from, to, new BigDecimal("100"), TransactionType.TRANSFER);

        TransactionResponse response = mapper.toResponse(tx, to);

        assertEquals("RECEIVED", response.direction());
        assertEquals("Mateo Lopez", response.counterpartName());
    }

    @Test
    void toResponse_deposit_noCounterpart() {
        Wallet wallet = buildWallet(1L, "Mateo", "Lopez");
        Transaction tx = new Transaction(null, wallet, new BigDecimal("200"), TransactionType.DEPOSIT);

        TransactionResponse response = mapper.toResponse(tx, wallet);

        assertEquals("RECEIVED", response.direction());
        assertNull(response.counterpartName());
    }

    @Test
    void toResponse_withdrawal_noCounterpart() {
        Wallet wallet = buildWallet(1L, "Mateo", "Lopez");
        Transaction tx = new Transaction(wallet, null, new BigDecimal("50"), TransactionType.WITHDRAWAL);

        TransactionResponse response = mapper.toResponse(tx, wallet);

        assertEquals("SENT", response.direction());
        assertNull(response.counterpartName());
    }
}