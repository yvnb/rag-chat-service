package com.spring.ragchatservice.controller;

import com.spring.ragchatservice.aspect.RateLimit;
import com.spring.ragchatservice.dto.*;
import com.spring.ragchatservice.exception.InvalidPaginationParameterException;
import com.spring.ragchatservice.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat-sessions")
@RequiredArgsConstructor
@Tag(name = "Chat Sessions", description = "Chat session management APIs")
@Slf4j
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @PostMapping
    @Operation(summary = "Create a new chat session")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<ChatSessionDTO>> createSession(
            @Valid @RequestBody CreateSessionRequest createSessionRequest) {

        ChatSessionDTO session = chatSessionService.createChatSession(createSessionRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(session, "Chat session created successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all sessions for a user")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<List<ChatSessionDTO>>> getUserSessions(@PathVariable String userId) {
        List<ChatSessionDTO> sessions = chatSessionService.getUserSessions(userId);
        return ResponseEntity.ok(APIResponse.success(sessions, "User sessions retrieved successfully"));
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Get paginated sessions for a user")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<PageResponse<ChatSessionDTO>>> getPaginatedUserSessions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {

        if (pageNumber < 0) {
            throw new InvalidPaginationParameterException("Page number must be greater than or equal to 0");
        }
        if (pageSize <= 0) {
            throw new InvalidPaginationParameterException("Page size must be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());

        Page<ChatSessionDTO> pageSessions = chatSessionService.getPaginatedUserSessions(userId, pageable);
        PageResponse<ChatSessionDTO> response = new PageResponse<>(
                pageSessions.getContent(),
                pageSessions.getNumber(),
                pageSessions.getSize(),
                pageSessions.getTotalElements(),
                pageSessions.getTotalPages(),
                pageSessions.isLast()
        );

        return ResponseEntity.ok(APIResponse.success(response, "Paginated user sessions retrieved successfully"));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get session by ID")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<ChatSessionDTO>> getSession(@PathVariable UUID sessionId) {
        ChatSessionDTO session = chatSessionService.getSessionById(sessionId);
        return ResponseEntity.ok(APIResponse.success(session, "Chat session retrieved successfully"));
    }

    @PatchMapping("/{sessionId}/title")
    @Operation(summary = "Update session title")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<ChatSessionDTO>> updateTitle(
            @PathVariable UUID sessionId,
            @Valid @RequestBody UpdateTitleRequest updateTitleRequest) {

        ChatSessionDTO session = chatSessionService.updateSessionTitle(sessionId, updateTitleRequest.getNewTitle());
        return ResponseEntity.ok(APIResponse.success(session, "Chat session title updated successfully"));
    }

    @PatchMapping("/{sessionId}/favorite")
    @Operation(summary = "Toggle favorite status")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<ChatSessionDTO>> toggleFavorite(@PathVariable UUID sessionId) {
        ChatSessionDTO session = chatSessionService.toggleFavorite(sessionId);
        return ResponseEntity.ok(APIResponse.success(session, "Chat session favorite status toggled successfully"));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Delete a chat session")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<Void>> deleteSession(@PathVariable UUID sessionId) {
        chatSessionService.deleteSession(sessionId);
        return ResponseEntity.ok(APIResponse.success(null, "Chat session deleted successfully"));
    }

    @GetMapping("/user/{userId}/favorites")
    @Operation(summary = "Retrieves user's favorite sessions")
    @RateLimit(capacity = 5, interval = 60)
    public ResponseEntity<APIResponse<List<ChatSessionDTO>>> getFavorites(@PathVariable String userId) {
        List<ChatSessionDTO> favorites = chatSessionService.getFavoriteSessions(userId);
        return ResponseEntity.ok(APIResponse.success(favorites, "User favorite sessions retrieved successfully"));
    }

}
