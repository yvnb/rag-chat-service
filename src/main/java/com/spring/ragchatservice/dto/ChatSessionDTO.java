package com.spring.ragchatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionDTO {

    private UUID id;
    private String userId;
    private String title;
    private String description;
    private boolean isFavorite;
    private Instant createdAt;
    private Instant updatedAt;

}
