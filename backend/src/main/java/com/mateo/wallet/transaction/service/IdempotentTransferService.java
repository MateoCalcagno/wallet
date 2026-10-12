package com.mateo.wallet.transaction.service;

import com.mateo.wallet.common.exception.IdempotencyKeyReuseException;
import com.mateo.wallet.transaction.model.IdempotencyKey;
import com.mateo.wallet.transaction.repository.IdempotencyKeyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Punto de entrada de las transferencias. NO lleva @Transactional a propósito:
 * en PostgreSQL, tras una violación de unicidad la transacción queda abortada y
 * no se puede seguir usando, así que el error se atrapa acá, afuera de ella.
 */
@Service
public class IdempotentTransferService {

    private static final int MAX_KEY_LENGTH = 64;

    private final TransactionService transactionService;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public IdempotentTransferService(TransactionService transactionService,
                                     IdempotencyKeyRepository idempotencyKeyRepository) {
        this.transactionService = transactionService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    /**
     * @return true si la transferencia se ejecutó ahora;
     *         false si era un reintento de una operación ya procesada (replay).
     */
    public boolean transfer(String email, String identifier, BigDecimal amount, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key must not be blank");
        }
        if (idempotencyKey.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "Idempotency-Key must be at most " + MAX_KEY_LENGTH + " characters");
        }

        try {
            transactionService.transfer(email, identifier, amount, idempotencyKey);
            return true;
        } catch (DataIntegrityViolationException e) {
            Optional<IdempotencyKey> existing =
                    idempotencyKeyRepository.findByUserEmailAndKey(email, idempotencyKey);

            if (existing.isEmpty()) {
                throw e; // el error era por otra cosa (ej. el CHECK de saldo), no por nuestra clave
            }
            if (!existing.get().getRequestHash().equals(TransferRequestHash.of(identifier, amount))) {
                throw new IdempotencyKeyReuseException(); // misma clave, otro contenido
            }
            return false; // misma clave y mismo contenido: es un reintento
        }
    }
}