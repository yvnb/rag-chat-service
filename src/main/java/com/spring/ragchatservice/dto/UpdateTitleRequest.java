package com.spring.ragchatservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTitleRequest {

    @NotBlank(message = "New title is required")
    private String newTitle;
}