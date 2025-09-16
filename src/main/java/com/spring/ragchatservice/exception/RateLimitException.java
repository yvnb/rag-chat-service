package com.spring.ragchatservice.exception;

public class RateLimitException extends RuntimeException{
    public RateLimitException(String message) {
        super(message);
    }
}
