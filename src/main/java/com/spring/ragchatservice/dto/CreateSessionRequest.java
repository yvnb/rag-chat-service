package com.spring.ragchatservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSessionRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

}
