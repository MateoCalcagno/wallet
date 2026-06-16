package com.mateo.wallet.common.exception;

public class SelfTransferException extends RuntimeException {
    public SelfTransferException() {
        super("Cannot transfer to yourself");
    }
}