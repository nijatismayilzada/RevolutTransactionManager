package com.revolut.revoluttransactionmanager.model.exception;

public class TransactionRuntimeException extends RuntimeException {
    public TransactionRuntimeException(String message) {
        super(message);
    }

    public TransactionRuntimeException(Throwable cause) {
        super(cause);
    }
}
