package com.spring.ragchatservice.service;

import com.spring.ragchatservice.dto.ChatMessageDTO;
import com.spring.ragchatservice.dto.CreateMessageRequest;
import com.spring.ragchatservice.exception.ResourceNotFoundException;
import com.spring.ragchatservice.mapper.ChatMessageMapper;
import com.spring.ragchatservice.model.ChatMessage;
import com.spring.ragchatservice.model.ChatSession;
import com.spring.ragchatservice.repository.ChatMessageRepository;
import com.spring.ragchatservice.service.ai.AIService;
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
    private final ChatMessageMapper chatMessageMapper;
    private final AIService aiService;

    @Transactional
    public ChatMessageDTO addMessage(UUID sessionId, CreateMessageRequest createMessageRequest) {
        log.info("Adding message to session: {}", sessionId);

        ChatSession chatSession = chatSessionService.findById(sessionId);

        ChatMessage message = new ChatMessage();
        message.setChatSession(chatSession);
        message.setSender(createMessageRequest.getSender());
        message.setContent(createMessageRequest.getContent());
        message.setRetrievedContext(createMessageRequest.getRetrievedContext());

        ChatMessage savedMessage = messageRepository.save(message);

        // Prepare context from previous messages
        String context = chatSession.getChatMessages().stream()
                .map(ChatMessage::getContent)
                .reduce("", (acc, msg) -> acc + "\n" + msg);

        // Generate AI response
       // String aiResponse = aiService.generateResponse(createMessageRequest.getContent(), context);

        // Save AI response
       /* ChatMessage aiMessage = new ChatMessage();
        aiMessage.setChatSession(chatSession);
        aiMessage.setSender("AI");
        aiMessage.setContent(aiResponse);
        aiMessage.setRetrievedContext(context);
        ChatMessage aiSavedMessage = messageRepository.save(aiMessage); */

        return chatMessageMapper.toDto(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getSessionMessages(UUID sessionId) {
        log.info("Retrieving messages for session: {}", sessionId);

        if (!chatSessionService.sessionExists(sessionId)) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        return messageRepository.findByChatSessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(chatMessageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getSessionMessagesPaginated(UUID sessionId, Pageable pageable) {
        if (!chatSessionService.sessionExists(sessionId)) {
            throw new ResourceNotFoundException("Session not found with id: " + sessionId);
        }

        return messageRepository.findByChatSessionId(sessionId, pageable)
                .map(chatMessageMapper::toDto);
    }

}
