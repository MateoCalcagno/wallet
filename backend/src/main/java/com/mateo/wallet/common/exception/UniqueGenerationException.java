package com.mateo.wallet.common.exception;

public class UniqueGenerationException extends RuntimeException {
    public UniqueGenerationException(String field) {
        super("Could not generate a unique " + field + ". Please try again.");
    }
}