package com.example.exception;

public class TaskByIdAbsentException extends RuntimeException {
    public TaskByIdAbsentException(String message) {
        super(message);
    }
}