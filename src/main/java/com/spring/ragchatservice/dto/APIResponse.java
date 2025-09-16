package com.spring.ragchatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {

    private boolean success;       // true for success, false for errors
    private int status;            // HTTP status code
    private String message;        // message describing success/error
    private T data;                // payload or error details

    // Convenience constructors
    public APIResponse(int status, String message, T data) {
        this.success = status >= 200 && status < 300;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(true, 200, message, data);
    }

    public static <T> APIResponse<T> error(int status, String message, T data) {
        return new APIResponse<>(false, status, message, data);
    }
}