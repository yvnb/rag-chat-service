package com.spring.ragchatservice.controller;

import com.spring.ragchatservice.aspect.RateLimit;
import com.spring.ragchatservice.dto.APIResponse;
import com.spring.ragchatservice.dto.ChatMessageDTO;
import com.spring.ragchatservice.dto.CreateMessageRequest;
import com.spring.ragchatservice.dto.PageResponse;
import com.spring.ragchatservice.exception.InvalidPaginationParameterException;
import com.spring.ragchatservice.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat-sessions/{chatSessionId}/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Messages", description = "APIs for managing chat messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @PostMapping
    @Operation(summary = "Add a message to a chat session")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<ChatMessageDTO>> addMessage(
            @PathVariable UUID chatSessionId,
            @Valid @RequestBody CreateMessageRequest request) {

        ChatMessageDTO message = chatMessageService.addMessage(
                chatSessionId,
                request.getSender(),
                request.getContent(),
                request.getRetrievedContext()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>(HttpStatus.CREATED.value(), "Message added", message));
    }

    @GetMapping
    @Operation(summary = "Get paginated messages for a chat session")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<PageResponse<ChatMessageDTO>>> getMessages(
            @PathVariable UUID chatSessionId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {

        if (pageNumber < 0) {
            throw new InvalidPaginationParameterException("Page number must be greater than or equal to 0");
        }
        if (pageSize <= 0) {
            throw new InvalidPaginationParameterException("Page size must be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
        Page<ChatMessageDTO> page = chatMessageService.getSessionMessagesPaginated(chatSessionId, pageable);
        PageResponse<ChatMessageDTO> pageResponse = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );

        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Paginated messages retrieved", pageResponse));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all messages for a chat session")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<List<ChatMessageDTO>>> getAllMessages(@PathVariable UUID chatSessionId) {
        List<ChatMessageDTO> messages = chatMessageService.getSessionMessages(chatSessionId);
        return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "All messages retrieved", messages));
    }
}
