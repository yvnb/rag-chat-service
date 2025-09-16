package com.spring.ragchatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private UUID id;
    private UUID sessionId;
    private String sender;
    private String content;
    private String retrievedContext;
    private Instant createdAt;
}
