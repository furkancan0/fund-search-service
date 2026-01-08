package com.example.demo.exception;

public class ReindexException extends RuntimeException {
    public ReindexException(String message, Throwable cause) {
        super(message, cause);
    }
}