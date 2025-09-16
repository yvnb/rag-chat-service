package com.spring.ragchatservice.exception;

public class InvalidPaginationParameterException extends RuntimeException {
    public InvalidPaginationParameterException(String message) {
        super(message);
    }
}