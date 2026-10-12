package com.mateo.wallet.common.exception;

public class IdempotencyKeyReuseException extends RuntimeException {
    public IdempotencyKeyReuseException() {
        super("Idempotency-Key was already used with a different request");
    }
}