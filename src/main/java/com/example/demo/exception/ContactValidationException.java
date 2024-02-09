package com.example.demo.exception;

public class ContactValidationException  extends RuntimeException {

    public ContactValidationException(String message) {
        super(message);
    }
}
