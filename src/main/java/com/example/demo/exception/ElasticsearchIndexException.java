package com.example.demo.exception;

public class ElasticsearchIndexException extends RuntimeException {
    public ElasticsearchIndexException(String message, Throwable cause) {
        super(message, cause);
    }
}