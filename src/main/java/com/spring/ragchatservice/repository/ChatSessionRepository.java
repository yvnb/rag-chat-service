package com.spring.ragchatservice.repository;

import com.spring.ragchatservice.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);
    List<ChatSession> findByUserIdAndFavoriteOrderByUpdatedAtDesc(String userId, boolean isFavorite);

    Page<ChatSession> findByUserId(String userId, Pageable pageable);
}
