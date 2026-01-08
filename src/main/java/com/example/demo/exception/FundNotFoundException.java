package com.example.demo.exception;

public class FundNotFoundException extends RuntimeException {

    public FundNotFoundException(String message) {
        super(message);
    }
}