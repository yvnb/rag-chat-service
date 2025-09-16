package com.spring.ragchatservice.controller;

import com.spring.ragchatservice.dto.APIResponse;
import com.spring.ragchatservice.exception.InvalidPaginationParameterException;
import com.spring.ragchatservice.exception.RateLimitException;
import com.spring.ragchatservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(APIResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        String message = "Duplicate or invalid data. Please check your input.";

        // Optional: log full exception for debugging
        log.error("Data integrity violation", ex);

        APIResponse<Object> response = new APIResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameter = ex.getName();
        String message = String.format("Invalid value for parameter '%s'. Expected %s.", parameter, ex.getRequiredType().getSimpleName());

        log.warn("Type mismatch for parameter {}: {}", parameter, ex.getValue());

        APIResponse<Object> response = new APIResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPaginationParameterException.class)
    public ResponseEntity<APIResponse<Object>> handleInvalidPagination(InvalidPaginationParameterException ex) {
        APIResponse<Object> response = APIResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<APIResponse<Object>> handleRateLimit(RateLimitException ex) {
        APIResponse<Object> response = APIResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }
}
