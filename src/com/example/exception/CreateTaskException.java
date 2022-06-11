package com.example.exception;

public class CreateTaskException extends RuntimeException {
    public CreateTaskException(String message) {
        super(message);
    }
}