package com.spring.ragchatservice.repository;

import com.spring.ragchatservice.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
     List<ChatMessage> findByChatSessionIdOrderByCreatedAtAsc(UUID chatSessionId);
     Page<ChatMessage> findByChatSessionId(UUID chatSessionId, Pageable pageable);
}
