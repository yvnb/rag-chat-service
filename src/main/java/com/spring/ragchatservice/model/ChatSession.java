package com.spring.ragchatservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="chat_sessions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSession extends BaseEntity{

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_favorite", nullable = false)
    private boolean favorite = false;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages;

}
