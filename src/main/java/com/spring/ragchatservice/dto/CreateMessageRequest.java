package com.spring.ragchatservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMessageRequest {

    @NotBlank(message = "Sender is required")
    private String sender;

    @NotBlank(message = "Content is required")
    private String content;

    private String retrievedContext;
}
