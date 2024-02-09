package com.example.demo.exception;

public class RejectedTransferException extends RuntimeException {

    public RejectedTransferException(String message) {
        super(message);
    }
}
