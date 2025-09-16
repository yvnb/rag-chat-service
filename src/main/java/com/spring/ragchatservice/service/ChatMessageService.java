package com.spring.ragchatservice.service;

import com.spring.ragchatservice.dto.ChatMessageDTO;
import com.spring.ragchatservice.exception.ResourceNotFoundException;
import com.spring.ragchatservice.model.ChatMessage;
import com.spring.ragchatservice.model.ChatSession;
import com.spring.ragchatservice.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatSessionService chatSessionService;

    @Transactional
    public ChatMessageDTO addMessage(UUID sessionId, String sender, String content, String retrievedContext) {
        log.info("Adding message to session: {}", sessionId);

        ChatSession session = chatSessionService.findById(sessionId);

        ChatMessage message = new ChatMessage();
        message.setChatSession(session);
        message.setSender(sender);
        message.setContent(content);
        message.setRetrievedContext(retrievedContext);

        ChatMessage savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getSessionMessages(UUID sessionId) {
        log.info("Retrieving messages for session: {}", sessionId);

        if (!chatSessionService.sessionExists(sessionId)) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        return messageRepository.findByChatSessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getSessionMessagesPaginated(UUID sessionId, Pageable pageable) {
        if (!chatSessionService.sessionExists(sessionId)) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        return messageRepository.findByChatSessionId(sessionId, pageable)
                .map(this::convertToDTO);
    }

    private ChatMessageDTO convertToDTO(ChatMessage message) {
        return new ChatMessageDTO(
                message.getId(),
                message.getChatSession().getId(),
                message.getSender(),
                message.getContent(),
                message.getRetrievedContext(),
                message.getCreatedAt()
        );
    }

}
