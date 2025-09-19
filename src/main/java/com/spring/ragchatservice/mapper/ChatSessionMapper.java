package com.spring.ragchatservice.mapper;

import com.spring.ragchatservice.dto.ChatSessionDTO;
import com.spring.ragchatservice.dto.CreateSessionRequest;
import com.spring.ragchatservice.model.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper extends BaseMapper<ChatSessionDTO, ChatSession> {

    ChatSessionMapper INSTANCE = Mappers.getMapper(ChatSessionMapper.class);

    @Override
    ChatSession toEntity(ChatSessionDTO dto);

    @Override
    ChatSessionDTO toDto(ChatSession entity);

    @Mapping(target = "id", ignore = true)       // auto-generated
    @Mapping(target = "createdAt", ignore = true) // handled by auditing
    @Mapping(target = "updatedAt", ignore = true) // handled by auditing
    @Mapping(target = "favorite", constant = "false") // default value
    ChatSession toEntity(CreateSessionRequest request);
}
