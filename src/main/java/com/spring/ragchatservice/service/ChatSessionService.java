package com.spring.ragchatservice.service;

import com.spring.ragchatservice.dto.ChatSessionDTO;
import com.spring.ragchatservice.dto.CreateSessionRequest;
import com.spring.ragchatservice.model.ChatSession;
import com.spring.ragchatservice.repository.ChatSessionRepository;
import com.spring.ragchatservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public ChatSessionDTO createChatSession(CreateSessionRequest createSessionRequest) {
        log.info("creating new chat session for user: {}", createSessionRequest.getUserId());
        ChatSession chatSession = new ChatSession();
        chatSession.setUserId(createSessionRequest.getUserId());
        chatSession.setTitle(createSessionRequest.getTitle());
        chatSession.setDescription(createSessionRequest.getDescription());
        chatSession.setCreatedAt(Instant.now());
        chatSession.setUpdatedAt(Instant.now());
        ChatSession savedSession = chatSessionRepository.save(chatSession);
        return convertToDTO(savedSession);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDTO> getUserSessions(String userId) {
        log.info("Retrieving sessions for user: {}", userId);
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionDTO> getPaginatedUserSessions(String userId, Pageable pageable) {
        log.info("Retrieving sessions for user: {}", userId);
        return chatSessionRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ChatSessionDTO getSessionById(UUID chatSessionId) {
        ChatSession session = chatSessionRepository.findById(chatSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + chatSessionId));
        return convertToDTO(session);
    }

    @Transactional(readOnly = true)
    public boolean sessionExists(UUID chatSessionId) {
        return chatSessionRepository.existsById(chatSessionId);
    }

    @Transactional(readOnly = true)
    public ChatSession findById(UUID chatSessionId) {
        return chatSessionRepository.findById(chatSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + chatSessionId));
    }

    @Transactional
    public ChatSessionDTO updateSessionTitle(UUID sessionId, String newTitle) {
        log.info("Updating session title for session: {}", sessionId);
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));

        session.setTitle(newTitle);
        ChatSession updatedSession = chatSessionRepository.save(session);
        return convertToDTO(updatedSession);
    }

    @Transactional
    public ChatSessionDTO toggleFavorite(UUID sessionId) {
        log.info("Toggling favorite status for session: {}", sessionId);

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));

        session.setFavorite(!session.isFavorite());
        ChatSession updatedSession = chatSessionRepository.save(session);
        return convertToDTO(updatedSession);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        log.info("Deleting session: {}", sessionId);

        if (!chatSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }
        chatSessionRepository.deleteById(sessionId);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDTO> getFavoriteSessions(String userId) {
        return chatSessionRepository.findByUserIdAndFavoriteOrderByUpdatedAtDesc(userId, true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ChatSessionDTO convertToDTO(ChatSession session) {
        return new ChatSessionDTO(
                session.getId(),
                session.getUserId(),
                session.getTitle(),
                session.getDescription(),
                session.isFavorite(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

}
