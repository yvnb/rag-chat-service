-- create chat sessions and chat messages tables
CREATE TABLE chat_sessions
(
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_title UNIQUE (user_id, title)
);

CREATE TABLE chat_messages
(
   id UUID PRIMARY KEY,
   session_id UUID NOT NULL,
   sender VARCHAR(50) NOT NULL,
   content TEXT NOT NULL,
   retrieved_context TEXT,
   created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_session FOREIGN KEY (session_id) REFERENCES chat_sessions (id) ON DELETE CASCADE
);

-- add indexes for faster retrieval
CREATE INDEX idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX idx_chat_messages_created_at ON chat_messages(created_at);






