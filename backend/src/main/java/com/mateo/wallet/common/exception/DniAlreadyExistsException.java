package com.mateo.wallet.common.exception;

public class DniAlreadyExistsException extends RuntimeException {
    public DniAlreadyExistsException() {
        super("DNI already in use");
    }
}