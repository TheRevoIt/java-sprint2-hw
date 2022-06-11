package com.example.exception;

public class RemoveByIdException extends RuntimeException {
    public RemoveByIdException(String message) {
        super(message);
    }
}